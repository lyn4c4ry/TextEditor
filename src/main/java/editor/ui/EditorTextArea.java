package editor.ui;

import editor.app.EditorApp;
import editor.model.Document;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main text area of the editor.
 * Supports toggle-based formatting like Word (Bold, Italic, Underline).
 *
 * Design pattern: Observable — notifies registered FormatChangeListeners
 * whenever a formatting state changes (bold/italic/underline), so the
 * toolbar can stay in sync without polling or relying solely on caret events.
 */
public class EditorTextArea extends JTextPane {

    // -----------------------------------------------------------------------
    // Observer interface — toolbar registers itself here
    // -----------------------------------------------------------------------

    /**
     * Listener interface (Observer pattern) for format-state changes.
     * Implemented by EditorToolBar so it can update its toggle buttons
     * immediately after every bold/italic/underline toggle, not only on
     * the next caret movement.
     */
    public interface FormatChangeListener {
        void onFormatChanged(boolean bold, boolean italic, boolean underline);
    }

    // -----------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------

    private final Document document;
    private final UndoManager undoManager;
    private boolean isSyncing = false;

    // Current "input cursor" format state — what the next typed character will look like
    private boolean isBold      = false;
    private boolean isItalic    = false;
    private boolean isUnderline = false;
    private int     fontSize    = 14;

