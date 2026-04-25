package ui;

import dao.DBConnection;
import dao.BookingDAO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MyBookingsFrame extends JFrame {
    private JTable bookingsTable;
    private int currentUserId;

    private static final Color BG_APP        = new Color(10, 12, 18);
    private static final Color BG_CARD       = new Color(28, 30, 40);
    private static final Color BG_HEADER_TBL = new Color(18, 20, 28);
    private static final Color TEXT_LIGHT    = new Color(245, 245, 250);
    private static final Color TEXT_MUTED    = new Color(140, 145, 160);
    private static final Color BRAND_BLUE    = new Color(59, 130, 246);
    private static final Color BRAND_PURPLE  = new Color(168, 85, 247);
    private static final Color GREEN         = new Color(52, 199, 130);
    private static final Color BORDER_SOFT   = new Color(45, 50, 65);

    public MyBookingsFrame(int userId) {
        this.currentUserId = userId;

        setTitle("AutoFleet — My Bookings");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_APP);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_APP);
        headerPanel.setBorder(new EmptyBorder(30, 40, 20, 40));

        GradientTextLabel title = new GradientTextLabel("Booking History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Vehicle", "Start Date", "Total Paid", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        bookingsTable = new JTable(model);
        styleTable(bookingsTable);


        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(BG_APP);
        tableContainer.setBorder(new EmptyBorder(0, 40, 20, 40));

        JPanel cardInner = new JPanel(new BorderLayout());
        cardInner.setBackground(BG_CARD);
        cardInner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_SOFT),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane scroll = new JScrollPane(bookingsTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // Hidden sleek scrollbar

        cardInner.add(scroll, BorderLayout.CENTER);
        tableContainer.add(cardInner, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setBackground(BG_APP);
        footer.setBorder(new EmptyBorder(0, 40, 30, 40));

        JButton btnClose = new JButton("Close") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 50, 65));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setForeground(TEXT_LIGHT);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        JButton btnReturn = new GradientButton("Return Selected Car");
        btnReturn.addActionListener(e -> processReturn());

        footer.add(btnClose);
        footer.add(btnReturn);
        add(footer, BorderLayout.SOUTH);

        loadBookings();
    }


    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_LIGHT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_SOFT);
        table.setSelectionBackground(new Color(40, 50, 75));
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


        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, false, row, col);
                c.setBackground(sel ? new Color(40, 50, 75) : BG_CARD);
                ((JComponent) c).setBorder(new EmptyBorder(0, 15, 0, 15));

                String strVal = val != null ? val.toString() : "";
                if ("RETURNED".equals(strVal)) {
                    c.setForeground(GREEN);
                } else if ("BOOKED".equals(strVal)) {
                    c.setForeground(BRAND_BLUE);
                } else {
                    c.setForeground(TEXT_LIGHT);
                }
                return c;
            }
        });
    }

    private void loadBookings() {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0);

        String query = "SELECT b.id, c.brand, c.model, b.start_date, b.total_price, b.status " +
                "FROM bookings b JOIN cars c ON b.car_id = c.id " +
                "WHERE b.user_id = ? ORDER BY b.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String vehicle = rs.getString("brand") + " " + rs.getString("model");
                double total = rs.getDouble("total_price");
                String formattedPrice = String.format("₹ %,.2f", total);

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        vehicle,
                        rs.getString("start_date"),
                        formattedPrice,
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processReturn() {
        int selectedRow = bookingsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to return.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) bookingsTable.getValueAt(selectedRow, 4);
        if ("RETURNED".equals(status)) {
            JOptionPane.showMessageDialog(this, "This vehicle has already been returned.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to return this vehicle?", "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);

            String rawPrice = (String) bookingsTable.getValueAt(selectedRow, 3);
            double finalPrice = Double.parseDouble(rawPrice.replace("₹", "").replace(",", "").trim());

            BookingDAO dao = new BookingDAO();
            dao.finalizeReturn(bookingId, finalPrice);

            JOptionPane.showMessageDialog(this, "Vehicle returned successfully!");
            loadBookings();
        }
    }


    private class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setBorder(new EmptyBorder(10, 20, 10, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, BRAND_BLUE, getWidth(), 0, BRAND_PURPLE);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
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