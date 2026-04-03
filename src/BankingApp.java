import java.util.Scanner;

// Entry point — runs the console menu
// Demonstrates: how objects are created and used together
public class BankingApp {

    private static Bank bank = new Bank("Java National Bank");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Welcome to " + bank.getBankName());
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:  createAccount();       break;
                case 2:  depositMoney();         break;
                case 3:  withdrawMoney();        break;
                case 4:  transferMoney();        break;
                case 5:  checkBalance();         break;
                case 6:  viewStatement();        break;
                case 7:  listAccounts();         break;
                case 8:  applyInterest();        break;
                case 0:
                    System.out.println("Thank you for using " + bank.getBankName() + ". Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    // ---------------------------------------------------------------
    // Menu handlers
    // ---------------------------------------------------------------

    private static void createAccount() {
        System.out.println("\n-- Create Account --");
        System.out.println("1. Savings Account (4% annual interest, no overdraft)");
        System.out.println("2. Current Account (overdraft up to 5000)");
        int type = readInt("Choose account type: ");

        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine().trim();
        double initialDeposit = readDouble("Enter initial deposit amount: ");

        try {
            if (type == 1) {
                bank.createSavingsAccount(name, initialDeposit);
            } else if (type == 2) {
                bank.createCurrentAccount(name, initialDeposit);
            } else {
                System.out.println("Invalid account type.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void depositMoney() {
        System.out.println("\n-- Deposit Money --");
        String accNo = readAccountNumber();
        double amount = readDouble("Enter amount to deposit: ");
        try {
            bank.deposit(accNo, amount);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void withdrawMoney() {
        System.out.println("\n-- Withdraw Money --");
        String accNo = readAccountNumber();
        double amount = readDouble("Enter amount to withdraw: ");
        try {
            bank.withdraw(accNo, amount);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void transferMoney() {
        System.out.println("\n-- Transfer Money --");
        System.out.print("Enter source account number: ");
        String from = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter destination account number: ");
        String to = scanner.nextLine().trim().toUpperCase();
        double amount = readDouble("Enter amount to transfer: ");
        try {
            bank.transfer(from, to, amount);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void checkBalance() {
        System.out.println("\n-- Check Balance --");
        String accNo = readAccountNumber();
        try {
            bank.checkBalance(accNo);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewStatement() {
        System.out.println("\n-- View Statement --");
        String accNo = readAccountNumber();
        try {
            bank.printStatement(accNo);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listAccounts() {
        System.out.println("\n-- All Accounts --");
        bank.listAllAccounts();
    }

    private static void applyInterest() {
        System.out.println("\n-- Apply Monthly Interest (Savings Accounts) --");
        bank.applyInterestToAllSavings();
    }

    // ---------------------------------------------------------------
    // Input helpers
    // ---------------------------------------------------------------

    private static void printMenu() {
        System.out.println("\n========================================");
        System.out.println("  Main Menu");
        System.out.println("========================================");
        System.out.println("  1. Create Account");
        System.out.println("  2. Deposit");
        System.out.println("  3. Withdraw");
        System.out.println("  4. Transfer");
        System.out.println("  5. Check Balance");
        System.out.println("  6. View Statement");
        System.out.println("  7. List All Accounts");
        System.out.println("  8. Apply Monthly Interest");
        System.out.println("  0. Exit");
        System.out.println("========================================");
    }

    private static String readAccountNumber() {
        System.out.print("Enter account number (e.g. ACC1001): ");
        return scanner.nextLine().trim().toUpperCase();
    }

    // Reads an integer safely — keeps asking until a valid number is entered
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Reads a double safely
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("Amount cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
