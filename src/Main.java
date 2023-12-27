public class Main {
    public static void main(String[] args) {
        Git git = new Git(new AuthorConfig("Áron Noszály", "noszalyaron4@gmail.com"));
        git.add(".gitignore", """
                asd.exe
                teszt.class
                """);
        git.commit("1337");
        git.add("cppstuff/hello.cpp", "cout<<\"asd\"<<\"\n\";");
        git.add("javastuff/hello.java", "publicstaticvoidmain");
        git.commit("asd stuff");
        System.out.println(git.log());
        System.out.println(git.listFiles());
    }
}