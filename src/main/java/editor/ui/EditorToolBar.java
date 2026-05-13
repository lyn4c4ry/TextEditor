package editor.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Formatting toolbar with toggle buttons for Bold, Italic, Underline and font size.
 *
 * Design pattern: Observer — registers itself as a FormatChangeListener on
 * EditorTextArea so button states are updated immediately whenever the format
 * state changes (toggle or caret movement), rather than waiting for the next
 * caret event after the fact.
 *
 * Previously the toolbar relied on EditorTextArea.isBoldActive() being read
 * inside a CaretListener. The problem was:
 *   1. Ctrl+B called toggleBold() which flipped isBold internally.
 *   2. No caret-position change occurred (the cursor stayed put).
 *   3. Therefore the CaretListener never fired.
 *   4. The toolbar button stayed visually un-toggled until the user typed
 *      something (which finally moved the caret and triggered the listener).
 *
 * Fix: EditorTextArea now fires FormatChangeListener.onFormatChanged() at
 * the end of every toggle AND on every caret change, so the toolbar always
 * receives the new state immediately.
 */
public class EditorToolBar extends JToolBar {

    private final EditorTextArea textArea;

    private final JToggleButton boldButton;
    private final JToggleButton italicButton;
    private final JToggleButton underlineButton;
    private final JComboBox<Integer> fontSizeBox;

    private static final Color BG_COLOR     = new Color(245, 245, 245);
    private static final Color ACTIVE_COLOR = new Color(210, 225, 255);
    private static final Color HOVER_COLOR  = new Color(230, 230, 230);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final int   BTN_SIZE     = 28;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public EditorToolBar(EditorTextArea textArea) {
        this.textArea = textArea;

        setFloatable(false);
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(3, 6, 3, 6)
        ));

        // --- Build toggle buttons ---
        boldButton      = makeToggleButton("B",
                new Font("Serif", Font.BOLD, 13));
        italicButton    = makeToggleButton("<html><i>I</i></html>",
                new Font("Serif", Font.ITALIC, 13));
        underlineButton = makeToggleButton("<html><u>U</u></html>",
                new Font("Serif", Font.PLAIN, 13));

        // Toolbar button clicks delegate directly to the text area.
        // The resulting format change fires onFormatChanged(), which updates
        // the button's selected state — no manual setSelected() calls needed here.
        boldButton.addActionListener(e -> textArea.toggleBold());
        italicButton.addActionListener(e -> textArea.toggleItalic());
        underlineButton.addActionListener(e -> textArea.toggleUnderline());

        // --- Font size selector ---
        Integer[] sizes = {8, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32, 36, 48};
        fontSizeBox = new JComboBox<>(sizes);
        fontSizeBox.setEditable(true);
        fontSizeBox.setSelectedItem(14);
        fontSizeBox.setMaximumSize(new Dimension(55, BTN_SIZE));
        fontSizeBox.setPreferredSize(new Dimension(55, BTN_SIZE));
        fontSizeBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fontSizeBox.setBackground(Color.WHITE);
        fontSizeBox.addActionListener(e -> {
            try {
                int size = Integer.parseInt(
                        fontSizeBox.getSelectedItem().toString().trim());
                if (size > 0 && size <= 200) {
                    textArea.setFontSize(size);
                }
            } catch (NumberFormatException ex) {
                // Invalid input — silently ignore, keep previous size
            }
        });

        // --- Layout ---
        add(boldButton);
        add(italicButton);
        add(underlineButton);
        addSeparator(new Dimension(8, BTN_SIZE));
        add(makeSizeLabel());
        add(Box.createHorizontalStrut(4));
        add(fontSizeBox);
        add(Box.createHorizontalGlue());

        // --- Register as an observer on the text area ---
        // This single listener replaces the old CaretListener that was
        // registered here.  It is called by EditorTextArea:
        //   • immediately after every toggleBold/Italic/Underline() call, AND
        //   • on every caret movement (so moving into bold text lights the button).
        textArea.addFormatChangeListener(this::updateButtonStates);
    }

    // -----------------------------------------------------------------------
    // Observer callback
    // -----------------------------------------------------------------------

    /**
     * Called by EditorTextArea (Observable) whenever bold/italic/underline
     * state changes.  Updates button visual state to match.
     *
     * Runs on the Event Dispatch Thread because EditorTextArea fires this
     * from caret listeners and action listeners, which are always on the EDT.
     *
     * @param bold      current bold state
     * @param italic    current italic state
     * @param underline current underline state
     */
    private void updateButtonStates(boolean bold, boolean italic, boolean underline) {
        boldButton.setSelected(bold);
        italicButton.setSelected(italic);
        underlineButton.setSelected(underline);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Builds a styled toggle button with hover and active-colour effects.
     * Uses a ChangeListener so the background tracks the selected state
     * without the action listener having to manage it manually.
     */
    private JToggleButton makeToggleButton(String text, Font font) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(font);
        btn.setPreferredSize(new Dimension(BTN_SIZE, BTN_SIZE));
        btn.setMaximumSize(new Dimension(BTN_SIZE, BTN_SIZE));
        btn.setFocusable(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(2, 2, 2, 2)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover: highlight only when not already selected
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(HOVER_COLOR);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(Color.WHITE);
            }
        });

        // Active colour: driven by the button's own selected state,
        // which is set by updateButtonStates() via the observer callback.
        btn.addChangeListener(e ->
                btn.setBackground(btn.isSelected() ? ACTIVE_COLOR : Color.WHITE));

        return btn;
    }

    /** Creates the "Size:" label shown before the font-size combo box. */
    private JLabel makeSizeLabel() {
        JLabel label = new JLabel("  Size:");
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setForeground(new Color(100, 100, 100));
        return label;
    }
}