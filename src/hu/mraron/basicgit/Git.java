package hu.mraron.basicgit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Git {
    private final BlobStore blobStore;
    private Tree workingDirectory;
    private final ArrayList<Commit> commits;
    private AuthorConfig author;
    private final HashMap<String, Commit> branches;
    private String currentBranch;
    public static final String DEFAULT_BRANCH = "master";
    public Git(AuthorConfig defaultAuthor) {
        this.blobStore = new BlobStore();
        this.workingDirectory = new Tree();
        this.commits = new ArrayList<>();
        this.currentBranch = Git.DEFAULT_BRANCH;
        this.branches = new HashMap<>();

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
        commit(message, null);
    }

    public void commit(String message, Date date) {
        Commit commit = new Commit(workingDirectory, author.toString(), message, getLastCommit(), date);
        commits.add(commit);

        switchBranch(this.currentBranch, commit);
    }

    public String getBranch() {
        return this.currentBranch;
    }

    public void switchBranch(String name) {
        if(branches.containsKey(name)) {
            switchBranch(name, branches.get(name));
        }else {
            switchBranch(name, getLastCommit());
        }
    }

    public void switchBranch(String name, Commit commit) {
        this.currentBranch = name;

        if(commit != null) {
            this.workingDirectory = commit.root;
        }
        branches.put(name, commit);
    }

    public void merge(String branch) throws CanNotMergeException {
        if(!branches.containsKey(branch)) {
            throw new IllegalArgumentException();
        }
        merge(branches.get(branch));
    }

    public void merge(Commit commit) throws CanNotMergeException {
        if(commit == null) {
            throw new IllegalArgumentException();
        }

        Commit currentCommit = commit;
        ArrayList<Commit> parentCommits = new ArrayList<>();
        while (currentCommit != null) {
            parentCommits.add(currentCommit);
            currentCommit = currentCommit.parent;
        }

        if(!parentCommits.contains(this.getLastCommit())) {
            throw new CanNotMergeException("can't fast-forward to "+commit.getHash().substring(0,6)+": divergent branches");
        }
        switchBranch(this.currentBranch, commit);
    }

    public Commit getLastCommit() {
        if(!branches.containsKey(currentBranch)) {
            return null;
        }
        return branches.get(currentBranch);
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
            ArrayList<String> branches = new ArrayList<>();
            for(String branch : this.branches.keySet()) {
                if(this.branches.get(branch) == c) {
                    if(!Objects.equals(branch, currentBranch)) {
                        branches.add(branch);
                    }else {
                        branches.add("HEAD -> "+branch);
                    }
                }
            }
            if(branches.isEmpty()) {
                sb.append(c.toStringWithMessage(""));
            }else {
                sb.append(c.toStringWithMessage(" (" + String.join(", ", branches) + ")"));
            }
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
