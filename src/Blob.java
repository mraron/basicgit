public class Blob {
    public final String data;
    public final String hash;

    public Blob(String data) {
        this.data = data;
        this.hash = SHA1.hash(data);
    }

    public String getHash() {
        return this.hash;
    }
}
