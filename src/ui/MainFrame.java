package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import dao.DBConnection;
import model.User;

public class MainFrame extends JFrame {

    private User user;
    private JPanel fleetGrid;
    private JButton myBookingsBtn, logoutBtn;

    private static final Color BG_APP        = new Color(10, 12, 18);
    private static final Color BG_CARD       = new Color(28, 30, 40);
    private static final Color TEXT_LIGHT    = new Color(245, 245, 250);
    private static final Color TEXT_MUTED    = new Color(140, 145, 160);
    private static final Color BRAND_BLUE    = new Color(59, 130, 246);
    private static final Color BRAND_PURPLE  = new Color(168, 85, 247);
    private static final Color BORDER_SOFT   = new Color(45, 50, 65);

    public MainFrame(User user) {
        this.user = user;

        setTitle("AutoFleet — Choose Your Ride");
        setSize(1250, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_APP);

        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(BG_APP);
        navPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel logoLabel = new JLabel("AutoFleet");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoLabel.setForeground(TEXT_LIGHT);
        navPanel.add(logoLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        btnPanel.setOpaque(false);
        myBookingsBtn = buildNavButton("My Bookings");
        logoutBtn = buildNavButton("Log Out");
        btnPanel.add(myBookingsBtn);
        btnPanel.add(logoutBtn);
        navPanel.add(btnPanel, BorderLayout.EAST);
        add(navPanel, BorderLayout.NORTH);


        JPanel centeringWrapper = new JPanel(new GridBagLayout());
        centeringWrapper.setBackground(BG_APP);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout(0, 30));
        mainContent.setBackground(BG_APP);
        mainContent.setBorder(new EmptyBorder(30, 0, 50, 0));


        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 8));
        titleArea.setBackground(BG_APP);
        GradientTextLabel title = new GradientTextLabel("Choose Your Ride");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        JLabel subtitle = new JLabel("Premium vehicles available in Greater Noida Hub.");
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        titleArea.add(title);
        titleArea.add(subtitle);
        mainContent.add(titleArea, BorderLayout.NORTH);


        fleetGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        fleetGrid.setBackground(BG_APP);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(BG_APP);
        gridWrapper.add(fleetGrid, BorderLayout.NORTH);

        mainContent.add(gridWrapper, BorderLayout.CENTER);

        centeringWrapper.add(mainContent, gbc);

        JScrollPane scrollPane = new JScrollPane(centeringWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getViewport().setBackground(BG_APP);
        add(scrollPane, BorderLayout.CENTER);

        setupActions();
        loadCars();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupActions() {
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame(); });

        myBookingsBtn.addActionListener(e -> {
            MyBookingsFrame historyFrame = new MyBookingsFrame(user.getId());
            historyFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    loadCars();
                }
            });
            historyFrame.setVisible(true);
        });
    }

    public void loadCars() {
        fleetGrid.removeAll();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cars");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fleetGrid.add(new CarCard(rs.getInt("id"), rs.getString("brand"), rs.getString("model"),
                        rs.getDouble("price"), rs.getInt("available") == 1, rs.getString("image_filename")));
            }
            fleetGrid.revalidate();
            fleetGrid.repaint();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private class CarCard extends JPanel {
        public CarCard(int carId, String brand, String model, double price, boolean available, String imageFilename) {
            setLayout(new BorderLayout());
            setBackground(BG_CARD);

            setPreferredSize(new Dimension(340, 420));
            setBorder(BorderFactory.createLineBorder(BORDER_SOFT, 1, true));

            JPanel imgPanel = new JPanel(new BorderLayout());
            imgPanel.setBackground(new Color(20, 22, 30));
            imgPanel.setPreferredSize(new Dimension(340, 200));

            File imgFile = new File("assets/" + imageFilename);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(340, 200, Image.SCALE_SMOOTH);
                imgPanel.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
            }
            add(imgPanel, BorderLayout.NORTH);

            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBackground(BG_CARD);
            body.setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel name = new JLabel(brand + " " + model);
            name.setFont(new Font("Segoe UI", Font.BOLD, 22));
            name.setForeground(TEXT_LIGHT);

            JLabel rate = new JLabel(String.format("₹%,.0f / day", price));
            rate.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            rate.setForeground(TEXT_MUTED);

            body.add(name);
            body.add(Box.createVerticalStrut(5));
            body.add(rate);
            body.add(Box.createVerticalGlue());


            JPanel footer = new JPanel(new BorderLayout());
            footer.setBackground(BG_CARD);

            JButton btn = new CircularGradientButton(available ? ">" : "✕");
            btn.setEnabled(available);

            // 🔥 THE FIX: Wrap the button so it stays a circle and doesn't stretch!
            JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnWrapper.setBackground(BG_CARD);
            btnWrapper.add(btn);

            btn.addActionListener(e -> {
                BookingFrame bFrame = new BookingFrame(user.getId(), carId, brand + " " + model, price, "assets/" + imageFilename);
                bFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        loadCars();
                    }
                });
                bFrame.setVisible(true);
            });

            footer.add(btnWrapper, BorderLayout.EAST);
            body.add(footer);

            add(body, BorderLayout.CENTER);
        }
    }


    private JButton buildNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_MUTED);
        btn.setBorder(new EmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(TEXT_LIGHT); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(TEXT_MUTED); }
        });
        return btn;
    }

    private class CircularGradientButton extends JButton {
        public CircularGradientButton(String text) {
            super(text);
            setPreferredSize(new Dimension(45, 45));
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setForeground(Color.WHITE);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isEnabled()) {
                g2.setPaint(new GradientPaint(0, 0, BRAND_BLUE, getWidth(), getHeight(), BRAND_PURPLE));
            } else {
                g2.setColor(new Color(60, 65, 80));
            }
            g2.fillOval(0, 0, getWidth(), getHeight());
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
            FontMetrics fm = g2.getFontMetrics(getFont());
            g2.setPaint(new GradientPaint(0, 0, BRAND_BLUE, fm.stringWidth(getText()), 0, BRAND_PURPLE));
            g2.setFont(getFont());
            g2.drawString(getText(), 0, fm.getAscent());
            g2.dispose();
        }
    }
}