package reclaim.manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import reclaim.exceptions.ClaimVerificationException;
import reclaim.exceptions.InvalidInputException;
import reclaim.exceptions.ItemAlreadyClaimedException;
import reclaim.model.Book;
import reclaim.model.Electronics;
import reclaim.model.IDCard;
import reclaim.model.Item;
import reclaim.model.Other;

/** Stores all items and handles adding, searching, sorting, claiming and file saving/loading. */
public class ReClaimManager {
    private static final String ITEMS_FILE = "items.txt";
    private static final String CLAIMS_LOG = "claims_log.txt";

    private ArrayList<Item> items;
    private ArrayList<ClaimRecord> claimHistory;

    public ReClaimManager() {
        items = new ArrayList<Item>();
        claimHistory = new ArrayList<ClaimRecord>();
    }

    private class ClaimRecord {
        private String itemId;
        private String claimantName;
        private String claimDate;
        private boolean success;

        ClaimRecord(String itemId, String claimantName, String claimDate, boolean success) {
            this.itemId = itemId;
            this.claimantName = claimantName;
            this.claimDate = claimDate;
            this.success = success;
        }

        @Override
        public String toString() {
            String result;
            if (success) {
                result = "SUCCESS";
            } else {
                result = "FAILED";
            }
            return claimDate + " | " + itemId + " | " + claimantName + " | " + result;
        }
    }

    // synchronized: the main thread and AutoSaveThread share the same list
    public synchronized void addItem(Item item) throws InvalidInputException {
        if (items.contains(item)) {
            throw new InvalidInputException("An item with ID " + item.getItemId() + " already exists.");
        }
        items.add(item);
    }

    public ArrayList<Item> getAllItems() {
        // shallow copy, so callers cannot change our internal list
        return new ArrayList<Item>(items);
    }

    public Item findById(String id) throws InvalidInputException {
        for (Item item : items) {
            if (item.getItemId().equalsIgnoreCase(id.trim())) {
                return item;
            }
        }
        throw new InvalidInputException("No item found with ID \"" + id.trim() + "\".");
    }

