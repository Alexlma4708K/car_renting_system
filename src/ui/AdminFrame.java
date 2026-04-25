package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.DBConnection;
import model.User;

public class AdminFrame extends JFrame {

    private User admin;
    private JPanel cardPanel;
    private CardLayout cardLayout;


    private static final Color BG_APP        = new Color(10, 12, 18);
    private static final Color BG_CARD       = new Color(28, 30, 40);
    private static final Color BG_ROW_EVEN   = new Color(22, 24, 32);
    private static final Color BG_ROW_SEL    = new Color(40, 50, 75);
    private static final Color BG_HEADER_TBL = new Color(18, 20, 28);

    private static final Color TEXT_LIGHT    = new Color(245, 245, 250);
    private static final Color TEXT_MUTED    = new Color(140, 145, 160);
    private static final Color BRAND_BLUE    = new Color(59, 130, 246);
    private static final Color BRAND_PURPLE  = new Color(168, 85, 247);
    private static final Color DANGER_RED    = new Color(239, 68, 68);
    private static final Color DANGER_DARK   = new Color(185, 28, 28);
    private static final Color GREEN         = new Color(52, 199, 130);
    private static final Color BORDER_SOFT   = new Color(45, 50, 65);

    public AdminFrame(User admin) {
        this.admin = admin;

        setTitle("AutoFleet — Admin Control Panel");
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_APP);

        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(BG_APP);
        navPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel logoLabel = new JLabel("Admin Control Panel");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(TEXT_LIGHT);
        navPanel.add(logoLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton bookingsBtn = buildNavButton("View All Bookings");
        JButton usersBtn = buildNavButton("Manage Users");
        JButton fleetBtn = buildNavButton("Manage Fleet");
        JButton logoutBtn = buildNavButton("Log Out");

        btnPanel.add(bookingsBtn);
        btnPanel.add(usersBtn);
        btnPanel.add(fleetBtn);
        btnPanel.add(logoutBtn);
        navPanel.add(btnPanel, BorderLayout.EAST);
        add(navPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BG_APP);
        cardPanel.setBorder(new EmptyBorder(10, 30, 30, 30));

        cardPanel.add(buildBookingsPanel(), "BOOKINGS");
        cardPanel.add(buildUsersPanel(), "USERS");
        cardPanel.add(buildFleetPanel(), "FLEET");

        add(cardPanel, BorderLayout.CENTER);


        bookingsBtn.addActionListener(e -> { cardLayout.show(cardPanel, "BOOKINGS"); refreshBookings(); });
        usersBtn.addActionListener(e -> { cardLayout.show(cardPanel, "USERS"); refreshUsers(); });
        fleetBtn.addActionListener(e -> { cardLayout.show(cardPanel, "FLEET"); refreshFleet(); });
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame(); });

        setLocationRelativeTo(null);
        setVisible(true);
    }


    private JTable bookingsTable;
    private JPanel buildBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_APP);

        JLabel title = new JLabel("Global Booking History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);

        bookingsTable = new JTable();
        styleTable(bookingsTable);
        panel.add(buildTableScrollWrapper(bookingsTable), BorderLayout.CENTER);

        refreshBookings();
        return panel;
    }

    private void refreshBookings() {
        String query = "SELECT b.id, u.username, c.brand, c.model, b.days, b.start_date, b.status " +
                "FROM bookings b JOIN users u ON b.user_id = u.id JOIN cars c ON b.car_id = c.id " +
                "ORDER BY b.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            String[] cols = {"Booking ID", "Customer", "Car", "Days", "Start Date", "Status"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("brand") + " " + rs.getString("model"),
                        rs.getInt("days"),
                        rs.getString("start_date"),
                        rs.getString("status")
                });
            }
            bookingsTable.setModel(model);
            applyTableRenderers(bookingsTable);

        } catch (Exception e) { e.printStackTrace(); }
    }


    private JTable usersTable;
    private JPanel buildUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_APP);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(BG_APP);

        JLabel title = new JLabel("Registered Users");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_LIGHT);

        JButton removeUserBtn = new DangerButton("Remove Selected User");
        removeUserBtn.setPreferredSize(new Dimension(180, 35));
        removeUserBtn.addActionListener(e -> deleteSelectedUser());

        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(removeUserBtn, BorderLayout.EAST);
        panel.add(headerRow, BorderLayout.NORTH);

        usersTable = new JTable();
        styleTable(usersTable);
        panel.add(buildTableScrollWrapper(usersTable), BorderLayout.CENTER);

        refreshUsers();
        return panel;
    }

    private void refreshUsers() {
        String query = "SELECT id, username, license_number, role FROM users ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            String[] cols = {"User ID", "Username", "License Number", "Role"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("license_number") == null ? "Not Provided" : rs.getString("license_number"),
                        rs.getString("role").toUpperCase()
                });
            }
            usersTable.setModel(model);
            applyTableRenderers(usersTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deleteSelectedUser() {
        int row = usersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to remove.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) usersTable.getValueAt(row, 0);
        String role = (String) usersTable.getValueAt(row, 3);

        if ("ADMIN".equals(role)) {
            JOptionPane.showMessageDialog(this, "Cannot remove an administrator account.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete User ID " + userId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
                refreshUsers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete user. They may have active or past booking records tied to their account.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private JTable fleetTable;
    private JPanel buildFleetPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_APP);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(BG_APP);

        JLabel title = new JLabel("Fleet Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_LIGHT);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnGroup.setBackground(BG_APP);

        JButton removeCarBtn = new DangerButton("Remove Selected Vehicle");
        removeCarBtn.setPreferredSize(new Dimension(190, 35));
        removeCarBtn.addActionListener(e -> deleteSelectedCar());

        JButton addCarBtn = new GradientButton("+ Add New Vehicle");
        addCarBtn.setPreferredSize(new Dimension(160, 35));
        addCarBtn.addActionListener(e -> {
            new AddCarDialog(this).setVisible(true);
            refreshFleet();
        });

        btnGroup.add(removeCarBtn);
        btnGroup.add(addCarBtn);

        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(btnGroup, BorderLayout.EAST);
        panel.add(headerRow, BorderLayout.NORTH);

        fleetTable = new JTable();
        styleTable(fleetTable);
        panel.add(buildTableScrollWrapper(fleetTable), BorderLayout.CENTER);

        refreshFleet();
        return panel;
    }

    private void refreshFleet() {
        String query = "SELECT id, brand, model, price, available, image_filename FROM cars ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            String[] cols = {"Car ID", "Brand", "Model", "Price/Day", "Availability", "Image File"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };

            while (rs.next()) {
                boolean available = rs.getInt("available") == 1;
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getDouble("price"),
                        available ? "AVAILABLE" : "RENTED",
                        rs.getString("image_filename")
                });
            }
            fleetTable.setModel(model);
            applyTableRenderers(fleetTable);

        } catch (Exception e) { e.printStackTrace(); }
    }


    private void deleteSelectedCar() {
        int row = fleetTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to remove.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int carId = (int) fleetTable.getValueAt(row, 0);
        String status = (String) fleetTable.getValueAt(row, 4);

        if ("RENTED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Cannot remove a vehicle that is currently rented.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete Car ID " + carId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM cars WHERE id = ?")) {
                stmt.setInt(1, carId);
                stmt.executeUpdate();
                refreshFleet();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete vehicle. It may be tied to existing booking history.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_LIGHT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_SOFT);
        table.setSelectionBackground(BG_ROW_SEL);
        table.setSelectionForeground(TEXT_LIGHT);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setOpaque(false);
        header.setBackground(BG_HEADER_TBL);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setOpaque(true);
                label.setBackground(BG_HEADER_TBL);
                label.setForeground(TEXT_MUTED);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 0, BORDER_SOFT),
                        new EmptyBorder(0, 15, 0, 15)
                ));
                return label;
            }
        });
    }

    private void applyTableRenderers(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, false, row, col);
                c.setBackground(sel ? BG_ROW_SEL : (row % 2 == 0 ? BG_CARD : BG_ROW_EVEN));
                ((JComponent) c).setBorder(new EmptyBorder(0, 15, 0, 15));

                String strVal = val != null ? val.toString() : "";

                if ("AVAILABLE".equals(strVal) || "RETURNED".equals(strVal)) {
                    c.setForeground(GREEN);
                } else if ("RENTED".equals(strVal) || "BOOKED".equals(strVal) || "ACTIVE".equals(strVal)) {
                    c.setForeground(BRAND_BLUE);
                } else if (strVal.equals("ADMIN")) {
                    c.setForeground(BRAND_PURPLE);
                } else {
                    c.setForeground(TEXT_LIGHT);
                }
                return c;
            }
        });
    }

    private JPanel buildTableScrollWrapper(JTable table) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_CARD);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_SOFT),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // Hidden sleek scrollbar

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
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

    private class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, BRAND_BLUE, getWidth(), 0, BRAND_PURPLE);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class DangerButton extends JButton {
        public DangerButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, DANGER_RED, getWidth(), 0, DANGER_DARK);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
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