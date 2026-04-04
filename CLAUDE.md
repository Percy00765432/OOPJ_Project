# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run

All source files are in `src/`. Compile and run from that directory:

```bash
cd src
javac Transaction.java Account.java SavingsAccount.java CurrentAccount.java Bank.java BankingApp.java
java BankingApp
```

The compile order matters: `Transaction` and `Account` must be compiled before the classes that depend on them. There are no build tools (no Maven/Gradle) — plain `javac` only.

## Architecture

The class hierarchy is straightforward:

- **`Transaction`** — immutable value object; records one banking event (type, amount, balance after, timestamp, note). Uses an inner `enum Type {DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT}`.
- **`Account`** (abstract) — base class holding account number, holder name, balance, and a `List<Transaction>` history. Exposes `deposit()` as concrete and `withdraw()` / `getAccountType()` as abstract. Protected helpers `credit()` and `debit()` are the only way subclasses should mutate balance — they also append to the transaction history automatically.
- **`SavingsAccount extends Account`** — no overdraft; adds `applyMonthlyInterest()` (annual rate ÷ 12).
- **`CurrentAccount extends Account`** — allows balance to go negative up to `overdraftLimit` (default 5000).
- **`Bank`** — owns a `HashMap<String, Account>` keyed by account number (format: `ACC1001`, `ACC1002`, …). Account numbers are auto-generated from an incrementing counter starting at 1000. All public operations (`deposit`, `withdraw`, `transfer`, `checkBalance`, `printStatement`, `listAllAccounts`, `applyInterestToAllSavings`) delegate to `Account` polymorphically.
- **`BankingApp`** — `main()` entry point; console menu loop. Contains input helpers `readInt()` and `readDouble()` that loop until valid input is given.

## Key design notes

- Data lives only in memory — there is no file I/O or database. All state is lost when the program exits.
- `Bank.transfer()` calls `from.withdraw()` (which enforces that account's own withdrawal rules) then `to.credit()`. The withdrawal already records a `WITHDRAWAL` transaction on the source account; there is no separate `TRANSFER_OUT` entry written — this is a known inconsistency in the current code.
- Account numbers are normalised to uppercase in `BankingApp` (`toUpperCase()`) before being passed to `Bank`, so lookups are case-insensitive from the user's perspective.
