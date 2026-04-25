package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.DBConnection;

public class RegisterFrame extends JFrame {

    private JTextField usernameField, licenseField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton signUpBtn;
    private JLabel statusLabel;

    private static final Color BG_APP        = new Color(10, 12, 18);
    private static final Color BG_CARD       = new Color(28, 30, 40);
    private static final Color BG_INPUT      = new Color(40, 42, 54);
    private static final Color BORDER_INPUT  = new Color(60, 65, 80);
    private static final Color BORDER_SOFT   = new Color(45, 50, 65);

    private static final Color TEXT_LIGHT    = new Color(245, 245, 250);
    private static final Color TEXT_MUTED    = new Color(140, 145, 160);

    // Gradients
    private static final Color BRAND_BLUE    = new Color(59, 130, 246);
    private static final Color BRAND_PURPLE  = new Color(168, 85, 247);

    public RegisterFrame() {
        setTitle("AutoFleet — Create Account");
        setSize(900, 750);
        setMinimumSize(new Dimension(800, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(BG_APP);

        add(buildRegisterCard());
        setupActions();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildRegisterCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setPreferredSize(new Dimension(450, 620));
        card.setMinimumSize(new Dimension(450, 620));
        card.setMaximumSize(new Dimension(450, 620));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(24, BORDER_SOFT),
                new EmptyBorder(40, 45, 40, 45)
        ));


        GradientTextLabel title = new GradientTextLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join AutoFleet and start your journey.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        licenseField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        formPanel.add(makeLeftAlignedRow(makeInputLabel("NEW USERNAME")));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(buildPlainInputField(usernameField));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(makeLeftAlignedRow(makeInputLabel("DRIVER'S LICENSE NUMBER")));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(buildPlainInputField(licenseField));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(makeLeftAlignedRow(makeInputLabel("PASSWORD")));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(buildPlainInputField(passwordField));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(makeLeftAlignedRow(makeInputLabel("CONFIRM PASSWORD")));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(buildPlainInputField(confirmPasswordField));
        formPanel.add(Box.createVerticalStrut(20));


        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(255, 90, 90));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        signUpBtn = new GradientButton("Sign Up");
        signUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginRow.setBackground(BG_CARD);
        JLabel alreadyHave = new JLabel("Already have an account?");
        alreadyHave.setForeground(TEXT_MUTED);
        alreadyHave.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel loginLink = new JLabel("Back to Login");
        loginLink.setForeground(BRAND_BLUE);
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginFrame();
            }
        });

        loginRow.add(alreadyHave);
        loginRow.add(loginLink);


        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(formPanel);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(signUpBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(loginRow);

        return card;
    }

    private void setupActions() {
        signUpBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String license = licenseField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String confirmPass = new String(confirmPasswordField.getPassword());

            // 1. Basic Validation
            if (username.isEmpty() || license.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("Please fill out all fields.");
                return;
            }

            if (!pass.equals(confirmPass)) {
                statusLabel.setText("Passwords do not match.");
                return;
            }

            signUpBtn.setEnabled(false);
            statusLabel.setText(" ");

            Timer animateDots = new Timer(300, evt -> {
                String text = signUpBtn.getText();
                if (text.equals("Creating Account...")) {
                    signUpBtn.setText("Creating Account");
                } else {
                    signUpBtn.setText(text + ".");
                }
            });
            signUpBtn.setText("Creating Account");
            animateDots.start();

            Timer networkDelay = new Timer(1500, evt -> {
                animateDots.stop();

                if (registerUserInDatabase(username, pass, license)) {
                    statusLabel.setForeground(new Color(52, 199, 130));
                    statusLabel.setText("Account Created! Redirecting...");
                    signUpBtn.setText("Success");

                    Timer redirectTimer = new Timer(800, redirectEvt -> {
                        dispose();
                        new LoginFrame();
                    });
                    redirectTimer.setRepeats(false);
                    redirectTimer.start();
                } else {
                    statusLabel.setForeground(new Color(255, 90, 90));
                    statusLabel.setText("Username or License already exists.");
                    signUpBtn.setText("Sign Up");
                    signUpBtn.setEnabled(true);
                }
            });
            networkDelay.setRepeats(false);
            networkDelay.start();
        });
    }

    private boolean registerUserInDatabase(String username, String password, String license) {
        String checkSql = "SELECT id FROM users WHERE username = ? OR license_number = ?";
        String insertSql = "INSERT INTO users (username, password, license_number, role) VALUES (?, ?, ?, 'customer')";

        try (Connection conn = DBConnection.getConnection()) {
            // Check for duplicates first
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, license);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return false; // User or license already exists
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, license);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private JPanel makeLeftAlignedRow(Component c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(BG_CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        p.add(c);
        return p;
    }

    private JLabel makeInputLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JPanel buildPlainInputField(JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_INPUT);
        wrap.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, BORDER_INPUT),
                new EmptyBorder(10, 15, 10, 15)
        ));
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(BRAND_BLUE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    private class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color c1 = isEnabled() ? BRAND_BLUE : new Color(40, 60, 90);
            Color c2 = isEnabled() ? BRAND_PURPLE : new Color(80, 50, 100);

            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class GradientTextLabel extends JLabel {
        public GradientTextLabel(String text) { super(text); }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fm = g2.getFontMetrics(getFont());
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();

            GradientPaint gp = new GradientPaint(0, 0, BRAND_BLUE, textWidth, 0, BRAND_PURPLE);
            g2.setPaint(gp);
            g2.setFont(getFont());

            g2.drawString(getText(), 0, textHeight);
            g2.dispose();
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
    }
}