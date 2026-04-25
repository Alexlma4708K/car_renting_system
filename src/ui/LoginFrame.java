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
import dao.UserDAO;
import model.User;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JLabel statusLabel;

    private static final Color BG_APP        = new Color(10, 12, 18);
    private static final Color BG_CARD       = new Color(28, 30, 40);
    private static final Color BG_INPUT      = new Color(40, 42, 54);
    private static final Color BORDER_INPUT  = new Color(60, 65, 80);

    private static final Color TEXT_LIGHT    = new Color(245, 245, 250);
    private static final Color TEXT_MUTED    = new Color(140, 145, 160);


    private static final Color BRAND_BLUE    = new Color(59, 130, 246);
    private static final Color BRAND_PURPLE  = new Color(168, 85, 247);

    private static final Color BORDER_SOFT   = new Color(45, 50, 65);

    public LoginFrame() {
        setTitle("AutoFleet — Sign In");
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(BG_APP);

        add(buildLoginCard());
        setupActions();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setPreferredSize(new Dimension(420, 520));
        card.setMinimumSize(new Dimension(420, 520));
        card.setMaximumSize(new Dimension(420, 520));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(24, new Color(45, 50, 65)),
                new EmptyBorder(40, 40, 40, 40)
        ));

        GradientTextLabel title = new GradientTextLabel("AutoFleet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to access your fleet.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        formPanel.add(makeLeftAlignedRow(makeInputLabel("Username")));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(buildIconInputField("@", usernameField));
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(makeLeftAlignedRow(makeInputLabel("Password")));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(buildIconInputField("*", passwordField));
        formPanel.add(Box.createVerticalStrut(15));

        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setBackground(BG_CARD);
        optionsRow.setMaximumSize(new Dimension(340, 30));
        optionsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setBackground(BG_CARD);
        rememberMe.setForeground(TEXT_LIGHT);
        rememberMe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rememberMe.setFocusPainted(false);
        rememberMe.setBorder(BorderFactory.createEmptyBorder(0, -4, 0, 0));

        JLabel forgotPass = new JLabel("Forgot Password?");
        forgotPass.setForeground(BRAND_BLUE);
        forgotPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));

        forgotPass.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordDialog(LoginFrame.this).setVisible(true);
            }
        });

        optionsRow.add(rememberMe, BorderLayout.WEST);
        optionsRow.add(forgotPass, BorderLayout.EAST);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(255, 90, 90));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn = new GradientButton("Sign In");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(340, 45));

        JPanel signUpRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signUpRow.setBackground(BG_CARD);
        JLabel dontHave = new JLabel("Don't have an account?");
        dontHave.setForeground(TEXT_MUTED);
        dontHave.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel signUpLink = new JLabel("Sign up here");
        signUpLink.setForeground(BRAND_BLUE);
        signUpLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signUpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signUpLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterFrame();
            }
        });

        signUpRow.add(dontHave);
        signUpRow.add(signUpLink);

        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(35));
        card.add(formPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(optionsRow);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(25));
        card.add(signUpRow);

        return card;
    }

    private void setupActions() {
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password.");
                return;
            }

            loginBtn.setEnabled(false);
            statusLabel.setText(" ");

            Timer animateDots = new Timer(300, evt -> {
                String text = loginBtn.getText();
                if (text.equals("Authenticating...")) {
                    loginBtn.setText("Authenticating");
                } else {
                    loginBtn.setText(text + ".");
                }
            });
            loginBtn.setText("Authenticating");
            animateDots.start();

            Timer networkDelay = new Timer(1500, evt -> {
                animateDots.stop();

                UserDAO dao = new UserDAO();
                User user = dao.authenticate(username, password);

                if (user != null) {
                    statusLabel.setForeground(new Color(52, 199, 130));
                    statusLabel.setText("Success! Redirecting...");
                    loginBtn.setText("Access Granted");

                    Timer redirectTimer = new Timer(600, redirectEvt -> {
                        dispose();
                        if ("admin".equalsIgnoreCase(user.getRole())) {
                            new AdminFrame(user);
                        } else {
                            new MainFrame(user);
                        }
                    });
                    redirectTimer.setRepeats(false);
                    redirectTimer.start();

                } else {
                    statusLabel.setForeground(new Color(255, 90, 90));
                    statusLabel.setText("Invalid username or password.");
                    loginBtn.setText("Sign In");
                    loginBtn.setEnabled(true);
                }
            });
            networkDelay.setRepeats(false);
            networkDelay.start();
        });
    }

    private class ForgotPasswordDialog extends JDialog {
        private JTextField userField, licenseField;
        private JPasswordField newPassField;
        private JButton resetBtn;
        private JLabel msgLabel;

        public ForgotPasswordDialog(Frame owner) {
            super(owner, "Identity Verification", true);
            setSize(400, 500);
            setLayout(new BorderLayout());
            getContentPane().setBackground(BG_CARD);
            setUndecorated(true);
            ((JComponent) getContentPane()).setBorder(new LineBorder(BORDER_SOFT, 1));

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(BG_CARD);
            mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

            JLabel title = new JLabel("Password Recovery");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(TEXT_LIGHT);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtitle = new JLabel("Verify using your registered Driver's License.");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(TEXT_MUTED);
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            userField = new JTextField();
            licenseField = new JTextField();
            newPassField = new JPasswordField();

            mainPanel.add(title);
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(subtitle);
            mainPanel.add(Box.createVerticalStrut(30));

            mainPanel.add(makeLeftAlignedRow(makeInputLabel("Account Username")));
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(buildPlainInputField(userField));
            mainPanel.add(Box.createVerticalStrut(15));

            mainPanel.add(makeLeftAlignedRow(makeInputLabel("Driver's License Number")));
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(buildPlainInputField(licenseField));
            mainPanel.add(Box.createVerticalStrut(15));

            mainPanel.add(makeLeftAlignedRow(makeInputLabel("New Password")));
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(buildPlainInputField(newPassField));
            mainPanel.add(Box.createVerticalStrut(20));

            msgLabel = new JLabel(" ");
            msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(msgLabel);
            mainPanel.add(Box.createVerticalStrut(10));

            JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            btnPanel.setBackground(BG_CARD);
            btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setFocusPainted(false);
            cancelBtn.setContentAreaFilled(false);
            cancelBtn.setForeground(TEXT_MUTED); // Uses sleek grey text
            cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            cancelBtn.setBorder(new LineBorder(BORDER_SOFT, 1)); // Soft dark border
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.addActionListener(e -> dispose());

            resetBtn = new GradientButton("Reset Password");

            btnPanel.add(cancelBtn);
            btnPanel.add(resetBtn);
            mainPanel.add(btnPanel);

            add(mainPanel, BorderLayout.CENTER);
            setLocationRelativeTo(owner);
            setupResetAction();
        }

        private void setupResetAction() {
            resetBtn.addActionListener(e -> {
                String uName = userField.getText().trim();
                String license = licenseField.getText().trim();
                String nPass = new String(newPassField.getPassword()).trim();

                if (uName.isEmpty() || license.isEmpty() || nPass.isEmpty()) {
                    msgLabel.setForeground(new Color(255, 90, 90));
                    msgLabel.setText("All fields are required.");
                    return;
                }

                resetBtn.setEnabled(false);
                resetBtn.setText("Verifying...");

                Timer t = new Timer(1200, evt -> {
                    boolean success = verifyAndUpdatePassword(uName, license, nPass);

                    if (success) {
                        msgLabel.setForeground(new Color(52, 199, 130));
                        msgLabel.setText("Password reset successful!");
                        resetBtn.setText("Done");

                        Timer close = new Timer(1000, cEvt -> dispose());
                        close.setRepeats(false);
                        close.start();
                    } else {
                        msgLabel.setForeground(new Color(255, 90, 90));
                        msgLabel.setText("Verification failed. Invalid details.");
                        resetBtn.setText("Reset Password");
                        resetBtn.setEnabled(true);
                    }
                });
                t.setRepeats(false);
                t.start();
            });
        }

        private boolean verifyAndUpdatePassword(String username, String license, String newPassword) {
            String checkSql = "SELECT id FROM users WHERE username = ? AND license_number = ?";
            String updateSql = "UPDATE users SET password = ? WHERE id = ?";

            try (Connection conn = DBConnection.getConnection()) {
                int foundId = -1;
                try (PreparedStatement stmt1 = conn.prepareStatement(checkSql)) {
                    stmt1.setString(1, username);
                    stmt1.setString(2, license);
                    ResultSet rs = stmt1.executeQuery();
                    if (rs.next()) {
                        foundId = rs.getInt("id");
                    }
                }

                if (foundId != -1) {
                    try (PreparedStatement stmt2 = conn.prepareStatement(updateSql)) {
                        stmt2.setString(1, newPassword);
                        stmt2.setInt(2, foundId);
                        stmt2.executeUpdate();
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private JPanel buildPlainInputField(JTextField field) {
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(BG_INPUT);
            wrap.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, BORDER_INPUT),
                    new EmptyBorder(8, 12, 8, 12)
            ));
            wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            field.setOpaque(false);
            field.setBorder(null);
            field.setForeground(TEXT_LIGHT);
            field.setCaretColor(BRAND_BLUE);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            wrap.add(field, BorderLayout.CENTER);
            return wrap;
        }
    }


    private JPanel makeLeftAlignedRow(Component c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(BG_CARD);
        p.setMaximumSize(new Dimension(340, 25));
        p.add(c);
        return p;
    }

    private JLabel makeInputLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_LIGHT);
        return lbl;
    }

    private JPanel buildIconInputField(String icon, JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout(10, 0));
        wrap.setBackground(BG_INPUT);
        wrap.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_INPUT),
                new EmptyBorder(10, 15, 10, 15)
        ));

        wrap.setPreferredSize(new Dimension(340, 45));
        wrap.setMaximumSize(new Dimension(340, 45));
        wrap.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(TEXT_MUTED);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(BRAND_BLUE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        wrap.add(iconLabel, BorderLayout.WEST);
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

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