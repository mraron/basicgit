public class Main {
    public static void main(String[] args) {
        Tree t = new Tree();
        t = t.addBlobAndCopy("asd.java", new Blob("1337"));
        Commit c = new Commit(t, "Áron Noszály <noszalyaron4@gmail.com>", "Stuff", null);
        System.out.println(c);
    }
}