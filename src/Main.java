public class Main {
    public static void main(String[] args) {
        Git git = new Git(new AuthorConfig("Áron Noszály", "noszalyaron4@gmail.com"));
        git.add(".gitignore", "asd.exe\n" +
                "teszt.class\n");
        git.commit("1337");
        git.add("hello.cpp", "cout<<\"asd\"<<\"\n\";");
        git.commit("asd stuff");
        System.out.println(git.log());
    }
}