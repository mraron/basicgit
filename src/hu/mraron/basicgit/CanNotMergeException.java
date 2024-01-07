package hu.mraron.basicgit;

public class CanNotMergeException extends Exception {
    public CanNotMergeException(String msg) {
        super(msg);
    }
}
