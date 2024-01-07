package hu.mraron.basicgit.tests;

import hu.mraron.basicgit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

abstract class BaseGitTest {
    AuthorConfig author;
    Date epoch;
    @BeforeEach
    void initAuthor() {
        author = new AuthorConfig("Test John", "john@testing.com");

        epoch = new Date();
        epoch.setTime(0);
    }
}
class GitTest extends BaseGitTest {
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
        assertSame(git.getLastCommit().root.getFile("asd"), git.getLastCommit().root.getFile("bsd"));
        assertNotSame(git.getLastCommit().root.getFile("asd"), git.getLastCommit().root.getFile("csd"));
    }
}

class GitBranchTest extends BaseGitTest {
    @Test
    void defaultBranch() {
        Git git = new Git(author);
        assertSame(Git.DEFAULT_BRANCH, git.getBranch());
    }
    @Test
    void createBranch() {
        Git git = new Git(author);
        git.commit("empty commit", epoch);

        git.switchBranch("main");
        Commit c1 = git.getLastCommit();
        assertEquals(git.getBranch(), "main");
        git.switchBranch(Git.DEFAULT_BRANCH);
        Commit c2 = git.getLastCommit();
        assertEquals(c1, c2);
    }
    @Test
    void divergentBranchesParent() {
        Git git = new Git(author);
        git.commit("init", epoch);
        git.switchBranch("main");
        git.commit("commit", epoch);

        Commit c1 = git.getLastCommit();
        git.switchBranch(Git.DEFAULT_BRANCH);
        Commit c2 = git.getLastCommit();
        assertNotEquals(c1, c2);
        assertEquals(c1.parent, c2);
    }

    @Test
    void divergentBranchesCommonAncestor() {
        Git git = new Git(author);
        git.commit("init");

        git.commit("lca");
        Commit commitLCA = git.getLastCommit();

        git.switchBranch("other_branch");
        git.commit("case1");
        Commit c1 = git.getLastCommit();

        git.switchBranch(Git.DEFAULT_BRANCH);
        git.commit("case2");
        Commit c2 = git.getLastCommit();

        assertNotEquals(c1, c2);
        assertEquals(commitLCA, c1.parent);
        assertEquals(commitLCA, c2.parent);

        git.commit("forward2");
        c2 = git.getLastCommit();
        git.switchBranch("other_branch");
        git.commit("forward1");
        c1 = git.getLastCommit();

        assertNotEquals(c1, c2);
        assertEquals(commitLCA, c1.parent.parent);
        assertEquals(commitLCA, c2.parent.parent);
    }

    @Test
    void divergentBranchesSlowFast() {
        Git git = new Git(author);
        git.commit("init");
        git.switchBranch("slow");
        git.switchBranch(Git.DEFAULT_BRANCH);
        git.commit("commit1");
        git.commit("commit2");
        git.commit("commit3");
        git.commit("commit4");

        git.switchBranch("slow");
        Commit initCommit = git.getLastCommit();

        git.switchBranch(Git.DEFAULT_BRANCH);
        assertEquals(git.getLastCommit().parent.parent.parent.parent, initCommit);
    }

    @Test
    void workingDirectoryIsEmptied() {
        Git git = new Git(author);
        git.commit("init");
        git.add("test_file", "test_contents");
        git.switchBranch("test");
        git.commit("test_commit");
        assertNull(git.getLastCommit().root.getFile("test_file"));
    }
    @Test
    void divergentContents() {
        Git git = new Git(author);
        git.commit("init");
        git.switchBranch("other");
        git.add("a", "a");
        git.commit("commit1");
        Commit c1 = git.getLastCommit();

        git.switchBranch(Git.DEFAULT_BRANCH);
        git.add("a", "b");
        git.commit("commit2");
        Commit c2 = git.getLastCommit();

        assertEquals(c1.root.getFile("a").data, "a");
        assertEquals(c2.root.getFile("a").data, "b");
    }

    @Test
    void divergentExistenceOfFile() {
        Git git = new Git(author);
        git.commit("init");
        git.add("a", "a");
        git.commit("Add a");
        git.switchBranch("otherBranch");

        git.switchBranch(Git.DEFAULT_BRANCH);
        assertDoesNotThrow(() -> git.remove("a"));
        git.commit("Remove a");
        assertNull(git.getLastCommit().root.getFile("a"));

        git.switchBranch("otherBranch");
        git.add("b", "b");
        git.commit("add b");
        assertDoesNotThrow(() -> git.getLastCommit().root.getFile("a"));
        assertEquals(git.getLastCommit().root.getFile("b").data, "b");
    }
}

class GitDivergentBranchesTest extends BaseGitTest {
    public ArrayList<String> listParents;
    public ArrayList<String> listDivergences;
    public Commit initCommit;
    public Git git;

    @BeforeEach
    void initialize() {
        git = new Git(author);
        git.commit("init");

        initCommit = git.getLastCommit();

        listParents = new ArrayList<>();
        listDivergences = new ArrayList<>();

        listParents.add(git.getLastCommit().getHash());
        for(int i = 1; i <= 10; i++) {
            git.switchBranch("branch" + i);
            git.add("file", "curr"+i);
            git.commit("commit" + i);
            listParents.add(git.getLastCommit().getHash());
        }

        for(int i = 1; i <= 10; i++) {
            git.switchBranch("branch" + i);
            git.add("divergent_file", "curr"+i);
            git.commit("divergence" + i);
            listDivergences.add(git.getLastCommit().getHash());
        }
    }
    @Test
    void commitTree() {
        for(int i = 1; i <= 10; i++) {
            Commit current = git.getCommitByHash(listDivergences.get(i-1));
            for(int j = 0; j < i+1; j++) {
                current = current.parent;
                if(j == 0) {
                    assertEquals(current, git.getCommitByHash(listParents.get(i)));
                }
            }
            assertEquals(current, initCommit);
        }
    }
    @Test
    void mergeFastForward10() {
        git.switchBranch("toFastForward", git.getCommitByHash(listParents.get(1)));
        assertDoesNotThrow(() -> git.merge("branch10"));
        assertEquals(git.getBranch(), "toFastForward");
        assertEquals(git.getLastCommit().getHash(), listDivergences.getLast());
        assertEquals(git.getLastCommit().root.getFile("divergent_file").data, "curr10");
        assertEquals(git.getLastCommit().root.getFile("file").data, "curr10");
    }
    @Test
    void mergeFastForward5() {
        git.switchBranch("toFastForward", git.getCommitByHash(listParents.get(1)));
        assertDoesNotThrow(() -> git.merge("branch5"));
    }
    @Test
    void mergeDivergentCommitDivergent1() {
        git.switchBranch("toFastForward", git.getCommitByHash(listParents.get(5)));
        assertThrows(CanNotMergeException.class, () -> git.merge(git.getCommitByHash(listDivergences.getFirst())));
    }
    @Test
    void mergeDivergentBranchDivergent1() {
        git.switchBranch("divergence", git.getCommitByHash(listDivergences.getFirst()));
        git.switchBranch("toFastForward", git.getCommitByHash(listParents.get(5)));
        assertThrows(CanNotMergeException.class, () -> git.merge("divergence"));

        git.switchBranch("divergence", git.getCommitByHash(listParents.get(5)));
        git.switchBranch("toFastForward", git.getCommitByHash(listDivergences.getFirst()));
        assertThrows(CanNotMergeException.class, () -> git.merge("divergence"));
    }
}