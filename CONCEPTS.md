# ReClaim: OOP Concepts Used

Every concept below is also marked in the source code with a comment in the form
`// CONCEPT: <name> - <explanation>`. Search the code for `CONCEPT:` to find each one.

## Classes, objects and constructors

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| Class and object | `Electronics`, `IDCard`, `Book`, `Other` | `Main.reportFoundItem()` | `new Electronics(...)` creates an object from the class blueprint. |
| Constructor | `Item` | `Item(name, location, dateFound)` | Sets up a new item's fields and gives it a fresh ID. |
| Constructor overloading | `Item` + all 4 subclasses | two constructors each | One constructor for a newly found item, another for an item loaded from `items.txt`. |
| Method overloading | `Main` | `readLine(String)` / `readLine(String, String)` | Same name, different parameters: raw read vs. read-and-validate. |
| Static field | `Item` | `counter` | One counter shared by all items, so every ID is unique. |
| Static method | `ReClaimManager`, `Item` | `parseDate()`, `validateText()`, `formatDate()` | Called on the class (`ReClaimManager.parseDate(...)`) without creating an object. |
| `final` field | `Item` | `itemId` | An item's ID is set once in the constructor and can never change. |
| `static final` constants | `Item`, subclasses | `UNCLAIMED`, `CLAIMED`, `NONE`, `CATEGORY` | Named constants used in place of enums. |
| Accessors (getters) | `Item`, subclasses | `getItemId()`, `getName()`, `getBrand()`, ... | Give read-only access to private fields. |
| Controlled mutator | `Item` | `markClaimed()` (no `setStatus()`) | Status can only change through one method that enforces the rules. |
| `String.format` | `Item` | constructor, `formatDate()` | `"RC-%04d"` pads 7 to `RC-0007`; `%02d` pads day/month. |

## Encapsulation and access

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| Encapsulation | `Item` | all fields `private` | Data can only be reached through the class's own methods. |
| Data hiding (secret, no getter) | `Electronics`, `IDCard`, `Book`, `Other` | `serialNumber`, `idNumber`, `nameInside`, `distinguishingMark` | Only `verifyClaim()` and `toFileString()` read the secret; nobody can fetch it. |
| `private` / `protected` / `public` | `Item`, `ReClaimManager` | `commonFileFields()` is `protected`; `ClaimRecord` is `private` | Each member is visible only to the code that needs it. |
| Packages and `import` | all | `reclaim.model`, `reclaim.exceptions`, `reclaim.manager`, `reclaim.main` | Related classes are grouped; other packages use them via `import`. |

## Inheritance and polymorphism

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| Inheritance | `Electronics`, `IDCard`, `Book`, `Other` | `extends Item` | Each subclass reuses Item's fields and methods and adds its own. |
| `super(...)` | all subclasses | constructors | Calls the parent constructor first so the shared fields are set up. |
| `super.method()` | all subclasses | `toString()` | Reuses Item's summary line, then appends the subclass's own details. |
| Abstract class | `Item` | `public abstract class Item` | "Item" is too general to create; only concrete subclasses are created. |
| Abstract methods | `Item` | `getCategory()`, `getVerificationQuestion()`, `verifyClaim()`, `toFileString()` | Declared without a body; every subclass must provide one. |
| Method overriding | all subclasses, `AutoSaveThread` | `verifyClaim()`, `toString()`, `run()`, ... | A subclass replaces an inherited method with its own version. |
| Polymorphism (dynamic dispatch) | `ReClaimManager` | `claimItem()` calls `item.verifyClaim(answer)` | The same call runs Electronics', IDCard's, Book's or Other's version at run time. |
| Polymorphism (parent reference) | `Main` | `reportFoundItem()`: `Item item = new Book(...)` | An `Item` variable can hold any subclass object. |
| Polymorphism via `toString()` | `Main` | `printItems()`: `System.out.println(item)` | `println` automatically calls the subclass's overridden `toString()`. |

## Interfaces, sorting and inner classes

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| Interface | `Claimable` | `verifyClaim()`, `markClaimed()`, `isClaimed()` | A contract saying what a claimable thing can do, not how. |
| Implementing an interface | `Item` | `implements Claimable, Comparable<Item>` | Item promises to provide every interface method. |
| `Comparable` | `Item` | `compareTo()` | Defines Item's natural order: by date found. |
| `Collections.sort(list)` | `ReClaimManager` | `sortedByDate()` | Sorts using `compareTo()`, oldest item first. |
| `Comparator` | `ReClaimManager` | `sortedByName()` | A second sort order (by name) without changing `compareTo()`. |
| Anonymous class | `ReClaimManager` | `sortedByName()`: `new Comparator<Item>() { ... }` | A one-off unnamed class written exactly where it is needed. |
| Inner class (non-static) | `ReClaimManager` | `ClaimRecord` | A class inside a class; each record belongs to the manager object that created it. |

