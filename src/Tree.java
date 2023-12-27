import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import java.util.*;
import java.util.stream.Collectors;

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

        Tree newTree = copy.subtrees.get(path.getTopDirName()).removeFileAndCopy(path.getRemainingPath());
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

    public String generateDiff(Tree newTree) {
       DiffRowGenerator generator = DiffRowGenerator.create()
               .showInlineDiffs(true)
               .inlineDiffByWord(true)
               .mergeOriginalRevised(true)
               .oldTag(starts -> (starts?"\u001B[31m":"\u001B[0m"))
               .newTag(starts -> (starts?"\u001B[32m":"\u001B[0m"))
               .lineNormalizer(s -> s)
               .build();

        HashSet<Path> allFiles = new HashSet<>();
        allFiles.addAll(getFiles());
        allFiles.addAll(newTree.getFiles());
        StringBuilder sb = new StringBuilder();
        for(Path p : allFiles) {
            Blob oldBlob = getFile(p), newBlob = newTree.getFile(p);
            if(oldBlob == newBlob) {
                continue ;
            }

            sb.append("File: ").append(p);
            String[] oldContent = (oldBlob == null ? new String[]{} : oldBlob.data.split("\n"));
            String[] newContent = (newBlob == null ? new String[]{} : newBlob.data.split("\n"));

            if(newBlob == null) {
               sb.append(" was deleted\n\n");
            }else {
                sb.append("\n").append(
                       generator.generateDiffRows(
                               Arrays.stream(oldContent).toList(),
                               Arrays.stream(newContent).toList()
                       ).stream().map(DiffRow::getOldLine).collect(Collectors.joining("\n"))
                ).append("\n\n");
           }
       }

       return sb.toString();
    }

}
