package hu.mraron.basicgit;

import java.util.Date;

public class Commit {
    public final Tree root;
    public final String author;
    public final String message;
    public final Date committed;
    public final Commit parent;

    public Commit(Tree root, String author, String message, Commit parent, Date committed) {
        this.root = root;
        this.author = author;
        this.message = message;
        this.parent = parent;

        this.committed = committed == null ? new Date() : committed;
    }
    @Override
    public String toString() {
        return toStringWithMessage("");
    }

    public String toStringWithMessage(String message) {
        return "commit " + getHash() + message + "\n" +
                "Author:\t" + this.author + "\n" +
                "Date:\t" + this.committed + "\n" +
                "\n\t" + this.message + "\n\n";
    }

    public String getHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(root.getHash());
        sb.append(this.author);
        sb.append(this.message);
        sb.append(this.committed);
        if(this.parent != null) {
            sb.append(this.parent.getHash());
        }

        return SHA1.hash(sb.toString());
    }
}
