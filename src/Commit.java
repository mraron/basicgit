import java.util.Date;

public class Commit {
    private final Tree root;
    private final String author;
    private final String message;
    private final Date committed;
    private final Commit parent;

    public Commit(Tree root, String author, String message, Commit parent) {
        this.root = root;
        this.author = author;
        this.message = message;
        this.parent = parent;

        this.committed = new Date();
    }
    @Override
    public String toString() {
        return "commit " + getHash() + "\n" +
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
