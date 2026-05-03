package editor.ui;

import editor.app.EditorApp;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 * Contains the text area, menu bar and status bar.
 */
public class MainFrame extends JFrame {

    private final EditorTextArea textArea;
    private final StatusBar statusBar;

    public MainFrame() {
        setTitle("Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize components
        textArea = new EditorTextArea();
        statusBar = new StatusBar();

        // Register status bar as observer
        EditorApp.getInstance().getDocument().addObserver(statusBar);

        // Set up layout
        setJMenuBar(new EditorMenuBar(this, textArea));
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    public void launch() {
        setVisible(true);
    }
}