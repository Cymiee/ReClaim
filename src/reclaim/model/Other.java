package reclaim.model;

/** Any other lost item. Secret: a unique mark on it. */
public class Other extends Item {
    public static final String CATEGORY = "Other";

    private String description;

    private String distinguishingMark;

    public Other(String name, String locationFound, int dateFound,
                 String description, String distinguishingMark) {
        super(name, locationFound, dateFound);
        this.description = description;
        this.distinguishingMark = distinguishingMark;
    }

    public Other(String itemId, String name, String locationFound, int dateFound,
                 String status, String claimedBy,
                 String description, String distinguishingMark) {
        super(itemId, name, locationFound, dateFound, status, claimedBy);
        this.description = description;
        this.distinguishingMark = distinguishingMark;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getVerificationQuestion() {
        return "Describe the unique mark on the item (e.g. a sticker, scratch or engraving).";
    }

    @Override
    public boolean verifyClaim(String answer) {
        if (answer == null) {
            return false;
        }
        return distinguishingMark.trim().equalsIgnoreCase(answer.trim());
    }

    @Override
    public String toString() {
        return super.toString() + " | Description: " + description;
    }

    @Override
    public String toFileString() {
        return CATEGORY + "|" + commonFileFields() + "|" + toFileField(description)
                + "|" + toFileField(distinguishingMark);
    }
}
