public class Main {
    public static void main(String[] args) {
        Tree t = new Tree();
        t = t.addBlob("asd.java", new Blob("1337"));
        System.out.println(t.getBlob("asd.java"));
    }
}