package reclaim.model;

/** A lost electronic device. Secret: the serial number. */
public class Electronics extends Item {
    public static final String CATEGORY = "Electronics";

    private String brand;
    private String colour;

    private String serialNumber;

    public Electronics(String name, String locationFound, int dateFound,
                       String brand, String colour, String serialNumber) {
        super(name, locationFound, dateFound);
        this.brand = brand;
        this.colour = colour;
        this.serialNumber = serialNumber;
    }

    public Electronics(String itemId, String name, String locationFound, int dateFound,
                       String status, String claimedBy,
                       String brand, String colour, String serialNumber) {
        super(itemId, name, locationFound, dateFound, status, claimedBy);
        this.brand = brand;
        this.colour = colour;
        this.serialNumber = serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getColour() {
        return colour;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getVerificationQuestion() {
        return "What are the last 4 characters of the device's serial number?";
    }

    @Override
    public boolean verifyClaim(String answer) {
        if (answer == null) {
            return false;
        }
        String serial = serialNumber.trim();
        String lastFour;
        if (serial.length() < 4) {
            lastFour = serial;
        } else {
            lastFour = serial.substring(serial.length() - 4);
        }
        return lastFour.equalsIgnoreCase(answer.trim());
    }

    @Override
    public String toString() {
        return super.toString() + " | Brand: " + brand + ", Colour: " + colour;
    }

    @Override
    public String toFileString() {
        return CATEGORY + "|" + commonFileFields() + "|" + toFileField(brand)
                + "|" + toFileField(colour) + "|" + toFileField(serialNumber);
    }
}
