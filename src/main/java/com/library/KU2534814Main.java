package com.library;

import com.library.gui.KU2534814LibraryGUI;
import com.library.cli.KU2534814LibraryCLI;

import javax.swing.SwingUtilities;

public class KU2534814Main {
    public static void main(String[] args) {
        // Check if CLI mode is requested via command line argument
        boolean cliMode = false;
        
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--cli") || args[0].equalsIgnoreCase("-c")) {
                cliMode = true;
            } else if (args[0].equalsIgnoreCase("--gui") || args[0].equalsIgnoreCase("-g")) {
                cliMode = false;
            }
        }
        
        if (cliMode) {
            // Launch CLI Mode
            System.out.println("================================================================================");
            System.out.println("           SMART LIBRARY MANAGEMENT SYSTEM - KU2534814");
            System.out.println("                         CLI MODE");
            System.out.println("================================================================================");
            System.out.println();
            KU2534814LibraryCLI cli = new KU2534814LibraryCLI();
            cli.start();
        } else {
            // Launch GUI Mode (Default)
            System.out.println("================================================================================");
            System.out.println("           SMART LIBRARY MANAGEMENT SYSTEM - KU2534814");
            System.out.println("                         GUI MODE");
            System.out.println("================================================================================");
            System.out.println();
            System.out.println("Launching GUI...");
            System.out.println("To run CLI mode, use: java -jar smart-library.jar --cli");
            System.out.println();
            
            SwingUtilities.invokeLater(() -> {
                try {
                    // Set system look and feel
                    javax.swing.UIManager.setLookAndFeel(
                        javax.swing.UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                KU2534814LibraryGUI gui = new KU2534814LibraryGUI();
                gui.setVisible(true);
            });
        }
    }
}