## The Object class

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| Overriding `toString()` | `Item`, subclasses | `toString()` | Gives a readable one-line summary instead of `Electronics@1b6d3586`. |
| Overriding `equals()` | `Item` | `equals()` | Two items are equal if their IDs match. |
| `instanceof` and casting | `Item` | `equals()` | Checks the object really is an `Item` before casting it. |
| Overriding `hashCode()` | `Item` | `hashCode()` | Equal items give equal hash codes (the equals/hashCode contract). |
| `equals()` used by collections | `ReClaimManager` | `addItem()`: `items.contains(item)` | `contains()` calls our `equals()` to reject duplicate IDs. |
| Shallow copy | `ReClaimManager` | `getAllItems()` | Returns a new list holding the same Item objects, protecting the internal list. |

## Strings and collections

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| `String` methods | subclasses, `ReClaimManager` | `trim()`, `equalsIgnoreCase()`, `substring()`, `contains()`, `toLowerCase()` | Compare answers ignoring case/spaces; take the last 4 serial characters. |
| `StringBuffer` | `ReClaimManager` | `getClaimHistoryReport()` | A changeable string, used to build a multi-line report efficiently. |
| `StringTokenizer` | `ReClaimManager` | `parseDate()`, `parseItem()` | Splits `"27-11-2026"` on `-` and file lines on `\|`. |
| `ArrayList` | `ReClaimManager` | `items`, `claimHistory` | A list that grows as items and claim records are added. |
| `Iterator` | `ReClaimManager` | `search(String keyword)` | Walks the list with `hasNext()` / `next()`. |
| `Scanner` | `Main` | `readLine()` | Reads each line the user types. |

## Exception handling

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| Custom checked exceptions | `reclaim.exceptions` | all three classes | Extend `Exception`, so the compiler forces callers to handle them. |
| `super(message)` | each exception | constructor | Passes the message to `Exception` so `getMessage()` returns it. |
| `throw` | `Item`, `ReClaimManager`, `Main` | `markClaimed()`, `claimItem()`, `findById()`, `parseDate()`, `readChoice()` | Signals a problem to the caller. |
| `throws` | `ReClaimManager`, `Main` | `claimItem() throws InvalidInputException, ClaimVerificationException, ItemAlreadyClaimedException` | Declares which checked exceptions a method may pass on. |
| `try` / multiple `catch` | `Main` | `main()` menu loop | Each custom exception gets its own friendly message; the program never crashes. |
| `finally` | `ReClaimManager` | `saveToFile()`, `loadFromFile()`, `appendToClaimLog()` | Always runs, so files are closed even if an error occurs. |
| Exception translation | `ReClaimManager`, `Main` | `parseDate()`, `readChoice()` | Catches Java's `NumberFormatException` and throws our clearer `InvalidInputException`. |

Where each custom exception is thrown and caught:

| Exception | Thrown in | Caught in |
|---|---|---|
| `InvalidInputException` | `findById`, `parseDate`, `validateText`, `addItem`, `parseItem` (manager); `readChoice` (Main) | `Main.main` (user actions), `ReClaimManager.loadFromFile` (damaged lines) |
| `ClaimVerificationException` | `ReClaimManager.claimItem` (wrong answer) | `Main.main` |
| `ItemAlreadyClaimedException` | `Item.markClaimed`, `ReClaimManager.getVerificationQuestion`, `ReClaimManager.claimItem` | `Main.main` |

## Threads and files

| Concept | Class | Method / location | One-line explanation |
|---|---|---|---|
| `Thread` (extends) | `AutoSaveThread` | `run()` | A second flow of execution that saves every 30 seconds. |
| `start()` vs `run()` | `Main` | `main()`: `autoSave.start()` | `start()` creates the new thread, which then calls `run()`. |
| Daemon thread | `AutoSaveThread` | constructor: `setDaemon(true)` | The program can exit without waiting for this thread. |
| `Thread.sleep` / `InterruptedException` | `AutoSaveThread` | `run()` | Pauses 30 seconds between saves; stops if interrupted. |
| `synchronized` | `ReClaimManager` | `addItem()`, `claimItem()`, `saveToFile()`, `loadFromFile()` | Only one thread at a time can change or save the shared list. |
| `FileWriter` (overwrite) | `ReClaimManager` | `saveToFile()` | Rewrites `items.txt` with the current items. |
| `FileWriter` (append mode) | `ReClaimManager` | `appendToClaimLog()`: `new FileWriter(CLAIMS_LOG, true)` | Adds a line to the end of `claims_log.txt` without erasing it. |
| `BufferedReader` + `FileReader` | `ReClaimManager` | `loadFromFile()` | Reads `items.txt` one line at a time with `readLine()`. |
| `FileNotFoundException` | `ReClaimManager` | `loadFromFile()` | No save file yet means first run, so sample items are loaded. |
| `IOException` | `ReClaimManager`, `Main`, `AutoSaveThread` | `saveToFile()`, `main()`, `run()` | Checked exception for file problems; Main reports it, auto-save stays silent. |
