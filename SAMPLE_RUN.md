# ReClaim: Sample Console Run

This is a real run of the program, starting with no `items.txt` (so the 6 sample items load).
User input is shown after each prompt. To keep it short, repeated menus are replaced with
`[main menu shown again]`.

What it shows:

| Scenario | Where in the run |
|---|---|
| Invalid menu input (letters and out of range) | `abc` and `9` at the menu |
| Invalid date (`InvalidInputException` from `parseDate`) | `31-02-2026` |
| Reporting a new item (gets the next ID, RC-0007) | second "Report a found item" |
| Keyword search | `library` |
| **Failed claim** (`ClaimVerificationException`) | RC-0001 answered with `1234` |
| **Successful claim** (case-insensitive, spaces trimmed) | RC-0001 answered with ` 1xkz ` |
| **Already-claimed attempt** (`ItemAlreadyClaimedException`) | RC-0001 claimed a third time |
| Unknown item ID (`InvalidInputException` from `findById`) | `RC-0099` |
| Claim history, category search, sort by name, save and exit | options 6, 3, 7, 8 |

```text
==============================================
   ReClaim - Campus Lost & Found System
==============================================
Loaded 6 items.
Auto-save is on (every 30 seconds).

---------------- MAIN MENU ----------------
1. Report found item
2. View all items
3. Search by category
4. Search by keyword
5. Claim an item
6. View claim history
7. View items sorted by name
8. Save and exit
Enter your choice (1-8): 2

--- All items (oldest first) ---
[RC-0006] Other - Keys | Found at: Parking lot on 15-11-2026 | UNCLAIMED | Description: Two keys on a red keychain
[RC-0002] IDCard - Student ID card | Found at: Main gate on 18-11-2026 | UNCLAIMED | Card type: BITS Student ID
[RC-0001] Electronics - Laptop | Found at: Library 2nd floor on 20-11-2026 | UNCLAIMED | Brand: Dell, Colour: Silver
[RC-0003] Book - Textbook | Found at: Lecture Hall 3 on 22-11-2026 | UNCLAIMED | Title: Head First Java, Author: Kathy Sierra
[RC-0005] Electronics - Wireless earbuds | Found at: Cafeteria on 24-11-2026 | UNCLAIMED | Brand: Apple, Colour: White
[RC-0004] Other - Water bottle | Found at: Sports complex on 25-11-2026 | UNCLAIMED | Description: Blue steel bottle

[main menu shown again]
Enter your choice (1-8): abc
Invalid input: "abc" is not a number. Please enter 1 to 8.

[main menu shown again]
Enter your choice (1-8): 9
Invalid input: Please enter a number from 1 to 8.

[main menu shown again]
Enter your choice (1-8): 1

--- Report a found item ---
1. Electronics   2. ID card   3. Book   4. Other
Category (1-4): 1
Item name (e.g. Laptop): Phone
Where was it found? Block A corridor
Date found (dd-mm-yyyy): 31-02-2026
Invalid input: Day 31 is not valid for month 2.

[main menu shown again]
Enter your choice (1-8): 1

--- Report a found item ---
1. Electronics   2. ID card   3. Book   4. Other
Category (1-4): 1
Item name (e.g. Laptop): Phone
Where was it found? Block A corridor
Date found (dd-mm-yyyy): 27-11-2026
Brand: Samsung
Colour: Black
SECRET - serial number (kept private): R58N12AB7Q
Item logged with ID RC-0007. Thank you for handing it in!

[main menu shown again]
Enter your choice (1-8): 4

--- Search by keyword ---
Keyword (searches name and location): library

--- Items matching "library" ---
[RC-0001] Electronics - Laptop | Found at: Library 2nd floor on 20-11-2026 | UNCLAIMED | Brand: Dell, Colour: Silver

[main menu shown again]
Enter your choice (1-8): 5

--- Claim an item ---
Item ID (e.g. RC-0001): RC-0001
Verification question: What are the last 4 characters of the device's serial number?
Your answer: 1234
Your full name: Imposter
Claim rejected: That answer does not match our records for RC-0001. If this is your item, please visit the Lost & Found desk with proof.

[main menu shown again]
Enter your choice (1-8): 5

--- Claim an item ---
Item ID (e.g. RC-0001): rc-0001
Verification question: What are the last 4 characters of the device's serial number?
Your answer:  1xkz 
Your full name: Aisha Khan
Verified! Aisha Khan, please collect item RC-0001 from the Lost & Found desk.

[main menu shown again]
Enter your choice (1-8): 5

--- Claim an item ---
Item ID (e.g. RC-0001): RC-0001
Cannot claim: Item RC-0001 has already been returned to its owner.

[main menu shown again]
Enter your choice (1-8): 5

--- Claim an item ---
Item ID (e.g. RC-0001): RC-0099
Invalid input: No item found with ID "RC-0099".

[main menu shown again]
Enter your choice (1-8): 6

--- Claim history (this session) ---
Fri Nov 27 17:45:51 GMT+04:00 2026 | RC-0001 | Imposter | FAILED
Fri Nov 27 17:45:51 GMT+04:00 2026 | RC-0001 | Aisha Khan | SUCCESS

[main menu shown again]
Enter your choice (1-8): 3

--- Search by category ---
1. Electronics   2. ID card   3. Book   4. Other
Category (1-4): 1

--- Items in category Electronics ---
[RC-0001] Electronics - Laptop | Found at: Library 2nd floor on 20-11-2026 | CLAIMED by Aisha Khan | Brand: Dell, Colour: Silver
[RC-0005] Electronics - Wireless earbuds | Found at: Cafeteria on 24-11-2026 | UNCLAIMED | Brand: Apple, Colour: White
[RC-0007] Electronics - Phone | Found at: Block A corridor on 27-11-2026 | UNCLAIMED | Brand: Samsung, Colour: Black

[main menu shown again]
Enter your choice (1-8): 7

--- All items (sorted by name) ---
[RC-0006] Other - Keys | Found at: Parking lot on 15-11-2026 | UNCLAIMED | Description: Two keys on a red keychain
[RC-0001] Electronics - Laptop | Found at: Library 2nd floor on 20-11-2026 | CLAIMED by Aisha Khan | Brand: Dell, Colour: Silver
[RC-0007] Electronics - Phone | Found at: Block A corridor on 27-11-2026 | UNCLAIMED | Brand: Samsung, Colour: Black
[RC-0002] IDCard - Student ID card | Found at: Main gate on 18-11-2026 | UNCLAIMED | Card type: BITS Student ID
[RC-0003] Book - Textbook | Found at: Lecture Hall 3 on 22-11-2026 | UNCLAIMED | Title: Head First Java, Author: Kathy Sierra
[RC-0004] Other - Water bottle | Found at: Sports complex on 25-11-2026 | UNCLAIMED | Description: Blue steel bottle
[RC-0005] Electronics - Wireless earbuds | Found at: Cafeteria on 24-11-2026 | UNCLAIMED | Brand: Apple, Colour: White

[main menu shown again]
Enter your choice (1-8): 8
All items saved to items.txt. Goodbye!
```

