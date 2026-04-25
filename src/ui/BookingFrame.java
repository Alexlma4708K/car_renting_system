package ui;

import dao.BookingDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BookingFrame extends JFrame {

    private static final Color BG_APP = new Color(8, 10, 15);
    private static final Color BG_CARD = new Color(22, 26, 35);
    private static final Color BG_PANEL = new Color(30, 35, 45);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(150, 160, 180);
    private static final Color BRAND_BLUE = new Color(59, 130, 246);
    private static final Color BRAND_PURPLE = new Color(168, 85, 247);

    private int currentUserId;
    private int selectedCarId;
    private String carName;
    private double carDailyRate;
    private int rentalDays = 1;

    private JLabel totalLabel;
    private JCheckBox chauffeurBox;
    private JCheckBox waiverBox;
    private JSpinner daysSpinner;


    public BookingFrame(int userId, int carId, String carName, double dailyRate, String imagePath) {
        this.currentUserId = userId;
        this.selectedCarId = carId;
        this.carName = carName;
        this.carDailyRate = dailyRate;

        setTitle("Complete Your Booking");
        setSize(850, 550); // Made slightly wider for the image
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_APP);
        setLayout(new BorderLayout());


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(BG_APP);
        headerPanel.setBorder(new EmptyBorder(15, 20, 0, 20));
        JLabel backLabel = new JLabel("← Back to Fleet");
        backLabel.setForeground(BRAND_BLUE);
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { dispose(); }
        });
        headerPanel.add(backLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_APP);
        mainPanel.setBorder(new EmptyBorder(10, 30, 30, 30));


        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_APP);
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 20));

        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(350, 230, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));

            JLabel hubLabel = new JLabel(" Greater Noida Hub ", SwingConstants.CENTER);
            hubLabel.setOpaque(true);
            hubLabel.setBackground(new Color(40, 45, 55));
            hubLabel.setForeground(TEXT_LIGHT);
            hubLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            hubLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

            JPanel imgWrapper = new JPanel(new BorderLayout());
            imgWrapper.setBackground(BG_APP);
            imgWrapper.add(imageLabel, BorderLayout.CENTER);
            imgWrapper.add(hubLabel, BorderLayout.SOUTH);

            leftPanel.add(imgWrapper, BorderLayout.NORTH);
        } catch (Exception e) {
            System.out.println("Could not load image: " + imagePath);
        }
        mainPanel.add(leftPanel, BorderLayout.WEST);


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BG_APP);

        JLabel titleLbl = new JLabel(carName);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLbl.setForeground(TEXT_LIGHT);

        JLabel priceLbl = new JLabel(String.format("₹%,.0f / PER DAY", dailyRate));
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLbl.setForeground(BRAND_PURPLE);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BG_APP);
        titlePanel.add(titleLbl, BorderLayout.WEST);
        titlePanel.add(priceLbl, BorderLayout.EAST);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel descLbl = new JLabel("<html><p style='color:#96A0B4; font-family:Segoe UI;'>Command the road with premium comfort and advanced tech. Perfect for outstation family trips or commanding a presence in the city.</p></html>");
        descLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durationPanel.setBackground(BG_APP);
        JLabel daysLbl = new JLabel("Rental Duration (Days): ");
        daysLbl.setForeground(TEXT_LIGHT);
        daysLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 30, 1);
        daysSpinner = new JSpinner(spinnerModel);
        daysSpinner.setPreferredSize(new Dimension(80, 30));
        daysSpinner.addChangeListener(e -> {
            rentalDays = (int) daysSpinner.getValue();
            updateEstimatedTotal();
        });

        durationPanel.add(daysLbl);
        durationPanel.add(daysSpinner);

        JPanel addonsCard = new JPanel();
        addonsCard.setLayout(new BoxLayout(addonsCard, BoxLayout.Y_AXIS));
        addonsCard.setBackground(BG_CARD);
        addonsCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BG_PANEL, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel addonsTitle = new JLabel("Optional Add-ons");
        addonsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addonsTitle.setForeground(TEXT_MUTED);

        chauffeurBox = createCustomCheckBox("Experienced Chauffeur (12 hrs/day)", "+₹1,000/d");
        waiverBox = createCustomCheckBox("Zero Dep Damage Waiver", "+₹499/d");

        addonsCard.add(addonsTitle);
        addonsCard.add(Box.createVerticalStrut(15));
        addonsCard.add(chauffeurBox);
        addonsCard.add(Box.createVerticalStrut(10));
        addonsCard.add(waiverBox);

        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(BG_APP);
        checkoutPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel estText = new JLabel("Estimated Total");
        estText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        estText.setForeground(TEXT_LIGHT);

        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        totalLabel.setForeground(TEXT_LIGHT);

        JPanel totalInfoPanel = new JPanel(new BorderLayout());
        totalInfoPanel.setBackground(BG_APP);
        totalInfoPanel.add(estText, BorderLayout.WEST);
        totalInfoPanel.add(totalLabel, BorderLayout.EAST);

        JButton btnConfirm = new GradientButton("Confirm Booking");
        btnConfirm.setPreferredSize(new Dimension(200, 45));

        btnConfirm.addActionListener(e -> {
            try {
                String rawTotal = totalLabel.getText();
                String cleanNum = rawTotal.replace("₹", "").replace(",", "").trim();
                double finalCalculatedTotal = Double.parseDouble(cleanNum);

                BookingDAO dao = new BookingDAO();
                int newId = dao.saveBooking(currentUserId, selectedCarId, rentalDays, finalCalculatedTotal);

                if (newId != -1) {
                    new ReceiptDialog(this, "BOOKING CONFIRMED", String.valueOf(newId), carName, rawTotal).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error processing booking.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        checkoutPanel.add(totalInfoPanel, BorderLayout.CENTER);
        checkoutPanel.add(Box.createVerticalStrut(15), BorderLayout.NORTH);
        checkoutPanel.add(btnConfirm, BorderLayout.SOUTH);

        rightPanel.add(titlePanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(descLbl);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(durationPanel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(addonsCard);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(checkoutPanel);

        mainPanel.add(rightPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        updateEstimatedTotal();
    }

    private void updateEstimatedTotal() {
        double baseTotal = carDailyRate * rentalDays;
        double addonsTotal = 0;

        if (chauffeurBox.isSelected()) addonsTotal += 1000 * rentalDays;
        if (waiverBox.isSelected()) addonsTotal += 499 * rentalDays;

        double grandTotal = baseTotal + addonsTotal;
        totalLabel.setText(String.format("₹%,.0f", grandTotal));
    }

    private JCheckBox createCustomCheckBox(String text, String priceText) {
        JCheckBox box = new JCheckBox(text);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setForeground(TEXT_LIGHT);
        box.setBackground(BG_CARD);
        box.setFocusPainted(false);
        box.addActionListener(e -> updateEstimatedTotal());
        return box;
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