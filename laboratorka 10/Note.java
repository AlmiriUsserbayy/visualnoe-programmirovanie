package kz.atu.lab10;

public class Note {
    private String title;
    private String body;
    private String category;

    public Note() {}

    public Note(String title, String body, String category) {
        this.title = title;
        this.body = body;
        this.category = category;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "[" + category + "] " + title;
    }
}
