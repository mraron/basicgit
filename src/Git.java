import java.util.Vector;
public class Git {
    private final BlobStore blobStore;
    private Tree workingDirectory;
    private final Vector<Commit> commits;
    private AuthorConfig author;

    public Git(AuthorConfig author) {
        this.blobStore = new BlobStore();
        this.workingDirectory = new Tree();
        this.commits = new Vector<>();

        this.author = author;
    }

    public void add(String path, String contents) {
        workingDirectory = workingDirectory.addBlobAndCopy(path, blobStore.getBlob(contents));
    }
    public void remove(String path) throws Exception {
        workingDirectory = workingDirectory.removeBlobAndCopy(path);
    }

    public Commit getLastCommit() {
        if(commits.isEmpty()) {
            return null;
        }
        return commits.lastElement();
    }

    public void commit(String message) {
        Commit commit = new Commit(workingDirectory, author.toString(), message, getLastCommit());
        commits.add(commit);
    }

    public String log() {
        StringBuilder sb = new StringBuilder();
        for(Commit c : commits.reversed()) {
            sb.append(c.toString());
        }
        return sb.toString();
    }
}
