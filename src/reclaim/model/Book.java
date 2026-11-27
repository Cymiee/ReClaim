package reclaim.model;

/** A lost book. Secret: the name written inside the cover. */
public class Book extends Item {
    public static final String CATEGORY = "Book";

    private String title;
    private String author;

    private String nameInside;

    public Book(String name, String locationFound, int dateFound,
                String title, String author, String nameInside) {
        super(name, locationFound, dateFound);
        this.title = title;
        this.author = author;
        this.nameInside = nameInside;
    }

    public Book(String itemId, String name, String locationFound, int dateFound,
                String status, String claimedBy,
                String title, String author, String nameInside) {
        super(itemId, name, locationFound, dateFound, status, claimedBy);
        this.title = title;
        this.author = author;
        this.nameInside = nameInside;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getVerificationQuestion() {
        return "What name is written inside the front cover?";
    }

    @Override
    public boolean verifyClaim(String answer) {
        if (answer == null) {
            return false;
        }
        return nameInside.trim().equalsIgnoreCase(answer.trim());
    }

    @Override
    public String toString() {
        return super.toString() + " | Title: " + title + ", Author: " + author;
    }

    @Override
    public String toFileString() {
        return CATEGORY + "|" + commonFileFields() + "|" + toFileField(title)
                + "|" + toFileField(author) + "|" + toFileField(nameInside);
    }
}