Files after this run:

`items.txt`
```text
Electronics|RC-0001|Laptop|Library 2nd floor|20261120|CLAIMED|Aisha Khan|Dell|Silver|5CG2041XKZ
IDCard|RC-0002|Student ID card|Main gate|20261118|UNCLAIMED|NONE|BITS Student ID|2024A7PS0123U
Book|RC-0003|Textbook|Lecture Hall 3|20261122|UNCLAIMED|NONE|Head First Java|Kathy Sierra|Rahul Mehta
Other|RC-0004|Water bottle|Sports complex|20261125|UNCLAIMED|NONE|Blue steel bottle|cat sticker
Electronics|RC-0005|Wireless earbuds|Cafeteria|20261124|UNCLAIMED|NONE|Apple|White|H4KD92LM
Other|RC-0006|Keys|Parking lot|20261115|UNCLAIMED|NONE|Two keys on a red keychain|Eiffel Tower keyring
Electronics|RC-0007|Phone|Block A corridor|20261127|UNCLAIMED|NONE|Samsung|Black|R58N12AB7Q
```

`claims_log.txt` (appended to on every attempt, kept across runs)
```text
Fri Nov 27 17:45:51 GMT+04:00 2026 | RC-0001 | Imposter | FAILED
Fri Nov 27 17:45:51 GMT+04:00 2026 | RC-0001 | Aisha Khan | SUCCESS
```

On the next run the program prints `Loaded 7 items.`, RC-0001 still shows `CLAIMED by Aisha Khan`,
and the next new item is given ID `RC-0008` (the static counter continues from the loaded IDs).

## Answers for the sample items (for demos only)

| ID | Item | Question asks for | Correct answer |
|---|---|---|---|
| RC-0001 | Laptop | last 4 of serial | `1XKZ` |
| RC-0002 | Student ID card | ID number | `2024A7PS0123U` |
| RC-0003 | Textbook | name inside cover | `Rahul Mehta` |
| RC-0004 | Water bottle | unique mark | `cat sticker` |
| RC-0005 | Wireless earbuds | last 4 of serial | `92LM` |
| RC-0006 | Keys | unique mark | `Eiffel Tower keyring` |
