package editor.ui;

import editor.app.EditorApp;
import editor.model.Document;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.UndoManager;
import java.awt.event.*;

/**
 * Main text area of the editor.
 * Uses Swing's UndoManager for undo/redo of typing operations.
 */
public class EditorTextArea extends JTextArea {

    private final Document document;
    private final UndoManager undoManager;
    private boolean isSyncing = false;

    public EditorTextArea() {
        this.document = EditorApp.getInstance().getDocument();
        this.undoManager = new UndoManager();

        setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
        setLineWrap(true);
        setWrapStyleWord(true);

        // Register undo manager to Swing's document
        getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Sync changes to our Document model
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isSyncing) syncToModel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isSyncing) syncToModel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        // Ctrl+Z - Undo
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) undoManager.undo();
            }
        });

        // Ctrl+Y - Redo
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) undoManager.redo();
            }
        });
    }

    private void syncToModel() {
        isSyncing = true;
        document.setContent(getText());
        isSyncing = false;
    }

    public void syncFromModel() {
        isSyncing = true;
        setText(document.getContent());
        isSyncing = false;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }
}