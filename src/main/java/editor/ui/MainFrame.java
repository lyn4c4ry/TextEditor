package editor.ui;

import editor.app.EditorApp;
import editor.command.SaveFileCommand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window.
 * Shows unsaved indicator (*) in title and prompts to save on exit.
 */
public class MainFrame extends JFrame {

    private final EditorTextArea textArea;
    private final StatusBar statusBar;
    private final EditorToolBar toolBar;

    public MainFrame() {
        setTitle("Text Editor");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        textArea = new EditorTextArea();
        statusBar = new StatusBar();
        toolBar = new EditorToolBar(textArea);

        // Register status bar as observer
        EditorApp.getInstance().getDocument().addObserver(statusBar);

        // Update title when document changes
        EditorApp.getInstance().getDocument().addObserver((event, document) -> {
            String fileName = document.getFilePath() != null
                    ? new java.io.File(document.getFilePath()).getName()
                    : "Untitled";
            String modified = document.isModified() ? "* " : "";
            setTitle(modified + fileName + " - Text Editor");
        });

        // Ask to save on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        setJMenuBar(new EditorMenuBar(this, textArea));
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void handleExit() {
        if (EditorApp.getInstance().getDocument().isModified()) {
            String fileName = EditorApp.getInstance().getDocument().getFilePath() != null
                    ? new java.io.File(EditorApp.getInstance().getDocument().getFilePath()).getName()
                    : "Untitled";

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to save changes to " + fileName + "?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                EditorApp.getInstance().getCommandManager().executeCommand(
                        new SaveFileCommand(EditorApp.getInstance().getDocument())
                );
                dispose();
                System.exit(0);
            } else if (choice == JOptionPane.NO_OPTION) {
                dispose();
                System.exit(0);
            }
            // CANCEL - do nothing, stay in app
        } else {
            dispose();
            System.exit(0);
        }
    }

    public void launch() {
        setVisible(true);
        textArea.requestFocusInWindow();
    }
}