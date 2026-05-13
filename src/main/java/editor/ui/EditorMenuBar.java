package editor.ui;

import editor.app.EditorApp;
import editor.command.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Menu bar for the editor.
 * Uses Command pattern for file operations.
 * Uses Decorator pattern for text formatting with state-based UI synchronization.
 */
public class EditorMenuBar extends JMenuBar {

    private final EditorTextArea textArea;
    private JCheckBoxMenuItem boldItem;
    private JCheckBoxMenuItem italicItem;

    public EditorMenuBar(JFrame parent, EditorTextArea textArea) {
        this.textArea = textArea;
        add(buildFileMenu());
        add(buildEditMenu());
        add(buildFormatMenu());
    }

    private JMenu buildFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Shortcuts for file operations
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        openItem.addActionListener(e -> {
            EditorApp.getInstance().getCommandManager().executeCommand(
                    new OpenFileCommand(EditorApp.getInstance().getDocument())
            );
            textArea.syncFromModel();
        });

        saveItem.addActionListener(e ->
                EditorApp.getInstance().getCommandManager().executeCommand(
                        new SaveFileCommand(EditorApp.getInstance().getDocument())
                )
        );

        saveAsItem.addActionListener(e ->
                EditorApp.getInstance().getCommandManager().executeCommand(
                        new SaveAsFileCommand(EditorApp.getInstance().getDocument())
                )
        );

        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsFileMenuItem(saveAsItem)); // Added for completeness
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        return fileMenu;
    }

    // Small helper for Save As to keep buildFileMenu clean
    private JMenuItem saveAsFileMenuItem(JMenuItem item) {
        return item;
    }

    private JMenu buildEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        JMenuItem findItem = new JMenuItem("Find");

        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));

        undoItem.addActionListener(e -> {
            if (textArea.getUndoManager().canUndo()) textArea.getUndoManager().undo();
        });

        redoItem.addActionListener(e -> {
            if (textArea.getUndoManager().canRedo()) textArea.getUndoManager().redo();
        });

        findItem.addActionListener(e -> {
            FindDialog dialog = new FindDialog((JFrame) SwingUtilities.getWindowAncestor(this), textArea);
            dialog.setVisible(true);
        });

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(findItem);

        return editMenu;
    }

    /**
     * Builds Format menu using JCheckBoxMenuItems to provide visual synchronization with the text's font state.
     */
    private JMenu buildFormatMenu() {
        JMenu formatMenu = new JMenu("Format");

        boldItem = new JCheckBoxMenuItem("Bold");
        italicItem = new JCheckBoxMenuItem("Italic");
        JMenuItem underlineItem = new JMenuItem("Underline");

        boldItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        italicItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        underlineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));

        // Use the applyFormat method to handle logic and UI sync
        boldItem.addActionListener(e -> applyFormat("bold"));
        italicItem.addActionListener(e -> applyFormat("italic"));
        underlineItem.addActionListener(e -> applyFormat("underline"));

        formatMenu.add(boldItem);
        formatMenu.add(italicItem);
        formatMenu.add(underlineItem);

        return formatMenu;
    }

    /**
     * Decorator Pattern - applies formatting and synchronizes CheckBox states with the actual text area style.
     * Uses isStyleActive() from EditorTextArea for reliable state checking.
     */
    private void applyFormat(String type) {
        if (textArea == null) return;

        switch (type) {
            case "bold" -> {
                textArea.toggleBold();
                // Sync the checkbox with the actual font state after toggle
                boldItem.setSelected(textArea.isStyleActive(Font.BOLD));
            }
            case "italic" -> {
                textArea.toggleItalic();
                // Sync the checkbox with the actual font state after toggle
                italicItem.setSelected(textArea.isStyleActive(Font.ITALIC));
            }
            case "underline" -> {
                textArea.toggleUnderline();
                // Underline is usually a plain JMenuItem here, but logic remains consistent
            }
        }

        // Return focus to the text area to allow immediate typing with the new style
        textArea.requestFocusInWindow();
    }
}