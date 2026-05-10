package editor.ui;

import editor.app.EditorApp;
import editor.command.*;
import editor.decorator.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Menu bar for the editor.
 * Uses Command pattern for file operations.
 * Uses Decorator pattern for text formatting.
 */
public class EditorMenuBar extends JMenuBar {

    private final EditorTextArea textArea;

    public EditorMenuBar(JFrame parent, EditorTextArea textArea) {
        this.textArea = textArea;
        add(buildFileMenu());
        add(buildEditMenu());
    }

    private JMenu buildFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Keyboard shortcuts
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        openItem.addActionListener(e -> {
            EditorApp.getInstance().getCommandManager().executeCommand(
                    new OpenFileCommand(
                            EditorApp.getInstance().getDocument()
                    )
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

        // Keyboard shortcuts
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));

        undoItem.addActionListener(e -> {
            if (textArea.getUndoManager().canUndo()) {
                textArea.getUndoManager().undo();
            }
        });

        redoItem.addActionListener(e -> {
            if (textArea.getUndoManager().canRedo()) {
                textArea.getUndoManager().redo();
            }
        });

        findItem.addActionListener(e -> {
            FindDialog dialog = new FindDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    textArea
            );
            dialog.setVisible(true);
        });

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(findItem);

        return editMenu;
    }

    // Decorator Pattern - applies formatting to selected text
    private void applyFormat(String type) {
        switch (type) {
            case "bold" -> textArea.toggleBold();
            case "italic" -> textArea.toggleItalic();
            case "bolditalic" -> {
                textArea.toggleBold();
                textArea.toggleItalic();
            }
        }
    }
}