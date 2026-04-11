import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents a single banking transaction (deposit, withdrawal, or transfer)
// Demonstrates: Encapsulation — all fields are private and read-only after creation
public class Transaction {

    // Enum to define the type of transaction
    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    }

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String note;  // e.g. "Transfer to ACC1002"

    // Constructor — sets all fields once; they can never be changed
    public Transaction(Type type, double amount, double balanceAfter, String note) {
        this.type        = type;
        this.amount      = amount;
        this.balanceAfter = balanceAfter;
        this.note        = note;
        this.timestamp   = LocalDateTime.now();
    }

    // Constructor used when loading from storage (timestamp supplied explicitly)
    public Transaction(Type type, double amount, double balanceAfter, String note, LocalDateTime timestamp) {
        this.type        = type;
        this.amount      = amount;
        this.balanceAfter = balanceAfter;
        this.note        = note;
        this.timestamp   = timestamp;
    }

    // --- Getters (no setters — transactions are immutable) ---

    public Type getType()              { return type; }
    public double getAmount()          { return amount; }
    public double getBalanceAfter()    { return balanceAfter; }
    public String getNote()            { return note; }
    public LocalDateTime getTimestamp(){ return timestamp; }

    // Returns a formatted one-line summary for printing
    public String getSummary() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("  [%s]  %-15s  Amount: %8.2f  Balance: %8.2f  %s",
                timestamp.format(fmt),
                type,
                amount,
                balanceAfter,
                note.isEmpty() ? "" : "(" + note + ")");
    }
}
