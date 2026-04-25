package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import dao.DBConnection;

public class AddCarDialog extends JDialog {

    private JTextField brandField, modelField, priceField;
    private JButton saveBtn, cancelBtn;
    private boolean success = false;

    // Standard Theme Colors
    private static final Color BG_CARD = new Color(28, 33, 40);
    private static final Color BG_INPUT = new Color(22, 26, 32);
    private static final Color ACCENT = new Color(82, 148, 255);
    private static final Color TEXT_PRI = new Color(235, 238, 245);
    private static final Color TEXT_SEC = new Color(130, 140, 158);
    private static final Color BORDER_SOFT = new Color(38, 45, 56);

    public AddCarDialog(Frame owner) {
        super(owner, "Add New Car", true);
        setSize(400, 480);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_CARD);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_CARD);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        brandField = createField();
        modelField = createField();
        priceField = createField();

        mainPanel.add(createLabel("CAR BRAND"));
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(brandField);
        mainPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(createLabel("CAR MODEL"));
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(modelField);
        mainPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(createLabel("PRICE PER DAY (₹)"));
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(priceField);

        add(mainPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(new Color(22, 26, 32));

        cancelBtn = new JButton("Cancel");
        saveBtn = new JButton("Add to Fleet");
        styleButton(cancelBtn, false);
        styleButton(saveBtn, true);

        footer.add(cancelBtn);
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> {
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            String priceText = priceField.getText().trim();

            if (brand.isEmpty() || model.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                if (insertCar(brand, model, price)) {
                    success = true;
                    dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format.");
            }
        });

        setLocationRelativeTo(owner);
    }

    private boolean insertCar(String brand, String model, double price) {
        // 🔥 THE FIX: Added 'image_filename' with a default value so MySQL doesn't reject the insert
        String query = "INSERT INTO cars (brand, model, price, available, image_filename) VALUES (?, ?, ?, 1, 'default_car.png')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, brand);
            stmt.setString(2, model);
            stmt.setDouble(3, price);

            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            // 🔥 THE FIX 2: This will now pop up the EXACT reason MySQL rejected it
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isSuccess() { return success; }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_SEC);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRI);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_SOFT), new EmptyBorder(10, 10, 10, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return f;
    }

    private void styleButton(JButton b, boolean primary) {
        b.setFocusPainted(false);
        b.setBackground(primary ? ACCENT : new Color(45, 50, 60));
        b.setForeground(primary ? Color.WHITE : TEXT_PRI);
        b.setBorder(new EmptyBorder(8, 15, 8, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}