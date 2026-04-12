# Banking Transaction Management System

A Java banking application with a Swing GUI demonstrating core OOP concepts.

## How to Compile and Run

```bash
cd src
javac Transaction.java Account.java SavingsAccount.java CurrentAccount.java Bank.java BankStorage.java BankingGUI.java
java BankingGUI
```

No external libraries or build tools required — plain `javac` only.

## Features

| Screen | Feature |
|--------|---------|
| Dashboard | View all accounts in a table |
| New Account | Create a Savings or Current account |
| Deposit | Add money to an account |
| Withdraw | Remove money from an account |
| Transfer | Move money between two accounts |
| Statement | View full transaction history for an account |
| Apply Interest | Credit monthly interest to all Savings accounts |
| Delete Account | Permanently remove an account |

## Project Structure

```
src/
├── Transaction.java       — Immutable record of one transaction
├── Account.java           — Abstract base class for all accounts
├── SavingsAccount.java    — Savings account (4% interest, no overdraft)
├── CurrentAccount.java    — Current account (overdraft up to Rs. 5,000)
├── Bank.java              — Manages all accounts and operations
├── BankStorage.java       — Saves and loads data to bank_data.txt
└── BankingGUI.java        — Swing GUI and main entry point
```

Data is persisted to `bank_data.txt` (pipe-delimited text file) in the working directory. It is auto-created on first save and auto-loaded on startup.

## OOP Concepts Covered

| Concept | Where |
|---------|-------|
| **Encapsulation** | `private` fields + getters in `Account`, `Transaction` |
| **Abstraction** | `abstract class Account` with `abstract withdraw()` |
| **Inheritance** | `SavingsAccount extends Account`, `CurrentAccount extends Account` |
| **Polymorphism** | `account.withdraw()` behaves differently per subclass |
| **Composition** | `Bank` has-a `HashMap` of `Account`; `Account` has-a `List` of `Transaction` |

## Account Rules

- **Savings Account**: Cannot withdraw more than available balance. Earns 4% annual interest (applied monthly as 4% ÷ 12).
- **Current Account**: Can withdraw up to balance + overdraft limit (default: Rs. 5,000).
- **Account Numbers**: Randomly generated 6-digit integers (e.g. `482917`).
