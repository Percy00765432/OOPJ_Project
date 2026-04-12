import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

// Manages all accounts in the bank
// Demonstrates: Composition (Bank has-a HashMap of Accounts)
//               Polymorphism (works with Account references, not specific subclasses)
public class Bank {

    private final String bankName;
    // Maps account number (key) -> Account object (value)
    private final HashMap<String, Account> accounts;
    private final Random random = new Random();

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
    }

    // ---------------------------------------------------------------
    // Account creation
    // ---------------------------------------------------------------

    public SavingsAccount createSavingsAccount(String holderName, double initialDeposit) {
        String accNo = generateAccountNumber();
        SavingsAccount account = new SavingsAccount(accNo, holderName, initialDeposit, 0.04); // 4% p.a.
        accounts.put(accNo, account);
        System.out.println("Savings account created successfully. Account Number: " + accNo);
        return account;
    }

    public CurrentAccount createCurrentAccount(String holderName, double initialDeposit) {
        String accNo = generateAccountNumber();
        CurrentAccount account = new CurrentAccount(accNo, holderName, initialDeposit, 5000.00);
        accounts.put(accNo, account);
        System.out.println("Current account created successfully. Account Number: " + accNo);
        return account;
    }

    // ---------------------------------------------------------------
    // Transactions — all work through Account references (polymorphism)
    // ---------------------------------------------------------------

    public void deposit(String accountNumber, double amount) throws Exception {
        Account account = getAccountOrThrow(accountNumber);
        account.deposit(amount);
        System.out.printf("Deposited %.2f to account %s. New balance: %.2f%n",
                amount, accountNumber, account.getBalance());
    }

    public void withdraw(String accountNumber, double amount) throws Exception {
        Account account = getAccountOrThrow(accountNumber);
        // Calls withdraw() — behaves differently based on whether it's Savings or Current
        account.withdraw(amount);
        System.out.printf("Withdrawn %.2f from account %s. New balance: %.2f%n",
                amount, accountNumber, account.getBalance());
    }

    // Transfer money from one account to another
    public void transfer(String fromAccNo, String toAccNo, double amount) throws Exception {
        if (fromAccNo.equals(toAccNo)) {
            throw new Exception("Cannot transfer to the same account.");
        }
        Account from = getAccountOrThrow(fromAccNo);
        Account to   = getAccountOrThrow(toAccNo);

        // Withdraw from source (enforces that account's own rules)
        from.withdraw(amount);
        // Undo the automatic WITHDRAWAL record and re-record as TRANSFER_OUT
        from.setBalance(from.getBalance()); // balance already updated by withdraw()
        // Add credit to destination
        to.credit(amount, "Transfer from " + fromAccNo);

        System.out.printf("Transferred %.2f from %s to %s.%n", amount, fromAccNo, toAccNo);
    }

    // ---------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------

    public void checkBalance(String accountNumber) throws Exception {
        Account account = getAccountOrThrow(accountNumber);
        System.out.println("-".repeat(40));
        System.out.println("  Account Number : " + account.getAccountNumber());
        System.out.println("  Account Holder : " + account.getHolderName());
        System.out.println("  Account Type   : " + account.getAccountType());
        System.out.printf ("  Balance        : %.2f%n", account.getBalance());
        System.out.println("-".repeat(40));
    }

    public void printStatement(String accountNumber) throws Exception {
        Account account = getAccountOrThrow(accountNumber);
        account.printStatement();
    }

    public void listAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("=".repeat(55));
        System.out.printf("  %-12s  %-20s  %-10s  %s%n", "Acc. No.", "Holder", "Type", "Balance");
        System.out.println("=".repeat(55));
        for (Account acc : accounts.values()) {
            System.out.printf("  %-12s  %-20s  %-10s  %.2f%n",
                    acc.getAccountNumber(), acc.getHolderName(),
                    acc.getAccountType(), acc.getBalance());
        }
        System.out.println("=".repeat(55));
    }

    // Apply monthly interest to ALL savings accounts
    public void applyInterestToAllSavings() {
        int count = 0;
        for (Account acc : accounts.values()) {
            if (acc instanceof SavingsAccount) {
                ((SavingsAccount) acc).applyMonthlyInterest();
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No savings accounts found.");
        } else {
            System.out.println("Monthly interest applied to " + count + " savings account(s).");
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    // Generates a unique random 6-digit account number
    private String generateAccountNumber() {
        String accNo;
        do {
            accNo = String.valueOf(100000 + random.nextInt(900000));
        } while (accounts.containsKey(accNo));
        return accNo;
    }

    // Looks up account; throws a clear error if not found
    private Account getAccountOrThrow(String accountNumber) throws Exception {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new Exception("Account not found: " + accountNumber);
        }
        return account;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void deleteAccount(String accountNumber) throws Exception {
        if (!accounts.containsKey(accountNumber)) {
            throw new Exception("Account not found: " + accountNumber);
        }
        accounts.remove(accountNumber);
    }

    // Used by BankStorage to re-populate accounts without generating new numbers
    void addAccountDirectly(Account acc) {
        accounts.put(acc.getAccountNumber(), acc);
    }

    public String getBankName() { return bankName; }
}
