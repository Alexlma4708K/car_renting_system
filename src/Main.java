package ui;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                UIManager.put("Panel.background", new Color(24, 26, 27));
                UIManager.put("OptionPane.background", new Color(34, 36, 38));
                UIManager.put("OptionPane.messageForeground", new Color(230, 230, 230));


                UIManager.put("Button.focus", new Color(0, 0, 0, 0));
                UIManager.put("Button.border", BorderFactory.createEmptyBorder(8, 16, 8, 16));


                UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
                UIManager.put("Table.gridColor", new Color(55, 55, 55));
                UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
                UIManager.put("TextField.caretForeground", Color.WHITE);


                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");

            } catch (Exception e) {
                System.err.println("Failed to initialize premium theme.");
            }

            new LoginFrame();
        });
    }
}