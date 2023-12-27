import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Tree {
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

    public Tree addFileAndCopy(Path path, Blob blob) {
        Tree copy = new Tree(this);
        if(path.isFile()) {
            copy.files.put(path.toString(), blob);
            return copy;
        }

        if(!copy.subtrees.containsKey(path.getTopDirName())) {
            copy.subtrees.put(path.getTopDirName(), new Tree());
        }

        Tree newTree = copy.subtrees.get(path.getTopDirName()).addFileAndCopy(path.getRemainingPath(), blob);
        copy.subtrees.put(path.getTopDirName(), newTree);
        return copy;
    }
    public Blob getFile(Path path) {
        if (path.isFile()) {
            if(this.files.containsKey(path.toString())) {
                return this.files.get(path.toString());
            }

            return null;
        }

        if(!this.subtrees.containsKey(path.getTopDirName())) {
            return null;
        }

        return this.subtrees.get(path.getTopDirName()).getFile(path.getRemainingPath());
    }
    public Tree removeFileAndCopy(Path path) throws BlobNotFoundException {
        Tree copy = new Tree(this);
        if(path.isFile()) {
            if(copy.files.containsKey(path.toString())) {
                copy.files.remove(path.toString());
                return copy;
            }

            throw new BlobNotFoundException("file not found: " + path);
        }

        if(!copy.subtrees.containsKey(path.getTopDirName())) {
            throw new BlobNotFoundException("tree not found: " + path.getTopDirName());
        }

        Tree newTree = copy.removeFileAndCopy(path.getRemainingPath());
        copy.subtrees.put(path.getTopDirName(), newTree);
        return copy;
    }

    public List<Path> getFiles() {
        List<Path> result = new ArrayList<>();
        for(String dir : subtrees.keySet()) {
            for(Path path : subtrees.get(dir).getFiles()) {
                result.add(new Path(dir, path));
            }
        }
        for(String file : files.keySet()) {
            result.add(new Path(file));
        }
        return result;
    }

}
