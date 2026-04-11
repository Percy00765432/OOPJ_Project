import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

// Main entry point — Swing GUI for the banking system.
// Run with: java BankingGUI
public class BankingGUI extends JFrame {

    // ── Colour palette ────────────────────────────────────────────────
    private static final Color C_HEADER  = new Color(27,  42,  90);
    private static final Color C_SIDEBAR = new Color(45,  55,  72);
    private static final Color C_NAV_HVR = new Color(66,  84, 111);
    private static final Color C_NAV_ACT = new Color(49, 130, 206);
    private static final Color C_BG      = new Color(247, 249, 252);
    private static final Color C_CARD    = Color.WHITE;
    private static final Color C_ACCENT  = new Color(49, 130, 206);
    private static final Color C_SUCCESS = new Color(56, 161, 105);
    private static final Color C_ERROR   = new Color(229,  62,  62);
    private static final Color C_TEXT    = new Color(45,  55,  72);
    private static final Color C_MUTED   = new Color(113, 128, 150);
    private static final Color C_BORDER  = new Color(226, 232, 240);
    private static final Color C_ROW_ALT = new Color(248, 250, 252);

    // ── Fonts ─────────────────────────────────────────────────────────
    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font F_BOLD    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_PLAIN   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_MONO    = new Font("Consolas",  Font.PLAIN, 13);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);

    private static final String DATA_FILE = "bank_data.txt";

    private static final String[] CARDS = {
        "dashboard", "newAccount", "deposit", "withdraw", "transfer", "statement", "interest", "delete"
    };
    private static final String[] NAV_LABELS = {
        "Dashboard", "New Account", "Deposit", "Withdraw", "Transfer", "Statement", "Apply Interest", "Delete Account"
    };

    // ── State ─────────────────────────────────────────────────────────
    private final Bank      bank;
    private CardLayout      cardLayout;
    private JPanel          contentArea;
    private DefaultTableModel tableModel;
    private static final Color C_DANGER  = new Color(197,  48,  48);
    private static final Color C_DNG_HVR = new Color(155,  28,  28);

    private final JButton[] navBtns = new JButton[CARDS.length];

    // =================================================================
    // Constructor
    // =================================================================
    public BankingGUI() {
        bank = BankStorage.load(DATA_FILE);
        setTitle("JavaBank — Banking System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(920, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        buildUI();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                BankStorage.save(bank, DATA_FILE);
            }
        });
    }

    private void buildUI() {
        add(header(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.add(sidebar(), BorderLayout.WEST);

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(C_BG);
        contentArea.add(dashboardPanel(),  CARDS[0]);
        contentArea.add(newAccountPanel(), CARDS[1]);
        contentArea.add(depositPanel(),    CARDS[2]);
        contentArea.add(withdrawPanel(),   CARDS[3]);
        contentArea.add(transferPanel(),   CARDS[4]);
        contentArea.add(statementPanel(),  CARDS[5]);
        contentArea.add(interestPanel(),   CARDS[6]);
        contentArea.add(deletePanel(),     CARDS[7]);
        body.add(contentArea, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    // =================================================================
    // Header
    // =================================================================
    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_HEADER);
        p.setPreferredSize(new Dimension(0, 58));
        p.setBorder(new EmptyBorder(0, 22, 0, 22));

        JLabel name = new JLabel("JavaBank");
        name.setFont(new Font("Segoe UI", Font.BOLD, 21));
        name.setForeground(Color.WHITE);

        JLabel date = new JLabel(
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        date.setFont(F_SMALL);
        date.setForeground(new Color(160, 185, 230));

        p.add(name, BorderLayout.WEST);
        p.add(date, BorderLayout.EAST);
        return p;
    }

    // =================================================================
    // Sidebar
    // =================================================================
    private JPanel sidebar() {
        JPanel p = new JPanel();
        p.setBackground(C_SIDEBAR);
        p.setPreferredSize(new Dimension(200, 0));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 0, 16, 0));

        for (int i = 0; i < CARDS.length; i++) {
            final int idx = i;
            JButton btn = navButton(NAV_LABELS[i]);
            btn.addActionListener(e -> switchTo(idx));
            navBtns[i] = btn;
            p.add(btn);
            p.add(Box.createVerticalStrut(2));
        }
        activateNav(0);
        return p;
    }

    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(F_PLAIN);
        b.setForeground(new Color(190, 210, 235));
        b.setBackground(C_SIDEBAR);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(200, 44));
        b.setPreferredSize(new Dimension(200, 44));
        b.setBorder(new EmptyBorder(0, 22, 0, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!b.getBackground().equals(C_NAV_ACT)) b.setBackground(C_NAV_HVR);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!b.getBackground().equals(C_NAV_ACT)) b.setBackground(C_SIDEBAR);
            }
        });
        return b;
    }

    private void switchTo(int idx) {
        activateNav(idx);
        cardLayout.show(contentArea, CARDS[idx]);
        if (idx == 0) refreshTable();
    }

    private void activateNav(int active) {
        for (int i = 0; i < navBtns.length; i++) {
            navBtns[i].setBackground(i == active ? C_NAV_ACT : C_SIDEBAR);
            navBtns[i].setForeground(i == active ? Color.WHITE : new Color(190, 210, 235));
        }
    }

    // =================================================================
    // Dashboard
    // =================================================================
    private JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        hdr.add(heading("All Accounts"), BorderLayout.WEST);
        JButton refresh = btn("Refresh");
        refresh.setMaximumSize(new Dimension(100, 36));
        refresh.setPreferredSize(new Dimension(100, 36));
        refresh.addActionListener(e -> refreshTable());
        hdr.add(refresh, BorderLayout.EAST);
        p.add(hdr, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new String[]{"Account No.", "Holder Name", "Type", "Balance (Rs.)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scroll.getViewport().setBackground(C_CARD);
        p.add(scroll, BorderLayout.CENTER);

        refreshTable();
        return p;
    }

    private void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        for (Account acc : bank.getAllAccounts()) {
            tableModel.addRow(new Object[]{
                acc.getAccountNumber(),
                acc.getHolderName(),
                acc.getAccountType(),
                String.format("%.2f", acc.getBalance())
            });
        }
    }

    private void styleTable(JTable t) {
        t.setFont(F_PLAIN);
        t.setRowHeight(38);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setBackground(C_CARD);
        t.setForeground(C_TEXT);
        t.setSelectionBackground(new Color(235, 245, 255));
        t.setSelectionForeground(C_TEXT);

        JTableHeader th = t.getTableHeader();
        th.setFont(F_BOLD);
        th.setBackground(new Color(237, 242, 247));
        th.setForeground(C_MUTED);
        th.setPreferredSize(new Dimension(0, 42));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBackground(sel ? new Color(235, 245, 255)
                                  : (row % 2 == 0 ? C_CARD : C_ROW_ALT));
                setForeground(C_TEXT);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        });
    }

    // =================================================================
    // New Account
    // =================================================================
    private JPanel newAccountPanel() {
        JPanel outer = centered();
        JPanel card  = card();

        JTextField nameField    = field();
        JTextField depositField = field();
        JRadioButton savBtn = radio("Savings  (4% interest p.a.)");
        JRadioButton curBtn = radio("Current  (Rs. 5,000 overdraft)");
        savBtn.setSelected(true);
        ButtonGroup grp = new ButtonGroup();
        grp.add(savBtn); grp.add(curBtn);
        JLabel status = status();
        JButton create = btn("Create Account");

        create.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { err("Please enter the account holder name.", status); return; }
            double dep;
            try { dep = Double.parseDouble(depositField.getText().trim()); }
            catch (NumberFormatException ex) { err("Invalid deposit amount.", status); return; }
            if (dep < 0) { err("Initial deposit cannot be negative.", status); return; }
            try {
                Account acc = savBtn.isSelected()
                    ? bank.createSavingsAccount(name, dep)
                    : bank.createCurrentAccount(name, dep);
                BankStorage.save(bank, DATA_FILE);
                ok("Account created!  Number: " + acc.getAccountNumber(), status);
                nameField.setText(""); depositField.setText("");
            } catch (Exception ex) { err(ex.getMessage(), status); }
        });

        card.add(lbl("Create New Account", F_TITLE, C_TEXT));
        card.add(gap(18));
        card.add(lbl("Full Name", F_BOLD, C_TEXT));          card.add(gap(4));  card.add(nameField);    card.add(gap(14));
        card.add(lbl("Initial Deposit (Rs.)", F_BOLD, C_TEXT)); card.add(gap(4));  card.add(depositField); card.add(gap(14));
        card.add(lbl("Account Type", F_BOLD, C_TEXT));       card.add(gap(6));
        card.add(radioRow(savBtn, curBtn));                   card.add(gap(22));
        card.add(create); card.add(gap(10)); card.add(status);

        outer.add(card);
        return outer;
    }

    // =================================================================
    // Deposit / Withdraw — share a generic helper
    // =================================================================
    private JPanel depositPanel() {
        return simpleOp("Deposit Money", "Account Number", "Amount (Rs.)", "Deposit",
            (accNo, amount, st) -> {
                bank.deposit(accNo, amount);
                BankStorage.save(bank, DATA_FILE);
                Account a = bank.getAccount(accNo);
                ok(String.format("Deposited Rs. %.2f   |   New balance: Rs. %.2f",
                    amount, a.getBalance()), st);
            });
    }

    private JPanel withdrawPanel() {
        return simpleOp("Withdraw Money", "Account Number", "Amount (Rs.)", "Withdraw",
            (accNo, amount, st) -> {
                bank.withdraw(accNo, amount);
                BankStorage.save(bank, DATA_FILE);
                Account a = bank.getAccount(accNo);
                ok(String.format("Withdrawn Rs. %.2f   |   New balance: Rs. %.2f",
                    amount, a.getBalance()), st);
            });
    }

    @FunctionalInterface
    interface BankOp { void run(String accNo, double amount, JLabel status) throws Exception; }

    private JPanel simpleOp(String title, String f1, String f2, String btnText, BankOp op) {
        JPanel outer = centered();
        JPanel card  = card();

        JTextField accField = field();
        JTextField amtField = field();
        JLabel status = status();
        JButton btn   = btn(btnText);

        btn.addActionListener(e -> {
            String accNo = accField.getText().trim().toUpperCase();
            if (accNo.isEmpty()) { err("Please enter an account number.", status); return; }
            double amount;
            try { amount = Double.parseDouble(amtField.getText().trim()); }
            catch (NumberFormatException ex) { err("Invalid amount.", status); return; }
            try { op.run(accNo, amount, status); amtField.setText(""); }
            catch (Exception ex) { err(ex.getMessage(), status); }
        });

        card.add(lbl(title, F_TITLE, C_TEXT));
        card.add(gap(18));
        card.add(lbl(f1, F_BOLD, C_TEXT)); card.add(gap(4)); card.add(accField); card.add(gap(14));
        card.add(lbl(f2, F_BOLD, C_TEXT)); card.add(gap(4)); card.add(amtField); card.add(gap(22));
        card.add(btn); card.add(gap(10)); card.add(status);

        outer.add(card);
        return outer;
    }

    // =================================================================
    // Transfer
    // =================================================================
    private JPanel transferPanel() {
        JPanel outer = centered();
        JPanel card  = card();

        JTextField fromField = field();
        JTextField toField   = field();
        JTextField amtField  = field();
        JLabel status = status();
        JButton btn   = btn("Transfer");

        btn.addActionListener(e -> {
            String from = fromField.getText().trim().toUpperCase();
            String to   = toField.getText().trim().toUpperCase();
            double amount;
            try { amount = Double.parseDouble(amtField.getText().trim()); }
            catch (NumberFormatException ex) { err("Invalid amount.", status); return; }
            try {
                bank.transfer(from, to, amount);
                BankStorage.save(bank, DATA_FILE);
                ok(String.format("Transferred Rs. %.2f from %s to %s.", amount, from, to), status);
                amtField.setText("");
            } catch (Exception ex) { err(ex.getMessage(), status); }
        });

        card.add(lbl("Transfer Money", F_TITLE, C_TEXT));
        card.add(gap(18));
        card.add(lbl("From Account Number", F_BOLD, C_TEXT)); card.add(gap(4)); card.add(fromField); card.add(gap(14));
        card.add(lbl("To Account Number",   F_BOLD, C_TEXT)); card.add(gap(4)); card.add(toField);   card.add(gap(14));
        card.add(lbl("Amount (Rs.)",         F_BOLD, C_TEXT)); card.add(gap(4)); card.add(amtField);  card.add(gap(22));
        card.add(btn); card.add(gap(10)); card.add(status);

        outer.add(card);
        return outer;
    }

    // =================================================================
    // Statement
    // =================================================================
    private JPanel statementPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);
        topBar.add(heading("Account Statement"), BorderLayout.WEST);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        searchRow.setOpaque(false);
        JLabel accLbl = new JLabel("Account No:");
        accLbl.setFont(F_BOLD); accLbl.setForeground(C_TEXT);
        JTextField accField = new JTextField();
        accField.setFont(F_PLAIN);
        accField.setPreferredSize(new Dimension(180, 36));
        accField.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(C_BORDER), new EmptyBorder(4, 10, 4, 10)));
        JButton view = btn("View");
        view.setMaximumSize(new Dimension(90, 36));
        view.setPreferredSize(new Dimension(90, 36));
        searchRow.add(accLbl); searchRow.add(accField); searchRow.add(view);
        topBar.add(searchRow, BorderLayout.EAST);
        p.add(topBar, BorderLayout.NORTH);

        JTextArea area = new JTextArea("Enter an account number above and click View.");
        area.setFont(F_MONO);
        area.setEditable(false);
        area.setBackground(C_CARD);
        area.setForeground(C_TEXT);
        area.setBorder(new EmptyBorder(14, 18, 14, 18));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        p.add(scroll, BorderLayout.CENTER);

        ActionListener showStatement = e -> {
            String accNo = accField.getText().trim().toUpperCase();
            if (accNo.isEmpty()) { area.setText("Please enter an account number."); return; }
            Account acc = bank.getAccount(accNo);
            if (acc == null) { area.setText("Account not found: " + accNo); return; }
            area.setText(formatStatement(acc));
            area.setCaretPosition(0);
        };
        view.addActionListener(showStatement);
        accField.addActionListener(showStatement);

        return p;
    }

    private String formatStatement(Account acc) {
        String div = "=".repeat(72);
        StringBuilder sb = new StringBuilder();
        sb.append(div).append("\n");
        sb.append("  Account Statement\n");
        sb.append(String.format("  Account Number : %s%n", acc.getAccountNumber()));
        sb.append(String.format("  Account Holder : %s%n", acc.getHolderName()));
        sb.append(String.format("  Account Type   : %s%n", acc.getAccountType()));
        sb.append(String.format("  Current Balance: Rs. %.2f%n", acc.getBalance()));
        sb.append(div).append("\n");
        List<Transaction> history = acc.getHistory();
        if (history.isEmpty()) {
            sb.append("  No transactions yet.\n");
        } else {
            for (Transaction t : history) sb.append(t.getSummary()).append("\n");
        }
        sb.append(div).append("\n");
        return sb.toString();
    }

    // =================================================================
    // Apply Interest
    // =================================================================
    private JPanel interestPanel() {
        JPanel outer = centered();
        JPanel card  = card();

        JLabel desc = new JLabel(
            "<html>Click the button below to credit monthly interest<br>" +
            "to all Savings accounts &nbsp;(4% per annum &divide; 12).</html>");
        desc.setFont(F_PLAIN);
        desc.setForeground(C_MUTED);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel status = status();
        JButton apply = btn("Apply Interest to All Savings");

        apply.addActionListener(e -> {
            List<Account> savings = bank.getAllAccounts().stream()
                .filter(a -> a instanceof SavingsAccount)
                .collect(Collectors.toList());
            if (savings.isEmpty()) { err("No savings accounts found.", status); return; }
            bank.applyInterestToAllSavings();
            BankStorage.save(bank, DATA_FILE);
            ok("Monthly interest applied to " + savings.size() + " savings account(s).", status);
        });

        card.add(lbl("Apply Monthly Interest", F_TITLE, C_TEXT));
        card.add(gap(14));
        card.add(desc);
        card.add(gap(26));
        card.add(apply);
        card.add(gap(10));
        card.add(status);

        outer.add(card);
        return outer;
    }

    // =================================================================
    // =================================================================
    // Delete Account
    // =================================================================
    private JPanel deletePanel() {
        JPanel outer = centered();
        JPanel card  = card();

        JTextField accField = field();
        JLabel preview = new JLabel(" ");
        preview.setFont(F_PLAIN);
        preview.setForeground(C_MUTED);
        preview.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel status = status();

        // Lookup button — shows account details before committing
        JButton lookup = btn("Look Up");
        lookup.addActionListener(e -> {
            String accNo = accField.getText().trim().toUpperCase();
            if (accNo.isEmpty()) { err("Enter an account number first.", status); preview.setText(" "); return; }
            Account acc = bank.getAccount(accNo);
            if (acc == null) { err("Account not found: " + accNo, status); preview.setText(" "); return; }
            status.setText(" ");
            preview.setText(String.format(
                "<html><b>%s</b> &nbsp;|&nbsp; %s &nbsp;|&nbsp; %s &nbsp;|&nbsp; Balance: Rs. %.2f</html>",
                acc.getAccountNumber(), acc.getHolderName(),
                acc.getAccountType(), acc.getBalance()));
            preview.setForeground(C_TEXT);
        });

        // Red delete button
        JButton delete = new JButton("Delete Account");
        delete.setFont(F_BOLD);
        delete.setBackground(C_DANGER);
        delete.setForeground(Color.WHITE);
        delete.setBorderPainted(false);
        delete.setFocusPainted(false);
        delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete.setAlignmentX(Component.LEFT_ALIGNMENT);
        delete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        delete.setPreferredSize(new Dimension(180, 38));
        delete.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { delete.setBackground(C_DNG_HVR); }
            @Override public void mouseExited (MouseEvent e) { delete.setBackground(C_DANGER);  }
        });
        delete.addActionListener(e -> {
            String accNo = accField.getText().trim().toUpperCase();
            if (accNo.isEmpty()) { err("Enter an account number first.", status); return; }
            Account acc = bank.getAccount(accNo);
            if (acc == null) { err("Account not found: " + accNo, status); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Permanently delete account %s (%s)?\nThis cannot be undone.",
                    acc.getAccountNumber(), acc.getHolderName()),
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                bank.deleteAccount(accNo);
                BankStorage.save(bank, DATA_FILE);
                ok("Account " + accNo + " deleted.", status);
                accField.setText("");
                preview.setText(" ");
            } catch (Exception ex) { err(ex.getMessage(), status); }
        });

        card.add(lbl("Delete Account", F_TITLE, C_TEXT));
        card.add(gap(18));
        card.add(lbl("Account Number", F_BOLD, C_TEXT)); card.add(gap(4));
        card.add(accField); card.add(gap(10));
        card.add(lookup);   card.add(gap(12));
        card.add(preview);  card.add(gap(20));
        card.add(delete);   card.add(gap(10));
        card.add(status);

        outer.add(card);
        return outer;
    }

    // UI factory helpers
    // =================================================================
    /** Outer panel that centres its child using GridBagLayout */
    private JPanel centered() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_BG);
        return p;
    }

    /** White card with BoxLayout Y_AXIS and consistent padding */
    private JPanel card() {
        JPanel c = new JPanel();
        c.setBackground(C_CARD);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(C_BORDER),
            new EmptyBorder(30, 40, 30, 40)));
        return c;
    }

    private JLabel lbl(String text, Font f, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(f); l.setForeground(fg);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel heading(String text) { return lbl(text, F_HEADING, C_TEXT); }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(F_PLAIN);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setPreferredSize(new Dimension(380, 38));
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(C_BORDER),
            new EmptyBorder(4, 10, 4, 10)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFont(F_BOLD);
        b.setBackground(C_ACCENT);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.setPreferredSize(new Dimension(180, 38));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(new Color(43, 108, 176)); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(C_ACCENT); }
        });
        return b;
    }

    private JLabel status() {
        JLabel l = new JLabel(" ");
        l.setFont(F_PLAIN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private Component gap(int h) { return Box.createVerticalStrut(h); }

    private JRadioButton radio(String text) {
        JRadioButton r = new JRadioButton(text);
        r.setFont(F_PLAIN); r.setBackground(C_CARD); r.setForeground(C_TEXT);
        return r;
    }

    private JPanel radioRow(JRadioButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(C_CARD);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        for (int i = 0; i < buttons.length; i++) {
            p.add(buttons[i]);
            if (i < buttons.length - 1) p.add(Box.createHorizontalStrut(16));
        }
        return p;
    }

    private void ok (String msg, JLabel l) { l.setText(msg); l.setForeground(C_SUCCESS); }
    private void err(String msg, JLabel l) { l.setText(msg); l.setForeground(C_ERROR);   }

    // =================================================================
    // Entry point
    // =================================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new BankingGUI().setVisible(true));
    }
}
