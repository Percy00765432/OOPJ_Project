// Current account: supports overdraft (balance can go negative up to a limit)
// Demonstrates: Inheritance (extends Account) and Polymorphism (@Override)
public class CurrentAccount extends Account {

    private double overdraftLimit;  // How much below zero the account can go

    public CurrentAccount(String accountNumber, String holderName,
                          double initialBalance, double overdraftLimit) {
        super(accountNumber, holderName, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    // ---------------------------------------------------------------
    // Overriding abstract methods from Account (Polymorphism)
    // ---------------------------------------------------------------

    // DIFFERENT behaviour from SavingsAccount.withdraw() — allows overdraft
    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Withdrawal amount must be positive.");
        }
        // Available funds = current balance + overdraft allowance
        double available = getBalance() + overdraftLimit;
        if (amount > available) {
            throw new Exception(String.format(
                "Exceeds overdraft limit. Available (incl. overdraft): %.2f", available));
        }
        debit(amount, "Withdrawal");
    }

    @Override
    public String getAccountType() {
        return "Current";
    }

    // ---------------------------------------------------------------
    // Feature unique to Current accounts
    // ---------------------------------------------------------------

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }
}
