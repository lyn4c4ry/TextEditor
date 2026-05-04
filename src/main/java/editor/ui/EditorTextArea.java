package editor.ui;

import editor.app.EditorApp;
import editor.model.Document;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;

/**
 * Main text area of the editor.
 * Supports toggle-based formatting like Word (Bold, Italic, Underline).
 */
public class EditorTextArea extends JTextPane {

    private final Document document;
    private final UndoManager undoManager;
    private boolean isSyncing = false;

    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderline = false;
    private int fontSize = 14;

    public EditorTextArea() {
        this.document = EditorApp.getInstance().getDocument();
        this.undoManager = new UndoManager();

        setFont(new Font("Monospaced", Font.PLAIN, fontSize));

        // Clear default styles
        SimpleAttributeSet defaultAttrs = new SimpleAttributeSet();
        StyleConstants.setBold(defaultAttrs, false);
        StyleConstants.setItalic(defaultAttrs, false);
        StyleConstants.setUnderline(defaultAttrs, false);
        StyleConstants.setFontSize(defaultAttrs, fontSize);
        StyleConstants.setFontFamily(defaultAttrs, "Monospaced");
        setCharacterAttributes(defaultAttrs, true);

        // Register undo manager
        getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Sync changes to Document model
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

    public void toggleBold() {
        isBold = !isBold;
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start != end) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setBold(attrs, isBold);
            getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            MutableAttributeSet inputAttrs = getInputAttributes();
            StyleConstants.setBold(inputAttrs, isBold);
        }
        requestFocus();
    }

    public void toggleItalic() {
        isItalic = !isItalic;
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start != end) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setItalic(attrs, isItalic);
            getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            MutableAttributeSet inputAttrs = getInputAttributes();
            StyleConstants.setItalic(inputAttrs, isItalic);
        }
        requestFocus();
    }

    public void toggleUnderline() {
        isUnderline = !isUnderline;
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start != end) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setUnderline(attrs, isUnderline);
            getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            MutableAttributeSet inputAttrs = getInputAttributes();
            StyleConstants.setUnderline(inputAttrs, isUnderline);
        }
        requestFocus();
    }

    public void setFontSize(int size) {
        this.fontSize = size;
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start != end) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontSize(attrs, size);
            getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            MutableAttributeSet inputAttrs = getInputAttributes();
            StyleConstants.setFontSize(inputAttrs, size);
        }
        requestFocus();
    }

    public boolean isBoldActive() { return isBold; }
    public boolean isItalicActive() { return isItalic; }
    public boolean isUnderlineActive() { return isUnderline; }

    private void setupKeyboardShortcuts() {
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) undoManager.undo();
            }
        });

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
        try {
            StyledDocument doc = getStyledDocument();
            document.setContent(doc.getText(0, doc.getLength()));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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