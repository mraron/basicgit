package main.hu.mraron.basicgit;

import java.util.ArrayList;
public class Git {
    private final BlobStore blobStore;
    private Tree workingDirectory;
    private final ArrayList<Commit> commits;
    private AuthorConfig author;

    public Git(AuthorConfig defaultAuthor) {
        this.blobStore = new BlobStore();
        this.workingDirectory = new Tree();
        this.commits = new ArrayList<>();

        this.setAuthor(defaultAuthor);
    }

    public void setAuthor(AuthorConfig author) {
        if(author == null) {
            throw new IllegalArgumentException();
        }
        this.author = author;
    }

    public void add(String path, String contents) {
        workingDirectory = workingDirectory.addFileAndCopy(new Path(path), blobStore.getBlob(contents));
    }

    public void remove(String path) throws Exception {
        workingDirectory = workingDirectory.removeFileAndCopy(new Path(path));
    }

    public void commit(String message) {
        Commit commit = new Commit(workingDirectory, author.toString(), message, getLastCommit(), null);
        commits.add(commit);
    }

    public Commit getLastCommit() {
        if(commits.isEmpty()) {
            return null;
        }
        return commits.getLast();
    }

    public Commit getCommitByHash(String hash) {
        for(Commit c : commits) {
            if(c.getHash().startsWith(hash)) {
                return c;
            }
        }

        return null;
    }

    public ArrayList<Commit> getCommitsByAuthor(String author) {
        ArrayList<Commit> result = new ArrayList<>();
        for(Commit c : commits) {
            if(c.author.contains(author)) {
                result.add(c);
            }
        }

        return result;
    }

    public String log() {
        StringBuilder sb = new StringBuilder();
        for(Commit c : commits.reversed()) {
            sb.append(c.toString());
        }
        return sb.toString();
    }

    public String status() {
        return "Diff of commit " + getLastCommit().getHash() + " and working directory\n" +
                getLastCommit().root.generateDiff(workingDirectory);
    }

    public String listFiles() {
        StringBuilder sb = new StringBuilder();
        sb.append("Files in working directory:\n");
        for(Path p : workingDirectory.getFiles()) {
            sb.append("\t* ").append(p.toString()).append("\n");
        }
        return sb.append("\n").toString();
    }

    public String diffCommits(Commit oldC, Commit newC) {
        return "Diff of commits " + oldC.getHash() + " and " + newC.getHash() + "\n" +
                oldC.root.generateDiff(newC.root);
    }
}
