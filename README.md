# Banking Transaction Management System

A console-based banking application built in Java demonstrating core OOP concepts.

## How to Compile and Run

```bash
cd src
javac Transaction.java Account.java SavingsAccount.java CurrentAccount.java Bank.java BankingApp.java
java BankingApp
```

## Features

| Option | Feature |
|--------|---------|
| 1 | Create Savings or Current account |
| 2 | Deposit money |
| 3 | Withdraw money |
| 4 | Transfer money between accounts |
| 5 | Check balance |
| 6 | View full transaction statement |
| 7 | List all accounts |
| 8 | Apply monthly interest (savings accounts) |

## Project Structure

```
src/
├── Transaction.java       — Immutable record of one transaction
├── Account.java           — Abstract base class for all accounts
├── SavingsAccount.java    — Savings account (4% interest, no overdraft)
├── CurrentAccount.java    — Current account (overdraft up to 5000)
├── Bank.java              — Manages all accounts and operations
└── BankingApp.java        — Main menu and entry point
```

## OOP Concepts Covered

| Concept | Where |
|---------|-------|
| **Encapsulation** | `private` fields + getters in `Account`, `Transaction` |
| **Abstraction** | `abstract class Account` with `abstract withdraw()` |
| **Inheritance** | `SavingsAccount extends Account`, `CurrentAccount extends Account` |
| **Polymorphism** | `account.withdraw()` behaves differently per subclass |
| **Composition** | `Bank` has-a `HashMap` of `Account`; `Account` has-a `List` of `Transaction` |

## Account Rules

- **Savings Account**: Cannot withdraw more than available balance. Earns 4% annual interest.
- **Current Account**: Can withdraw up to balance + overdraft limit (default: 5000).
