package hu.mraron.basicgit.tests;

import hu.mraron.basicgit.AuthorConfig;
import hu.mraron.basicgit.Git;
import hu.mraron.basicgit.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class GitTest {
    AuthorConfig author;
    Date epoch;
    @BeforeEach
    void initAuthor() {
        author = new AuthorConfig("Test John", "john@testing.com");

        epoch = new Date();
        epoch.setTime(0);
    }

    @Test
    void commit() {
        Git oldGit = new Git(author);
        oldGit.add("testfile", "testcontents");

        oldGit.commit("Test message", epoch);

        Git hipGit = new Git(author);
        hipGit.add("testfile", "testcontents");
        hipGit.commit("Test message");

        assertNotEquals(oldGit.getLastCommit().getHash(), hipGit.getLastCommit().getHash());
    }

    @Test
    void multipleCommits() {
        Git git = new Git(author);
        git.add("file1", "contents1");
        git.commit("commit1");
        git.add("file2", "contents2");
        git.commit("commit2");
        git.add("file3", "contents3");
        git.commit("commit3");

        assertEquals(3, git.getLastCommit().root.getFiles().size());
        assertNull(git.getLastCommit().parent.parent.parent);
        assertEquals("commit2", git.getLastCommit().parent.message);
    }

    @Test
    void getLastCommit() {
        Git git = new Git(author);
        assertNull(git.getLastCommit());
    }

    @Test
    void getCommitByHash() {
        Git git = new Git(author);
        git.commit("commit1");
        String hash = git.getLastCommit().getHash();
        git.commit("commit2");
        assertEquals(hash, git.getCommitByHash(hash).getHash());
    }

    @Test
    void getCommitsByAuthor() {
        Git git = new Git(author);
        git.commit("commit1");
        git.commit("commit2");
        git.commit("commit3");
        git.setAuthor(new AuthorConfig("asd", "bsd"));
        git.commit("commit4");
        assertEquals(3, git.getCommitsByAuthor(author.name().substring(0, 3)).size());
    }

    @Test
    void sameBlob() {
        Git git = new Git(author);
        git.add("asd", "123");
        git.add("csd", "124");
        git.add("bsd", "123");
        git.commit("commit");
        assertSame(git.getLastCommit().root.getFile(new Path("asd")), git.getLastCommit().root.getFile(new Path("bsd")));
        assertNotSame(git.getLastCommit().root.getFile(new Path("asd")), git.getLastCommit().root.getFile(new Path("csd")));
    }
}