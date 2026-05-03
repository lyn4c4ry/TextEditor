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
        add(buildFormatMenu());
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
                            EditorApp.getInstance().getDocument(),
                            EditorApp.getInstance().getCaretaker()
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

    private JMenu buildFormatMenu() {
        JMenu formatMenu = new JMenu("Format");

        JMenuItem boldItem = new JMenuItem("Bold");
        JMenuItem italicItem = new JMenuItem("Italic");
        JMenuItem boldItalicItem = new JMenuItem("Bold + Italic");

        // Keyboard shortcuts
        boldItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        italicItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));

        boldItem.addActionListener(e -> applyFormat(new BoldDecorator(new BaseFormatter())));
        italicItem.addActionListener(e -> applyFormat(new ItalicDecorator(new BaseFormatter())));
        boldItalicItem.addActionListener(e -> applyFormat(
                new BoldDecorator(new ItalicDecorator(new BaseFormatter()))
        ));

        formatMenu.add(boldItem);
        formatMenu.add(italicItem);
        formatMenu.add(boldItalicItem);

        return formatMenu;
    }

    // Decorator Pattern - applies formatting to selected text
    private void applyFormat(TextFormatter formatter) {
        String selected = textArea.getSelectedText();
        if (selected != null && !selected.isEmpty()) {
            String formatted = formatter.format(selected);
            textArea.replaceSelection(formatted);
        } else {
            JOptionPane.showMessageDialog(this, "Please select text first.");
        }
    }
}