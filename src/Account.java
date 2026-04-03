import java.util.ArrayList;
import java.util.List;

// Abstract base class for all bank accounts
// Demonstrates: Abstraction (abstract class + abstract methods) and Encapsulation (private fields)
public abstract class Account {

    // Private fields — only accessible through methods (encapsulation)
    private final String accountNumber;
    private final String holderName;
    private double balance;
    private final List<Transaction> history;

    // Constructor called by subclasses via super()
    public Account(String accountNumber, String holderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName    = holderName;
        this.balance       = initialBalance;
        this.history       = new ArrayList<>();

        // Record the opening deposit
        if (initialBalance > 0) {
            history.add(new Transaction(Transaction.Type.DEPOSIT, initialBalance, balance, "Account opened"));
        }
    }

    // ---------------------------------------------------------------
    // Abstract methods — every subclass MUST provide its own version
    // ---------------------------------------------------------------

    // Different account types have different withdrawal rules
    public abstract void withdraw(double amount) throws Exception;

    // Returns a label like "Savings" or "Current"
    public abstract String getAccountType();

    // ---------------------------------------------------------------
    // Concrete methods — shared behaviour, same for all account types
    // ---------------------------------------------------------------

    // Deposit money into this account
    public void deposit(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Deposit amount must be positive.");
        }
        credit(amount, "Deposit");
    }

    // Print a full account statement to the console
    public void printStatement() {
        System.out.println("=".repeat(70));
        System.out.println("  Account Statement");
        System.out.println("  Account Number : " + accountNumber);
        System.out.println("  Account Holder : " + holderName);
        System.out.println("  Account Type   : " + getAccountType());
        System.out.printf ("  Current Balance: %.2f%n", balance);
        System.out.println("=".repeat(70));
        if (history.isEmpty()) {
            System.out.println("  No transactions yet.");
        } else {
            for (Transaction t : history) {
                System.out.println(t.getSummary());
            }
        }
        System.out.println("=".repeat(70));
    }

    // ---------------------------------------------------------------
    // Protected helpers — subclasses can use these to move money
    // (protected = visible to this class and its subclasses only)
    // ---------------------------------------------------------------

    protected void credit(double amount, String note) {
        balance += amount;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, note));
    }

    protected void debit(double amount, String note) {
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, note));
    }

    protected void recordTransfer(Transaction.Type type, double amount, double balanceAfter, String note) {
        history.add(new Transaction(type, amount, balanceAfter, note));
    }

    // ---------------------------------------------------------------
    // Getters — read-only access to private fields
    // ---------------------------------------------------------------

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName()    { return holderName; }
    public double getBalance()       { return balance; }

    // Used internally by Bank for transfers (keeps balance update in one place)
    protected void setBalance(double balance) { this.balance = balance; }
}
