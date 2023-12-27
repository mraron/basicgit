import java.util.ArrayList;
import java.util.Vector;
public class Git {
    private final BlobStore blobStore;
    private Tree workingDirectory;
    private final ArrayList<Commit> commits;
    private AuthorConfig author;

    public Git(AuthorConfig defaultAuthor) {
        this.blobStore = new BlobStore();
        this.workingDirectory = new Tree();
        this.commits = new ArrayList<>();

        this.author = defaultAuthor;
    }

    public void add(String path, String contents) {
        workingDirectory = workingDirectory.addFileAndCopy(new Path(path), blobStore.getBlob(contents));
    }

    public void remove(String path) throws Exception {
        workingDirectory = workingDirectory.removeFileAndCopy(new Path(path));
    }

    public void setAuthor(AuthorConfig author) {
        this.author = author;
    }

    public Commit getLastCommit() {
        if(commits.isEmpty()) {
            return null;
        }
        return commits.getLast();
    }

    public void commit(String message) {
        Commit commit = new Commit(workingDirectory, author.toString(), message, getLastCommit(), null);
        commits.add(commit);
    }

    public String log() {
        StringBuilder sb = new StringBuilder();
        for(Commit c : commits.reversed()) {
            sb.append(c.toString());
        }
        return sb.toString();
    }

    public String listFiles() {
        StringBuilder sb = new StringBuilder();
        sb.append("Files in working directory:\n");
        for(Path p : workingDirectory.getFiles()) {
            sb.append("\t* ").append(p.toString()).append("\n");
        }
        return sb.append("\n").toString();
    }
}
