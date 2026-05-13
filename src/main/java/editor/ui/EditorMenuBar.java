package editor.ui;

import editor.app.EditorApp;
import editor.command.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Menu bar handling File (Open, Save, Save As), Edit (Undo, Redo, Find),
 * and Format (Bold, Italic, Underline) with synced checkboxes.
 */
public class EditorMenuBar extends JMenuBar {

    private final EditorTextArea textArea;
    private JCheckBoxMenuItem boldItem;
    private JCheckBoxMenuItem italicItem;
    private JCheckBoxMenuItem underlineItem;

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

        // Accelerators for File operations
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        openItem.addActionListener(e -> {
            EditorApp.getInstance().getCommandManager().executeCommand(new OpenFileCommand(EditorApp.getInstance().getDocument()));
            textArea.syncFromModel();
        });

        saveItem.addActionListener(e ->
                EditorApp.getInstance().getCommandManager().executeCommand(new SaveFileCommand(EditorApp.getInstance().getDocument()))
        );

        saveAsItem.addActionListener(e ->
                EditorApp.getInstance().getCommandManager().executeCommand(new SaveAsFileCommand(EditorApp.getInstance().getDocument()))
        );

        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu buildEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        JMenuItem findItem = new JMenuItem("Find");

        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));

        undoItem.addActionListener(e -> { if (textArea.getUndoManager().canUndo()) textArea.getUndoManager().undo(); });
        redoItem.addActionListener(e -> { if (textArea.getUndoManager().canRedo()) textArea.getUndoManager().redo(); });

        findItem.addActionListener(e -> new FindDialog((JFrame) SwingUtilities.getWindowAncestor(this), textArea).setVisible(true));

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(findItem);

        return editMenu;
    }

    private JMenu buildFormatMenu() {
        JMenu formatMenu = new JMenu("Format");

        boldItem = new JCheckBoxMenuItem("Bold");
        italicItem = new JCheckBoxMenuItem("Italic");
        underlineItem = new JCheckBoxMenuItem("Underline");

        boldItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        italicItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        underlineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));

        // Listeners that toggle style and then sync UI state
        boldItem.addActionListener(e -> applyFormat("bold"));
        italicItem.addActionListener(e -> applyFormat("italic"));
        underlineItem.addActionListener(e -> applyFormat("underline"));

        formatMenu.add(boldItem);
        formatMenu.add(italicItem);
        formatMenu.add(underlineItem);

        return formatMenu;
    }

    /**
     * Executes style changes and ensures checkbox selection matches document state.
     */
    private void applyFormat(String type) {
        if (textArea == null) return;

        switch (type) {
            case "bold" -> textArea.toggleBold();
            case "italic" -> textArea.toggleItalic();
            case "underline" -> textArea.toggleUnderline();
        }

        syncCheckboxes();
    }

    /**
     * Synchronizes the menu checkboxes with the actual boolean states in EditorTextArea.
     */
    private void syncCheckboxes() {
        boldItem.setSelected(textArea.isBoldActive());
        italicItem.setSelected(textArea.isItalicActive());
        underlineItem.setSelected(textArea.isUnderlineActive());

        // Refresh UI state
        this.repaint();
        textArea.requestFocusInWindow();
    }
}