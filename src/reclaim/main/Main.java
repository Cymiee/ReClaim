package reclaim.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import reclaim.exceptions.ClaimVerificationException;
import reclaim.exceptions.InvalidInputException;
import reclaim.exceptions.ItemAlreadyClaimedException;
import reclaim.manager.AutoSaveThread;
import reclaim.manager.ReClaimManager;
import reclaim.model.Book;
import reclaim.model.Electronics;
import reclaim.model.IDCard;
import reclaim.model.Item;
import reclaim.model.Other;

/** Console menu for ReClaim. Reads input, calls the manager and prints results. */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ReClaimManager manager = new ReClaimManager();

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("   ReClaim - Campus Lost & Found System");
        System.out.println("==============================================");

        try {
            manager.loadFromFile();
            System.out.println("Loaded " + manager.getAllItems().size() + " items.");
        } catch (IOException e) {
            System.out.println("Could not read saved items (" + e.getMessage()
                    + "). Starting with an empty list.");
        }

        AutoSaveThread autoSave = new AutoSaveThread(manager);
        autoSave.start();
        System.out.println("Auto-save is on (every 30 seconds).");

        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = readChoice("Enter your choice (1-8): ", 1, 8);
                switch (choice) {
                    case 1:
                        reportFoundItem();
                        break;
                    case 2:
                        printItems("All items (oldest first)", manager.sortedByDate());
                        break;
                    case 3:
                        searchByCategory();
                        break;
                    case 4:
                        searchByKeyword();
                        break;
                    case 5:
                        claimItem();
                        break;
                    case 6:
                        System.out.println("\n--- Claim history (this session) ---");
                        System.out.println(manager.getClaimHistoryReport());
                        break;
                    case 7:
                        printItems("All items (sorted by name)", manager.sortedByName());
                        break;
                    case 8:
                        manager.saveToFile();
                        System.out.println("All items saved to items.txt. Goodbye!");
                        running = false;
                        break;
                    default:
                        break;
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (ClaimVerificationException e) {
                System.out.println("Claim rejected: " + e.getMessage()
                        + " If this is your item, please visit the Lost & Found desk with proof.");
            } catch (ItemAlreadyClaimedException e) {
                System.out.println("Cannot claim: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Save failed: " + e.getMessage() + ". Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("---------------- MAIN MENU ----------------");
        System.out.println("1. Report found item");
        System.out.println("2. View all items");
        System.out.println("3. Search by category");
        System.out.println("4. Search by keyword");
        System.out.println("5. Claim an item");
        System.out.println("6. View claim history");
        System.out.println("7. View items sorted by name");
        System.out.println("8. Save and exit");
    }

    private static void reportFoundItem() throws InvalidInputException {
        System.out.println("\n--- Report a found item ---");
        int category = chooseCategory();
        String name = readLine("Item name (e.g. Laptop): ", "Item name");
        String location = readLine("Where was it found? ", "Location");
        int date = ReClaimManager.parseDate(readLine("Date found (dd-mm-yyyy): "));

        Item item;
        if (category == 1) {
            String brand = readLine("Brand: ", "Brand");
            String colour = readLine("Colour: ", "Colour");
            String serial = readLine("SECRET - serial number (kept private): ", "Serial number");
            item = new Electronics(name, location, date, brand, colour, serial);
        } else if (category == 2) {
            String cardType = readLine("Card type (e.g. BITS Student ID): ", "Card type");
            String idNumber = readLine("SECRET - ID number on the card (kept private): ", "ID number");
            item = new IDCard(name, location, date, cardType, idNumber);
        } else if (category == 3) {
            String title = readLine("Title: ", "Title");
            String author = readLine("Author: ", "Author");
            String nameInside = readLine("SECRET - name written inside the cover (kept private): ",
                    "Name inside");
            item = new Book(name, location, date, title, author, nameInside);
        } else {
            String description = readLine("Short description: ", "Description");
            String mark = readLine("SECRET - a unique mark on it (kept private): ", "Unique mark");
            item = new Other(name, location, date, description, mark);
        }

        manager.addItem(item);
        System.out.println("Item logged with ID " + item.getItemId()
                + ". Thank you for handing it in!");
    }

    private static void searchByCategory() throws InvalidInputException {
        System.out.println("\n--- Search by category ---");
        int choice = chooseCategory();
        String category;
        if (choice == 1) {
            category = Electronics.CATEGORY;
        } else if (choice == 2) {
            category = IDCard.CATEGORY;
        } else if (choice == 3) {
            category = Book.CATEGORY;
        } else {
            category = Other.CATEGORY;
        }
        printItems("Items in category " + category, manager.searchByCategory(category));
    }

    private static void searchByKeyword() throws InvalidInputException {
        System.out.println("\n--- Search by keyword ---");
        String keyword = readLine("Keyword (searches name and location): ", "Keyword");
        printItems("Items matching \"" + keyword + "\"", manager.search(keyword));
    }

    private static void claimItem()
            throws InvalidInputException, ClaimVerificationException, ItemAlreadyClaimedException {
        System.out.println("\n--- Claim an item ---");
        String id = readLine("Item ID (e.g. RC-0001): ", "Item ID");
        String question = manager.getVerificationQuestion(id);
        System.out.println("Verification question: " + question);
        String answer = readLine("Your answer: ");
        String claimant = readLine("Your full name: ", "Name");

        manager.claimItem(id, answer, claimant);
        System.out.println("Verified! " + claimant + ", please collect item "
                + id.trim().toUpperCase() + " from the Lost & Found desk.");
    }

    private static int chooseCategory() throws InvalidInputException {
        System.out.println("1. Electronics   2. ID card   3. Book   4. Other");
        return readChoice("Category (1-4): ", 1, 4);
    }

    private static void printItems(String heading, ArrayList<Item> list) {
        System.out.println("\n--- " + heading + " ---");
        if (list.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        for (Item item : list) {
            System.out.println(item);
        }
    }

    private static int readChoice(String prompt, int min, int max) throws InvalidInputException {
        String text = readLine(prompt).trim();
        int choice;
        try {
            choice = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("\"" + text + "\" is not a number. Please enter "
                    + min + " to " + max + ".");
        }
        if (choice < min || choice > max) {
            throw new InvalidInputException("Please enter a number from " + min + " to " + max + ".");
        }
        return choice;
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println("\nInput closed. Saving and exiting.");
            try {
                manager.saveToFile();
            } catch (IOException e) {
                System.out.println("Save failed: " + e.getMessage());
            }
            System.exit(0);
        }
        return scanner.nextLine();
    }

    private static String readLine(String prompt, String fieldName) throws InvalidInputException {
        return ReClaimManager.validateText(readLine(prompt), fieldName);
    }
}
