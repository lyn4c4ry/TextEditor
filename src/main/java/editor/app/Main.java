package editor.app;

import editor.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point of the application.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.launch();
        });
    }
}