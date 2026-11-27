package reclaim.model;

import reclaim.exceptions.ItemAlreadyClaimedException;

/** Base class for all lost items. Each subclass adds one private secret used to verify the owner. */
public abstract class Item implements Claimable, Comparable<Item> {
    public static final String UNCLAIMED = "UNCLAIMED";
    public static final String CLAIMED = "CLAIMED";
    public static final String NONE = "NONE";

    private static int counter = 0;

    private final String itemId;

    private String name;
    private String locationFound;
    private int dateFound;
    private String status;
    private String claimedBy;

    public Item(String name, String locationFound, int dateFound) {
        counter++;
        this.itemId = String.format("RC-%04d", counter);
        this.name = name;
        this.locationFound = locationFound;
        this.dateFound = dateFound;
        this.status = UNCLAIMED;
        this.claimedBy = "";
    }

    public Item(String itemId, String name, String locationFound, int dateFound,
                String status, String claimedBy) {
        this.itemId = itemId;
        this.name = name;
        this.locationFound = locationFound;
        this.dateFound = dateFound;
        this.status = status;
        this.claimedBy = claimedBy;

        int number = Integer.parseInt(itemId.substring(3));
        if (number > counter) {
            counter = number;
        }
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public int getDateFound() {
        return dateFound;
    }

    public String getStatus() {
        return status;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    @Override
    public void markClaimed(String claimantName) throws ItemAlreadyClaimedException {
        if (isClaimed()) {
            throw new ItemAlreadyClaimedException("Item " + itemId + " has already been claimed.");
        }
        status = CLAIMED;
        claimedBy = claimantName;
    }

    @Override
    public boolean isClaimed() {
        return status.equals(CLAIMED);
    }

    public abstract String getCategory();

    public abstract String getVerificationQuestion();

    @Override
    public abstract boolean verifyClaim(String answer);

    public abstract String toFileString();

    protected String commonFileFields() {
        return toFileField(itemId) + "|" + toFileField(name) + "|" + toFileField(locationFound)
                + "|" + dateFound + "|" + toFileField(status) + "|" + toFileField(claimedBy);
    }

    // StringTokenizer skips empty fields, so empty values are saved as NONE
    protected static String toFileField(String value) {
        if (value == null || value.trim().length() == 0) {
            return NONE;
        }
        return value;
    }

    public static String formatDate(int yyyymmdd) {
        int year = yyyymmdd / 10000;
        int month = (yyyymmdd / 100) % 100;
        int day = yyyymmdd % 100;
        return String.format("%02d-%02d-%04d", day, month, year);
    }

    @Override
    public int compareTo(Item other) {
        return this.dateFound - other.dateFound;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        Item other = (Item) obj;
        return this.itemId.equals(other.itemId);
    }

    @Override
    public int hashCode() {
        return itemId.hashCode();
    }

    @Override
    public String toString() {
        String statusText;
        if (isClaimed()) {
            statusText = CLAIMED + " by " + claimedBy;
        } else {
            statusText = UNCLAIMED;
        }
        return "[" + itemId + "] " + getCategory() + " - " + name
                + " | Found at: " + locationFound + " on " + formatDate(dateFound)
                + " | " + statusText;
    }
}
