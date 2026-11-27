# ReClaim: Campus Lost & Found and Claim Verification System

CS F213 Object Oriented Programming mini project, BITS Pilani Dubai.

ReClaim is a menu-driven console program for a campus Lost & Found desk. Each found item is
logged with its public details and one private secret detail that only the real owner would
know, such as a serial number or the name written inside a book. To claim an item, a person
must answer a verification question about that secret.

## Features

- Report found items in four categories: Electronics, ID Card, Book, Other
- View all items (sorted by date found) or sorted by name
- Search by category or by keyword (name or location)
- Claim an item by answering its verification question
- View the history of claim attempts
- Items are saved to `items.txt`, and every claim attempt is logged to `claims_log.txt`
- A background thread auto-saves every 30 seconds

## Project structure

```
src/reclaim/
├── model/        Claimable, Item, Electronics, IDCard, Book, Other
├── exceptions/   ClaimVerificationException, ItemAlreadyClaimedException, InvalidInputException
├── manager/      ReClaimManager, AutoSaveThread
└── main/         Main
```

## How to run

From the project folder (the one containing `src`):

```
javac -d out -sourcepath src src/reclaim/main/Main.java
java -cp out reclaim.main.Main
```

On the first run, six sample items are loaded. To try a claim, choose option 5, enter
`RC-0001` and answer `1XKZ`.

## Design

- Each item's secret is a private field with no getter. Only the item's own `verifyClaim()`
  method can check it.
- `ReClaimManager` holds all the logic. `Main` only reads input and prints results.
- Wrong answers, already-claimed items and invalid input are custom checked exceptions,
  each shown to the user as a clear message.
- Methods that change or save the item list are `synchronized`, because the main thread and the
  auto-save thread share the same list.
