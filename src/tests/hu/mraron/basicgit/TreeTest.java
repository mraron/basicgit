package tests.hu.mraron.basicgit;

import main.hu.mraron.basicgit.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeTest {
    private Tree tree;
    @Test
    void getHash() {
        Tree one = new Tree(), other = new Tree();

        one = one.addFileAndCopy(new Path("a"), new Blob("b"));
        assertNotEquals(one.getHash(), other.getHash());

        other = other.addFileAndCopy(new Path("a"), new Blob("b"));
        assertEquals(one.getHash(), other.getHash());

        other = other.addFileAndCopy(new Path("b"), new Blob("b"));
        assertNotEquals(one.getHash(), other.getHash());

        //only filename different
        one = new Tree().addFileAndCopy(new Path("a"), new Blob("data"));
        other = new Tree().addFileAndCopy(new Path("b"), new Blob("data"));
        assertNotEquals(one.getHash(), other.getHash());

        //only data different
        one = new Tree().addFileAndCopy(new Path("a"), new Blob("data1"));
        other = new Tree().addFileAndCopy(new Path("a"), new Blob("data2"));
        assertNotEquals(one.getHash(), other.getHash());

        //subtrees with equal
        one = new Tree().addFileAndCopy(new Path("a/x"), new Blob("data1"));
        other = new Tree().addFileAndCopy(new Path("a/x"), new Blob("data1"));
        assertEquals(one.getHash(), other.getHash());

        //subtrees with extra dir
        one = new Tree().addFileAndCopy(new Path("a/x"), new Blob("data1"));
        one = one.addFileAndCopy(new Path("a/y/z"), new Blob("data1"));
        other = new Tree().addFileAndCopy(new Path("a/x"), new Blob("data1"));
        assertNotEquals(one.getHash(), other.getHash());

        //different order of adding
        one = new Tree().addFileAndCopy(new Path("a/x"), new Blob("data1"));
        one = one.addFileAndCopy(new Path("b/y"), new Blob("data2"));
        other = new Tree().addFileAndCopy(new Path("b/y"), new Blob("data2"));
        other = other.addFileAndCopy(new Path("a/x"), new Blob("data1"));
        assertEquals(one.getHash(), other.getHash());
    }
    @BeforeEach
    void init() {
        this.tree = new Tree();
    }
    @Test
    void testGetAndAddFile() {
        this.tree.addFileAndCopy(new Path("teszt.x"), new Blob("asd"));
        assertTrue(this.tree.getFiles().isEmpty());

        this.tree = this.tree.addFileAndCopy(new Path("teszt.x"), new Blob("asd"));
        Blob blob = this.tree.getFile(new Path("teszt.x"));
        assertEquals(blob.data, "asd");

        this.tree = this.tree.addFileAndCopy(new Path("teszt.y"), new Blob("bsd"));
        blob = this.tree.getFile(new Path("teszt.y"));
        assertEquals(blob.data, "bsd");

        // maybe unexpected that this works
        this.tree = this.tree.addFileAndCopy(new Path("teszt.x/ccc"), new Blob("csd"));
        blob = this.tree.getFile(new Path("teszt.x"));
        assertEquals(blob.data, "asd");
        blob = this.tree.getFile(new Path("teszt.x/ccc"));
        assertEquals(blob.data, "csd");
    }

    @Test
    void testChangingFile() {
        this.tree = this.tree.addFileAndCopy(new Path("file_to_change"), new Blob("old stuff"));
        assertEquals("old stuff", this.tree.getFile(new Path("file_to_change")).data);
        this.tree = this.tree.addFileAndCopy(new Path("file_to_change"), new Blob("new stuff"));
        assertEquals("new stuff", this.tree.getFile(new Path("file_to_change")).data);
    }

    @Test
    void removeFileAndCopy() {
        assertThrows(BlobNotFoundException.class, () -> {
            this.tree.removeFileAndCopy(new Path("teszt.x"));
        });

        this.tree = this.tree.addFileAndCopy(new Path("a"), new Blob("a"));
        this.tree = this.tree.addFileAndCopy(new Path("b"), new Blob("b"));
        this.tree = this.tree.addFileAndCopy(new Path("c"), new Blob("c"));

        assertDoesNotThrow(() -> {
            this.tree.removeFileAndCopy(new Path("a"));
        });
        assertDoesNotThrow(() -> {
            this.tree.removeFileAndCopy(new Path("a"));
        });

        assertDoesNotThrow(() -> {
            this.tree = this.tree.removeFileAndCopy(new Path("b"));
        });
        assertThrows(BlobNotFoundException.class, () -> {
            this.tree = this.tree.removeFileAndCopy(new Path("b"));
        });

        assertDoesNotThrow(() -> {
            this.tree.removeFileAndCopy(new Path("a"));
        });
        assertDoesNotThrow(() -> {
            this.tree.removeFileAndCopy(new Path("c"));
        });

        assertDoesNotThrow(() -> {
            this.tree = this.tree.removeFileAndCopy(new Path("a"));
        });
        assertDoesNotThrow(() -> {
            this.tree = this.tree.removeFileAndCopy(new Path("c"));
        });

        this.tree = this.tree.addFileAndCopy(new Path("a/a"), new Blob("a"));
        this.tree = this.tree.addFileAndCopy(new Path("a/b"), new Blob("a"));
        this.tree = this.tree.addFileAndCopy(new Path("a/c"), new Blob("a"));
        this.tree = this.tree.addFileAndCopy(new Path("b/a"), new Blob("a"));
        this.tree = this.tree.addFileAndCopy(new Path("b/b"), new Blob("a"));
        this.tree = this.tree.addFileAndCopy(new Path("b/c"), new Blob("a"));

        assertDoesNotThrow(() -> {
            this.tree = this.tree.removeFileAndCopy(new Path("b/b"));
        });
        assertEquals(5, this.tree.getFiles().size());
    }

    @Test
    void getFiles() {
        this.tree = this.tree.addFileAndCopy(new Path("a"), new Blob("a"));
        this.tree = this.tree.addFileAndCopy(new Path("a/b"), new Blob("b"));
        this.tree = this.tree.addFileAndCopy(new Path("a/b/a"), new Blob("b"));
        this.tree = this.tree.addFileAndCopy(new Path("a/c/b"), new Blob("b"));
        this.tree = this.tree.addFileAndCopy(new Path("a/b/c"), new Blob("b"));
        this.tree = this.tree.addFileAndCopy(new Path("a/c/a"), new Blob("b"));
        this.tree = this.tree.addFileAndCopy(new Path("c/e"), new Blob("d"));
        this.tree = this.tree.addFileAndCopy(new Path("c/d"), new Blob("c"));

        //check the order of files
        assertArrayEquals(
                new String[]{
                        "a/b/a",
                        "a/b/c",
                        "a/c/a",
                        "a/c/b",
                        "a/b",
                        "c/d",
                        "c/e",
                        "a"
                }, this.tree.getFiles().stream().map(Path::toString).toArray()
        );
    }
}