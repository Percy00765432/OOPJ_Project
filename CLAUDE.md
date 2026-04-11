# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run

All source files are in `src/`. Compile and run from that directory:

```bash
cd src
javac Transaction.java Account.java SavingsAccount.java CurrentAccount.java Bank.java BankStorage.java BankingGUI.java
java BankingGUI
```

The compile order matters: `Transaction` and `Account` must be compiled before the classes that depend on them. There are no build tools (no Maven/Gradle) — plain `javac` only.

Data is saved to `bank_data.txt` in the working directory (`src/bank_data.txt` when run from `src/`). The file is auto-created on first save and auto-loaded on startup.

## Architecture

The class hierarchy is straightforward:

- **`Transaction`** — immutable value object; records one banking event (type, amount, balance after, timestamp, note). Uses an inner `enum Type {DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT}`.
- **`Account`** (abstract) — base class holding account number, holder name, balance, and a `List<Transaction>` history. Exposes `deposit()` as concrete and `withdraw()` / `getAccountType()` as abstract. Protected helpers `credit()` and `debit()` are the only way subclasses should mutate balance — they also append to the transaction history automatically.
- **`SavingsAccount extends Account`** — no overdraft; adds `applyMonthlyInterest()` (annual rate ÷ 12).
- **`CurrentAccount extends Account`** — allows balance to go negative up to `overdraftLimit` (default 5000).
- **`Bank`** — owns a `HashMap<String, Account>` keyed by account number (format: `ACC1001`, `ACC1002`, …). Account numbers are auto-generated from an incrementing counter starting at 1000. All public operations (`deposit`, `withdraw`, `transfer`, `checkBalance`, `printStatement`, `listAllAccounts`, `applyInterestToAllSavings`) delegate to `Account` polymorphically.
- **`BankStorage`** — static `save(Bank, path)` / `load(path)` methods. Persists accounts and transaction history to a pipe-delimited text file.
- **`BankingGUI`** — `main()` entry point; Swing window with sidebar navigation and card-layout content panels.

## Key design notes

- Data is persisted to `bank_data.txt` (pipe-delimited text). `BankStorage.save()` is called after every successful operation and on window close. `BankStorage.load()` runs at startup.
- `Bank.transfer()` calls `from.withdraw()` (which enforces that account's own withdrawal rules) then `to.credit()`. The withdrawal already records a `WITHDRAWAL` transaction on the source account; there is no separate `TRANSFER_OUT` entry written — this is a known inconsistency in the current code.
- Account numbers are normalised to uppercase in `BankingGUI` before being passed to `Bank`, so lookups are case-insensitive from the user's perspective.
