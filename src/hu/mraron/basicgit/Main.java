package hu.mraron.basicgit;

import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // to make the output consistent
        Date date1 = new Date(), date2 = new Date(), date3 = new Date(), date4 = new Date();
        date1.setTime(100000);
        date2.setTime(200000);
        date3.setTime(300000);
        date4.setTime(400000);

        Git git = new Git(new AuthorConfig("Áron Noszály", "noszalyaron4@gmail.com"));
        git.add(".gitignore", """
                asd.exe
                teszt.class
                """);
        git.commit("1337", date1);
        git.add("cppstuff/hello.cpp", "cout<<\"asd\"<<\"\\n\";");
        git.add("javastuff/hello.java", "publicstaticvoidmain");
        git.commit("asd stuff", date2);

        try {
            git.remove("cppstuff/hello.cpp");
            git.remove("javastuff/hello.java");
            git.add(".gitignore", """
                    .exe
                    lol.class
                    """);
        }catch (Exception ignored) {}
        git.commit("remove hello.cpp", date3);
        git.switchBranch("anotherBranch1");

        System.out.println(git.log());
        System.out.println(git.listFiles());

        List<Commit> commits = git.getCommitsByAuthor("Áron");
        System.out.println(git.diffCommits(commits.get(0), commits.get(1)));
        System.out.println(git.diffCommits(commits.get(1), commits.get(2)));

        git.add("newfile", "interesting contents");
        System.out.println(git.status());
        git.setAuthor(new AuthorConfig("Interesting Author", "interesting@author.dev"));
        git.commit("Interesting message", date4);
        git.switchBranch("anotherBranch2");

        System.out.println(git.log());
    }
}