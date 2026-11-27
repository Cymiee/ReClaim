# ReClaim: A Campus Lost & Found and Claim Verification System

CS F213 Object Oriented Programming mini project, BITS Pilani Dubai.

ReClaim is a console (menu-driven) Java program for a campus Lost & Found desk. When someone
hands in a found item, it is logged with **public details** (name, location, date, brand, ...)
and **one private secret** that only the real owner would know (serial number, ID number, the
name written inside a book, or a unique mark). To claim an item, the person must answer a
verification question about that secret.

## Project structure

```
ReClaim/
├── src/reclaim/
│   ├── model/        Claimable (interface), Item (abstract), Electronics, IDCard, Book, Other
│   ├── exceptions/   ClaimVerificationException, ItemAlreadyClaimedException, InvalidInputException
│   ├── manager/      ReClaimManager (with inner class ClaimRecord), AutoSaveThread
│   └── main/         Main
├── README.md         this file
├── CONCEPTS.md       concept -> class -> method table for the report
└── SAMPLE_RUN.md     a real console run of the program
```

## How to compile and run

You need a JDK (Java 8 or newer). Open a terminal **in the `ReClaim` folder** (the one that
contains `src`), then run:

**Compile** (puts the `.class` files in a folder called `out`):

```
javac -d out src/reclaim/exceptions/*.java src/reclaim/model/*.java src/reclaim/manager/*.java src/reclaim/main/*.java
```

**Run:**

```
java -cp out reclaim.main.Main
```

These two commands work the same on macOS, Linux and Windows (Command Prompt or PowerShell).

- `-d out` tells `javac` to create the package folders (`out/reclaim/model/...`) for us.
- `-cp out` tells `java` where to find the compiled classes.
- `reclaim.main.Main` is the **fully qualified name** (package + class) of the class with `main()`.

### Data files

Both files are created in the folder you run the program from:

| File | What it holds | How it is written |
|---|---|---|
| `items.txt` | every item, one per line | overwritten by "Save and exit" and by auto-save every 30 s |
| `claims_log.txt` | every claim attempt, success or failure | appended to (never overwritten) |

On the very first run there is no `items.txt`, so 6 sample campus items are loaded. To start
fresh again, delete `items.txt`. The answers for the sample items are listed at the end of
[SAMPLE_RUN.md](SAMPLE_RUN.md).

`items.txt` line format (fields separated by `|`):

```
Category|itemId|name|location|dateFound|status|claimedBy|<category-specific fields>
Electronics|RC-0001|Laptop|Library 2nd floor|20261120|UNCLAIMED|NONE|Dell|Silver|5CG2041XKZ
```

## Menu

```
1. Report found item       5. Claim an item
2. View all items          6. View claim history
3. Search by category      7. View items sorted by name
4. Search by keyword       8. Save and exit
```

"View all items" lists items oldest first (`Comparable`, by date found). "View items sorted by
name" uses an anonymous `Comparator`.

## Design decisions

- **The secret has no getter.** Each subclass keeps its secret (`serialNumber`, `idNumber`,
  `nameInside`, `distinguishingMark`) `private` with no `get...()` method. Only that class's own
  `verifyClaim()` and `toFileString()` ever read it, and `toString()` never prints it. No other
  class (not even `ReClaimManager` or `Main`) can look the answer up. It can only ask "is this
  answer right?". This is encapsulation used for security.
- **`getAllItems()` returns a copy.** It returns `new ArrayList<Item>(items)`, not the private
  list itself. Callers (and the sort methods) can sort or change the copy without affecting the
  manager's real list or bypassing `addItem()`'s duplicate check. It is a *shallow* copy: the
  `Item` objects are shared. That is safe here because `Item` has no setters, and its status can
  only change through `markClaimed()`, which refuses an already-claimed item.
- **Manager methods are `synchronized`.** Two threads share one `ReClaimManager`: the main thread
  (user's menu) and `AutoSaveThread` (saves every 30 s). Without locking, the auto-save could
  loop over the list while the main thread is adding an item (`ConcurrentModificationException`)
  or save an item halfway through being claimed. `addItem`, `claimItem`, `loadFromFile` and
  `saveToFile` are `synchronized`, so only one of them runs at a time.
- **The exceptions are checked (extend `Exception`).** A wrong answer, an already-claimed item
  and bad input are *expected* situations, not programming bugs. Making them checked forces
  every caller to handle them (the compiler refuses to build otherwise), so `Main` must catch
  each one and show the user a friendly message instead of crashing.
- **`|` separator and `NONE` in the file.** `|` almost never appears in item names or places
  (unlike commas or spaces), and `validateText()` rejects any input containing it, so a line
  always splits correctly. `StringTokenizer` silently skips empty tokens (`a||b` gives only
  2 tokens), which would shift every later field. Writing the literal `NONE` for empty fields
  (such as `claimedBy` on an unclaimed item) keeps the field count fixed. `NONE` is turned back
  into `""` when loading.
- **`Main` has no business logic.** `Main` only reads input, calls manager/model methods and
  prints results. All rules (verification, validation, searching, sorting, file format) live in
  `ReClaimManager` and the model classes. Each class has one job, and the rules could be reused
  with a different interface (e.g. a GUI later) without being rewritten.
- **Status uses `static final String` constants, and only `markClaimed()` changes it.** `UNCLAIMED`
  and `CLAIMED` replace an enum (not yet covered). There is no `setStatus()`, so nothing can mark
  an item claimed without going through the "already claimed?" check.
- **Every claim attempt is recorded.** Failed attempts are logged as well as successful ones, so
  staff can spot someone repeatedly guessing answers for the same item.

## Notes for the viva

- **Overloading:** constructors are overloaded in `Item` and every subclass, and `Main.readLine`
  is an overloaded method (`readLine(prompt)` / `readLine(prompt, fieldName)`).
  `searchByCategory` and `search` have *different names*, so strictly they are two search
  methods, not overloads.
- **Claim history vs log:** the "View claim history" menu option shows this session's attempts
  (kept in the `claimHistory` list). `claims_log.txt` keeps every attempt across all runs.
- **Claim date:** `ClaimRecord` stores the time from `new java.util.Date().toString()`, the
  simplest way to get the current date without `java.time`.
- **Exact matching:** answers are compared with `trim().equalsIgnoreCase(...)`, so case and extra
  spaces don't matter, but the wording must match (this matters most for "Other" items).
- **Damaged save file:** if a line in `items.txt` is corrupted or duplicated, `loadFromFile()`
  skips that line and loads the rest.
