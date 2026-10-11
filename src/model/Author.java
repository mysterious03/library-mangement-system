package model;

public class Author {
    private int authorId;
    private String authorName;
    private String biography;

    public Author() {}

    public Author(int authorId, String authorName, String biography) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.biography = biography;
    }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
}
