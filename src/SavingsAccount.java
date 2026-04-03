// Savings account: earns interest, cannot go below zero
// Demonstrates: Inheritance (extends Account) and Polymorphism (@Override)
public class SavingsAccount extends Account {

    private double interestRate;  // Annual interest rate, e.g. 0.04 = 4%

    // Constructor passes shared data up to Account via super()
    public SavingsAccount(String accountNumber, String holderName,
                          double initialBalance, double interestRate) {
        super(accountNumber, holderName, initialBalance);
        this.interestRate = interestRate;
    }

    // ---------------------------------------------------------------
    // Overriding abstract methods from Account (Polymorphism)
    // ---------------------------------------------------------------

    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Withdrawal amount must be positive.");
        }
        if (amount > getBalance()) {
            throw new Exception("Insufficient funds. Available balance: " + getBalance());
        }
        // Use the protected helper from Account to move money and record the transaction
        debit(amount, "Withdrawal");
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

    // ---------------------------------------------------------------
    // Feature unique to Savings accounts
    // ---------------------------------------------------------------

    // Adds monthly interest to the account (annual rate / 12)
    public void applyMonthlyInterest() {
        double interest = getBalance() * (interestRate / 12);
        credit(interest, String.format("Monthly interest (%.1f%% p.a.)", interestRate * 100));
        System.out.printf("Interest of %.2f applied to account %s.%n", interest, getAccountNumber());
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}
