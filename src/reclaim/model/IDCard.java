package reclaim.model;

/** A lost ID card. Secret: the ID number on the card. */
public class IDCard extends Item {
    public static final String CATEGORY = "IDCard";

    private String cardType;

    private String idNumber;

    public IDCard(String name, String locationFound, int dateFound,
                  String cardType, String idNumber) {
        super(name, locationFound, dateFound);
        this.cardType = cardType;
        this.idNumber = idNumber;
    }

    public IDCard(String itemId, String name, String locationFound, int dateFound,
                  String status, String claimedBy, String cardType, String idNumber) {
        super(itemId, name, locationFound, dateFound, status, claimedBy);
        this.cardType = cardType;
        this.idNumber = idNumber;
    }

    public String getCardType() {
        return cardType;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getVerificationQuestion() {
        return "What is the ID number printed on the card?";
    }

    @Override
    public boolean verifyClaim(String answer) {
        if (answer == null) {
            return false;
        }
        return idNumber.trim().equalsIgnoreCase(answer.trim());
    }

    @Override
    public String toString() {
        return super.toString() + " | Card type: " + cardType;
    }

    @Override
    public String toFileString() {
        return CATEGORY + "|" + commonFileFields() + "|" + toFileField(cardType)
                + "|" + toFileField(idNumber);
    }
}
