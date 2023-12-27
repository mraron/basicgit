package main.hu.mraron.basicgit;

public class Path {
    public static char PATH_SEPARATOR = '/';
    private final String path;

    public Path(String top, Path p) {
        this.path = top + PATH_SEPARATOR + p.toString();
    }
    public Path(String s) {
        for(String i : s.split("/")) {
            if(i.isEmpty()) {
                throw new IllegalArgumentException();
            }
        }

        this.path = s;
    }

    public boolean isFile() {
        return !path.contains(Character.toString(PATH_SEPARATOR));
    }

    public String getTopDirName() {
        int sepIndex = path.indexOf(PATH_SEPARATOR);
        return path.substring(0, sepIndex);
    }

    public Path getRemainingPath() {
        int sepIndex = path.indexOf(PATH_SEPARATOR);
        String remPath = path.substring(sepIndex+1);
        return new Path(remPath);
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(this.getClass() != obj.getClass()) return false;
        return this.path.equals(((Path) obj).path);
    }
}
