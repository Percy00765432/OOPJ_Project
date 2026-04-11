import java.io.*;
import java.time.LocalDateTime;

// Saves and loads all bank data to/from a plain-text file.
// Format:
//   COUNTER=<n>
//   ACCOUNT|<accNo>|<Savings|Current>|<holderName>|<balance>|<interestRate|overdraftLimit>
//   TX|<accNo>|<TYPE>|<amount>|<balanceAfter>|<timestamp>|<note>
public class BankStorage {

    public static void save(Bank bank, String filePath) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
            out.println("COUNTER=" + bank.getAccountCounter());
            for (Account acc : bank.getAllAccounts()) {
                String extra = (acc instanceof SavingsAccount)
                    ? String.format("%.4f", ((SavingsAccount) acc).getInterestRate())
                    : String.format("%.2f", ((CurrentAccount) acc).getOverdraftLimit());
                out.printf("ACCOUNT|%s|%s|%s|%.2f|%s%n",
                    acc.getAccountNumber(), acc.getAccountType(),
                    acc.getHolderName(), acc.getBalance(), extra);
                for (Transaction tx : acc.getHistory()) {
                    // Put timestamp before note so the note can safely contain '|'
                    String note = tx.getNote().replace("\n", " ");
                    out.printf("TX|%s|%s|%.2f|%.2f|%s|%s%n",
                        acc.getAccountNumber(), tx.getType(),
                        tx.getAmount(), tx.getBalanceAfter(),
                        tx.getTimestamp(), note);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not save data — " + e.getMessage());
        }
    }

    public static Bank load(String filePath) {
        Bank bank = new Bank("JavaBank");
        File file = new File(filePath);
        if (!file.exists()) return bank;

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            Account current = null;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("COUNTER=")) {
                    bank.setAccountCounter(Integer.parseInt(line.substring(8)));

                } else if (line.startsWith("ACCOUNT|")) {
                    // ACCOUNT|accNo|type|holderName|balance|rateOrLimit
                    String[] p = line.split("\\|", 6);
                    String accNo   = p[1];
                    String type    = p[2];
                    String name    = p[3];
                    double balance = Double.parseDouble(p[4]);
                    double extra   = Double.parseDouble(p[5]);
                    // Create with balance 0 so no automatic "Account opened" transaction
                    current = type.equals("Savings")
                        ? new SavingsAccount(accNo, name, 0, extra)
                        : new CurrentAccount(accNo, name, 0, extra);
                    current.setBalance(balance);
                    bank.addAccountDirectly(current);

                } else if (line.startsWith("TX|") && current != null) {
                    // TX|accNo|type|amount|balanceAfter|timestamp|note (note may contain '|')
                    String[] p = line.split("\\|", 7);
                    Transaction.Type txType = Transaction.Type.valueOf(p[2]);
                    double amount   = Double.parseDouble(p[3]);
                    double balAfter = Double.parseDouble(p[4]);
                    LocalDateTime ts = LocalDateTime.parse(p[5]);
                    String note     = p.length > 6 ? p[6] : "";
                    current.addHistoryEntry(new Transaction(txType, amount, balAfter, note, ts));
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: error loading data — " + e.getMessage());
        }
        return bank;
    }
}