    /** Registered observers (typically just the toolbar). */
    private final List<FormatChangeListener> formatListeners = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public EditorTextArea() {
        this.document    = EditorApp.getInstance().getDocument();
        this.undoManager = new UndoManager();

        setFont(new Font("Monospaced", Font.PLAIN, fontSize));

        // Apply clean default character attributes so the document starts
        // with no residual bold/italic/underline state.
        SimpleAttributeSet defaultAttrs = new SimpleAttributeSet();
        StyleConstants.setBold(defaultAttrs, false);
        StyleConstants.setItalic(defaultAttrs, false);
        StyleConstants.setUnderline(defaultAttrs, false);
        StyleConstants.setFontSize(defaultAttrs, fontSize);
        StyleConstants.setFontFamily(defaultAttrs, "Monospaced");
        setCharacterAttributes(defaultAttrs, true);

        // Register undo manager with the underlying Swing document.
        getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Sync text changes to the application's Document model.
        getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { if (!isSyncing) syncToModel(); }
            @Override public void removeUpdate(DocumentEvent e)  { if (!isSyncing) syncToModel(); }
            @Override public void changedUpdate(DocumentEvent e) { /* attribute changes only */ }
        });

        // When the caret moves into already-formatted text, read the attributes
        // at that position and update our internal state + notify listeners.
        addCaretListener(e -> syncFormatStateFromCaret());

        setupKeyboardShortcuts();
    }

    // -----------------------------------------------------------------------
    // Observer registration
    // -----------------------------------------------------------------------

    /**
     * Registers a listener that will be called every time the bold/italic/
     * underline state changes (via toggle or caret movement).
     */
    public void addFormatChangeListener(FormatChangeListener listener) {
        formatListeners.add(listener);
    }

    /** Removes a previously registered listener. */
    public void removeFormatChangeListener(FormatChangeListener listener) {
        formatListeners.remove(listener);
    }

    /** Notifies all registered observers with the current format state. */
    private void fireFormatChanged() {
        for (FormatChangeListener l : formatListeners) {
            l.onFormatChanged(isBold, isItalic, isUnderline);
        }
    }

    // -----------------------------------------------------------------------
    // Format toggles (called by toolbar buttons AND keyboard shortcuts)
    // -----------------------------------------------------------------------

    /**
     * Toggles bold on the selected text or on the input cursor.
     * After applying the style change, all registered FormatChangeListeners
     * are notified immediately — this is the fix for the toolbar icon lag.
     */
    public void toggleBold() {
        isBold = !isBold;
        applyCharacterAttribute(StyleConstants.Bold, isBold);
        fireFormatChanged(); // <-- notify toolbar right away
        requestFocus();
    }

    /** Toggles italic on the selected text or on the input cursor. */
    public void toggleItalic() {
        isItalic = !isItalic;
        applyCharacterAttribute(StyleConstants.Italic, isItalic);
        fireFormatChanged();
        requestFocus();
    }

    /** Toggles underline on the selected text or on the input cursor. */
    public void toggleUnderline() {
        isUnderline = !isUnderline;
        applyCharacterAttribute(StyleConstants.Underline, isUnderline);
        fireFormatChanged();
        requestFocus();
    }

    /**
     * Sets the font size for selected text or for the input cursor.
     * Does NOT fire FormatChangeListener because font size is not tracked
     * in the boolean state the toolbar displays.
     */
    public void setFontSize(int size) {
        this.fontSize = size;
        int start = getSelectionStart();
        int end   = getSelectionEnd();
        if (start != end) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontSize(attrs, size);
            getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            StyleConstants.setFontSize(getInputAttributes(), size);
        }
        requestFocus();
    }

    // -----------------------------------------------------------------------
    // State accessors (used by toolbar for initial / fallback queries)
    // -----------------------------------------------------------------------

    public boolean isBoldActive()      { return isBold;      }
    public boolean isItalicActive()    { return isItalic;    }
    public boolean isUnderlineActive() { return isUnderline; }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Applies a single boolean character attribute to the selection,
     * or to the input-cursor attributes when nothing is selected.
     *
     * Extracted from the three toggle methods to eliminate duplication
     * (DRY / Single Responsibility).
     *
     * @param key   the StyleConstants attribute key (e.g. StyleConstants.Bold)
     * @param value the value to set
     */
    private void applyCharacterAttribute(Object key, boolean value) {
        int start = getSelectionStart();
        int end   = getSelectionEnd();
        if (start != end) {
            // Apply to selected range
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            attrs.addAttribute(key, value);
            getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            // No selection — affect only future typed characters
            MutableAttributeSet inputAttrs = getInputAttributes();
            inputAttrs.addAttribute(key, value);
        }
    }

    /**
     * Reads the character attributes at (or just before) the current caret
     * position and updates the internal bold/italic/underline state.
     *
     * This is called on every caret movement so that moving into already-bold
     * text makes the Bold button light up, matching Word-style behaviour.
     * After updating the state, registered listeners are notified.
     */
    private void syncFormatStateFromCaret() {
        int pos = Math.max(0, getCaretPosition() - 1);
        StyledDocument doc = getStyledDocument();
        Element elem = doc.getCharacterElement(pos);
        AttributeSet attrs = elem.getAttributes();

        isBold      = StyleConstants.isBold(attrs);
        isItalic    = StyleConstants.isItalic(attrs);
        isUnderline = StyleConstants.isUnderline(attrs);

        fireFormatChanged();
    }

    /**
     * Sets up Ctrl+B / Ctrl+I / Ctrl+U keyboard shortcuts and
     * Ctrl+Z / Ctrl+Y undo-redo shortcuts.
     *
     * NOTE: The formatting shortcuts were missing before, which meant
     * pressing Ctrl+B called nothing — the toolbar button state was
     * never updated because toggleBold() was never reached.
     */
    private void setupKeyboardShortcuts() {
        // --- Formatting shortcuts ---
        bindKey(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK, "bold", e -> toggleBold());
        bindKey(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK, "italic", e -> toggleItalic());
        bindKey(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK, "underline", e -> toggleUnderline());

        // --- Undo / Redo ---
        bindKey(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, "undo", e -> {
            if (undoManager.canUndo()) undoManager.undo();
        });
        bindKey(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK, "redo", e -> {
            if (undoManager.canRedo()) undoManager.redo();
        });
    }

    /**
     * Convenience method — registers a single key binding in both the
     * component's input map and action map, keeping setupKeyboardShortcuts()
     * readable and avoiding repetition.
     */
    private void bindKey(int keyCode, int modifiers, String actionKey,
                         ActionListener handler) {
        getInputMap().put(KeyStroke.getKeyStroke(keyCode, modifiers), actionKey);
        getActionMap().put(actionKey, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { handler.actionPerformed(e); }
        });
    }

    // -----------------------------------------------------------------------
    // Model sync
    // -----------------------------------------------------------------------

    /** Pushes the current text content to the application's Document model. */
    private void syncToModel() {
        isSyncing = true;
        try {
            StyledDocument doc = getStyledDocument();
            document.setContent(doc.getText(0, doc.getLength()));
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            isSyncing = false;
        }
    }

    /** Pulls the content from the application's Document model into the UI. */
    public void syncFromModel() {
        isSyncing = true;
        setText(document.getContent());
        isSyncing = false;
    }

    // -----------------------------------------------------------------------
    // Accessors used by the rest of the application
    // -----------------------------------------------------------------------

    public UndoManager getUndoManager() { return undoManager; }

    /**
     * Checks whether a given AWT font style flag is currently active.
     * Kept for backwards-compatibility; prefer isBoldActive() etc. internally.
     */
    public boolean isStyleActive(int style) {
        return (getFont().getStyle() & style) != 0;
    }
}