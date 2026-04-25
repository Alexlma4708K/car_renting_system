package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptDialog extends JDialog {

    private static final Color BG_APP = new Color(8, 10, 15);
    private static final Color BG_CARD = new Color(22, 26, 35);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(150, 160, 180);
    private static final Color BRAND_BLUE = new Color(59, 130, 246);
    private static final Color BRAND_PURPLE = new Color(168, 85, 247);
    private static final Color AMBER = new Color(245, 158, 11);

    public ReceiptDialog(Frame owner, String transactionType, String bookingId, String carDetails, String totalAmount) {
        super(owner, true);
        setSize(400, 620);
        setLayout(new BorderLayout());
        setUndecorated(true);
        ((JComponent) getContentPane()).setBorder(new LineBorder(BRAND_BLUE, 1));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel logo = new JLabel("AutoFleet");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(TEXT_LIGHT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel typeLbl = new JLabel(transactionType);
        typeLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLbl.setForeground(BRAND_BLUE);
        typeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dateLbl = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm a")));
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(TEXT_MUTED);
        dateLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel details = new JPanel(new GridLayout(3, 2, 0, 15));
        details.setBackground(BG_CARD);
        addDetail(details, "Receipt No.", "INV-" + bookingId + "88");
        addDetail(details, "Vehicle", carDetails);
        addDetail(details, "Payment", "PAY ON DELIVERY");

        JLabel totalLbl = new JLabel(totalAmount);
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        totalLbl.setForeground(TEXT_LIGHT);
        totalLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel terms = new JPanel();
        terms.setLayout(new BoxLayout(terms, BoxLayout.Y_AXIS));
        terms.setBackground(new Color(30, 35, 45));
        terms.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(50, 55, 70), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        terms.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel h = new JLabel("⚠️ RENTAL TERMS");
        h.setForeground(AMBER);
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel b = new JLabel("<html><body style='text-align: center; font-family: Segoe UI;'>" +
                "<div style='margin-bottom: 5px;'><b>Late Return:</b> ₹500/hr penalty applies.</div>" +
                "<div><b>Early Return:</b> No refunds for unused time.</div>" +
                "</body></html>");
        b.setForeground(TEXT_MUTED);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);

        terms.add(h);
        terms.add(Box.createVerticalStrut(10));
        terms.add(b);

        JButton done = new GradientButton("Done");
        done.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        done.setAlignmentX(Component.CENTER_ALIGNMENT);
        done.addActionListener(e -> dispose());

        card.add(logo); card.add(Box.createVerticalStrut(5));
        card.add(typeLbl); card.add(Box.createVerticalStrut(5));
        card.add(dateLbl); card.add(Box.createVerticalStrut(20));
        card.add(new JSeparator()); card.add(Box.createVerticalStrut(20));
        card.add(details); card.add(Box.createVerticalStrut(20));
        card.add(new JSeparator()); card.add(Box.createVerticalStrut(20));
        card.add(totalLbl); card.add(Box.createVerticalStrut(30));
        card.add(terms); card.add(Box.createVerticalGlue());
        card.add(done);

        add(card);
        setLocationRelativeTo(owner);
    }

    private void addDetail(JPanel p, String l, String v) {
        JLabel lbl = new JLabel(l);
        lbl.setForeground(TEXT_MUTED);
        JLabel val = new JLabel(v);
        val.setForeground(TEXT_LIGHT);
        val.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(lbl);
        p.add(val);
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
            GradientPaint gp = new GradientPaint(0, 0, BRAND_BLUE, getWidth(), 0, BRAND_PURPLE);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}