    public ArrayList<Item> searchByCategory(String category) {
        ArrayList<Item> results = new ArrayList<Item>();
        for (Item item : items) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                results.add(item);
            }
        }
        return results;
    }

    public ArrayList<Item> search(String keyword) {
        ArrayList<Item> results = new ArrayList<Item>();
        String key = keyword.trim().toLowerCase();

        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            String name = item.getName().toLowerCase();
            String location = item.getLocationFound().toLowerCase();
            if (name.contains(key) || location.contains(key)) {
                results.add(item);
            }
        }
        return results;
    }

    public ArrayList<Item> sortedByDate() {
        ArrayList<Item> sorted = getAllItems();
        Collections.sort(sorted);
        return sorted;
    }

    public ArrayList<Item> sortedByName() {
        ArrayList<Item> sorted = getAllItems();
        Collections.sort(sorted, new Comparator<Item>() {
            @Override
            public int compare(Item a, Item b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
        return sorted;
    }

    public String getVerificationQuestion(String id)
            throws InvalidInputException, ItemAlreadyClaimedException {
        Item item = findById(id);
        if (item.isClaimed()) {
            throw new ItemAlreadyClaimedException("Item " + item.getItemId()
                    + " has already been returned to its owner.");
        }
        return item.getVerificationQuestion();
    }

    public synchronized void claimItem(String id, String answer, String claimant)
            throws InvalidInputException, ClaimVerificationException, ItemAlreadyClaimedException {
        Item item = findById(id);

        if (item.isClaimed()) {
            recordAttempt(item.getItemId(), claimant, false);
            throw new ItemAlreadyClaimedException("Item " + item.getItemId()
                    + " has already been returned to its owner.");
        }

        if (!item.verifyClaim(answer)) {
            recordAttempt(item.getItemId(), claimant, false);
            throw new ClaimVerificationException("That answer does not match our records for "
                    + item.getItemId() + ".");
        }

        item.markClaimed(claimant);
        recordAttempt(item.getItemId(), claimant, true);
    }

    private void recordAttempt(String itemId, String claimant, boolean success) {
        String now = new Date().toString();
        ClaimRecord attempt = new ClaimRecord(itemId, claimant, now, success);
        claimHistory.add(attempt);
        appendToClaimLog(attempt.toString());
    }

    private void appendToClaimLog(String line) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(CLAIMS_LOG, true);
            writer.write(line + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Warning: could not write to " + CLAIMS_LOG + ".");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Warning: could not close " + CLAIMS_LOG + ".");
                }
            }
        }
    }

    public String getClaimHistoryReport() {
        if (claimHistory.isEmpty()) {
            return "No claim attempts yet in this session.";
        }
        StringBuffer report = new StringBuffer();
        for (ClaimRecord attempt : claimHistory) {
            if (report.length() > 0) {
                report.append("\n");
            }
            report.append(attempt.toString());
        }
        return report.toString();
    }

    public static int parseDate(String ddmmyyyy) throws InvalidInputException {
        StringTokenizer st = new StringTokenizer(ddmmyyyy.trim(), "-");
        if (st.countTokens() != 3) {
            throw new InvalidInputException("Date must be in dd-mm-yyyy format, e.g. 27-11-2026.");
        }

        try {
            int day = Integer.parseInt(st.nextToken());
            int month = Integer.parseInt(st.nextToken());
            int year = Integer.parseInt(st.nextToken());

            if (year < 2000 || year > 2100) {
                throw new InvalidInputException("Year must be between 2000 and 2100.");
            }
            if (month < 1 || month > 12) {
                throw new InvalidInputException("Month must be between 1 and 12.");
            }
            if (day < 1 || day > daysInMonth(month, year)) {
                throw new InvalidInputException("Day " + day + " is not valid for month " + month + ".");
            }
            return year * 10000 + month * 100 + day;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Date must contain only numbers, e.g. 27-11-2026.");
        }
    }

    private static int daysInMonth(int month, int year) {
        if (month == 2) {
            boolean leapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            if (leapYear) {
                return 29;
            }
            return 28;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        return 31;
    }

    public static String validateText(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().length() == 0) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
        if (value.indexOf('|') >= 0) {
            throw new InvalidInputException(fieldName + " cannot contain the '|' character.");
        }
        return value.trim();
    }

    public synchronized void saveToFile() throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(ITEMS_FILE);
            for (Item item : items) {
                writer.write(item.toFileString() + System.lineSeparator());
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public synchronized void loadFromFile() throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(ITEMS_FILE));
        } catch (FileNotFoundException e) {
            loadSampleItems();
            return;
        }

        try {
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().length() > 0) {
                    try {
                        addItem(parseItem(line));
                    } catch (InvalidInputException e) {
                        // skip a damaged line and keep loading the rest
                    }
                }
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
    }

    private Item parseItem(String line) throws InvalidInputException {
        StringTokenizer st = new StringTokenizer(line, "|");
        try {
            String category = nextField(st);
            String id = nextField(st);
            String name = nextField(st);
            String location = nextField(st);
            int date = Integer.parseInt(nextField(st));
            String status = nextField(st);
            String claimedBy = nextField(st);

            if (!id.startsWith("RC-")) {
                throw new InvalidInputException("Bad item ID: " + id);
            }
            if (!status.equals(Item.UNCLAIMED) && !status.equals(Item.CLAIMED)) {
                throw new InvalidInputException("Bad status: " + status);
            }

            if (category.equals(Electronics.CATEGORY)) {
                return new Electronics(id, name, location, date, status, claimedBy,
                        nextField(st), nextField(st), nextField(st));
            } else if (category.equals(IDCard.CATEGORY)) {
                return new IDCard(id, name, location, date, status, claimedBy,
                        nextField(st), nextField(st));
            } else if (category.equals(Book.CATEGORY)) {
                return new Book(id, name, location, date, status, claimedBy,
                        nextField(st), nextField(st), nextField(st));
            } else if (category.equals(Other.CATEGORY)) {
                return new Other(id, name, location, date, status, claimedBy,
                        nextField(st), nextField(st));
            } else {
                throw new InvalidInputException("Unknown category: " + category);
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Bad number in line: " + line);
        }
    }

    private String nextField(StringTokenizer st) throws InvalidInputException {
        if (!st.hasMoreTokens()) {
            throw new InvalidInputException("A field is missing in items.txt.");
        }
        String value = st.nextToken();
        if (value.equals(Item.NONE)) {
            return "";
        }
        return value;
    }

    private void loadSampleItems() {
        items.add(new Electronics("Laptop", "Library 2nd floor", 20261120,
                "Dell", "Silver", "5CG2041XKZ"));
        items.add(new IDCard("Student ID card", "Main gate", 20261118,
                "BITS Student ID", "2024A7PS0123U"));
        items.add(new Book("Textbook", "Lecture Hall 3", 20261122,
                "Head First Java", "Kathy Sierra", "Rahul Mehta"));
        items.add(new Other("Water bottle", "Sports complex", 20261125,
                "Blue steel bottle", "cat sticker"));
        items.add(new Electronics("Wireless earbuds", "Cafeteria", 20261124,
                "Apple", "White", "H4KD92LM"));
        items.add(new Other("Keys", "Parking lot", 20261115,
                "Two keys on a red keychain", "Eiffel Tower keyring"));
    }
}
