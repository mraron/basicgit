import java.util.TreeMap;

public class Tree {
    public static char PATH_SEPARATOR = '/';
    private final TreeMap<String, Tree> subtrees;
    private final TreeMap<String, Blob> files;

    public Tree() {
        this.subtrees = new TreeMap<>();
        this.files = new TreeMap<>();
    }

    public Tree(Tree orig) {
        this.subtrees = new TreeMap<>(orig.subtrees);
        this.files = new TreeMap<>(orig.files);
    }

    public String getHash() {
        StringBuilder sb = new StringBuilder();
        for(String name : this.subtrees.keySet()) {
            sb.append(this.subtrees.get(name).getHash());
        }
        for(String name : this.files.keySet()) {
            sb.append(this.files.get(name).getHash());
        }

        return SHA1.hash(sb.toString());
    }

    public Tree addBlob(String path, Blob blob) {
        Tree copy = new Tree(this);
        if(!path.contains(Character.toString(PATH_SEPARATOR))) {
            copy.files.put(path, blob);
            return copy;
        }

        int sepIndex = path.indexOf(PATH_SEPARATOR);
        String dirName = path.substring(0, sepIndex), remPath = path.substring(sepIndex+1);

        if(!copy.subtrees.containsKey(dirName)) {
            copy.subtrees.put(dirName, new Tree());
        }

        return copy.subtrees.get(dirName).addBlob(remPath, blob);
    }

    public Blob getBlob(String path) {
        if (!path.contains(Character.toString(PATH_SEPARATOR))) {
            if(this.files.containsKey(path)) {
                return this.files.get(path);
            }

            return null;
        }

        int sepIndex = path.indexOf(PATH_SEPARATOR);
        String dirName = path.substring(0, sepIndex), remPath = path.substring(sepIndex+1);

        if(!this.subtrees.containsKey(dirName)) {
            return null;
        }

        return this.subtrees.get(dirName).getBlob(remPath);
    }
    public Tree removeBlob(String path) throws BlobNotFoundException {
        Tree copy = new Tree(this);
        if(!path.contains(Character.toString(PATH_SEPARATOR))) {
            if(copy.files.containsKey(path)) {
                copy.files.remove(path);
                return copy;
            }

            throw new BlobNotFoundException("file not found: " + path);
        }

        int sepIndex = path.indexOf(PATH_SEPARATOR);
        String dirName = path.substring(0, sepIndex), remPath = path.substring(sepIndex+1);

        if(!copy.subtrees.containsKey(dirName)) {
            throw new BlobNotFoundException("tree not found: " + dirName);
        }

        return copy.removeBlob(remPath);
    }
}
