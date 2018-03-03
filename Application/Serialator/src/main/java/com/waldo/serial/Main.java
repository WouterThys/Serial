package com.waldo.serial;

import com.waldo.serial.gui.Application;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        String startUpPath = new File("").getAbsolutePath() + File.separator;

        SwingUtilities.invokeLater(() -> {
            setLookAndFeel();
            Application app = new Application(startUpPath);
            app.setTitle("Serialator");
            app.setLocationByPlatform(true);
            app.setPreferredSize(new Dimension(1000, 600));
            app.setMinimumSize(new Dimension(800, 400));
            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.initializeComponents();
            app.initializeLayouts();
            app.updateComponents();
            app.pack();

            app.setVisible(true);
        });
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel() {
                @Override
                public UIDefaults getDefaults() {
                    UIDefaults defaults = super.getDefaults();

                    defaults.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 15));

                    return defaults;
                }
            });
        } catch (UnsupportedLookAndFeelException e) {
           e.printStackTrace();
        }
    }
}
