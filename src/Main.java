import java.util.List;

public class Main {
    public static void main(String[] args) {
        Git git = new Git(new AuthorConfig("Áron Noszály", "noszalyaron4@gmail.com"));
        git.add(".gitignore", """
                asd.exe
                teszt.class
                """);
        git.commit("1337");
        git.add("cppstuff/hello.cpp", "cout<<\"asd\"<<\"\\n\";");
        git.add("javastuff/hello.java", "publicstaticvoidmain");
        git.commit("asd stuff");

        try {
            git.remove("cppstuff/hello.cpp");
            git.remove("javastuff/hello.java");
            git.add(".gitignore", """
                    .exe
                    lol.class
                    """);
        }catch (Exception ignored) {}
        git.commit("remove hello.cpp");

        System.out.println(git.log());
        System.out.println(git.listFiles());

        List<Commit> commits = git.getCommitsByAuthor("Áron");
        System.out.println(git.diffCommits(commits.get(0), commits.get(1)));
        System.out.println(git.diffCommits(commits.get(1), commits.get(2)));

        git.add("newfile", "interesting contents");
        System.out.println(git.status());
        git.setAuthor(new AuthorConfig("Interesting Author", "interesting@author.dev"));
        git.commit("Interesting message");

        System.out.println(git.log());
    }
}