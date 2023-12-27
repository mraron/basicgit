package main.hu.mraron.basicgit;

public record AuthorConfig(String name, String email) {
    @Override
    public String toString() {
        return name + " <"+email+">";
    }
}