package hu.mraron.basicgit.tests;

import hu.mraron.basicgit.Blob;
import hu.mraron.basicgit.Commit;
import hu.mraron.basicgit.Path;
import hu.mraron.basicgit.Tree;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CommitTest {

    @Test
    void getHash() {
        Date epoch = new Date(); epoch.setTime(0);

        Commit commit1 = new Commit(new Tree(), "author", "message", null, epoch);
        Commit commit2;

        commit2 = new Commit(new Tree(), "author", "message", null, epoch);
        assertEquals(commit1.getHash(), commit2.getHash());

        commit2 = new Commit(new Tree(), "author2", "message", null, epoch);
        assertNotEquals(commit1.getHash(), commit2.getHash());

        commit2 = new Commit(new Tree(), "author", "message2", null, epoch);
        assertNotEquals(commit1.getHash(), commit2.getHash());

        commit2 = new Commit(new Tree(), "author", "message", commit1, epoch);
        assertNotEquals(commit1.getHash(), commit2.getHash());

        commit2 = new Commit(new Tree(), "author", "message", null, new Date());
        assertNotEquals(commit1.getHash(), commit2.getHash());

        commit2 = new Commit(
                new Tree().addFileAndCopy(new Path("a"), new Blob("x")),
                "author",
                "message",
                null,
                epoch
        );
        assertNotEquals(commit1.getHash(), commit2.getHash());
    }
}