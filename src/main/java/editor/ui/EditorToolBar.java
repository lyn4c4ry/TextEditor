package editor.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;

/**
 * Formatting toolbar with toggle buttons for Bold, Italic, Underline and font size.
 * Uses Decorator pattern logic to apply styles to selected text.
 */
public class EditorToolBar extends JToolBar {

    private final EditorTextArea textArea;

    private final JToggleButton boldButton;
    private final JToggleButton italicButton;
    private final JToggleButton underlineButton;
    private final JComboBox<Integer> fontSizeBox;

    private static final Color BG_COLOR      = new Color(245, 245, 245);
    private static final Color ACTIVE_COLOR  = new Color(210, 225, 255);
    private static final Color HOVER_COLOR   = new Color(230, 230, 230);
    private static final Color BORDER_COLOR  = new Color(200, 200, 200);
    private static final int   BTN_SIZE      = 28;

    public EditorToolBar(EditorTextArea textArea) {
        this.textArea = textArea;
        setFloatable(false);
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(3, 6, 3, 6)
        ));

        boldButton      = makeToggleButton("B", new Font("Serif", Font.BOLD, 13));
        italicButton    = makeToggleButton("<html><i>I</i></html>", new Font("Serif", Font.ITALIC, 13));
        underlineButton = makeToggleButton("<html><u>U</u></html>", new Font("Serif", Font.PLAIN, 13));

        boldButton.addActionListener(e -> {
            textArea.toggleBold();
            boldButton.setSelected(textArea.isBoldActive());
        });

        italicButton.addActionListener(e -> {
            textArea.toggleItalic();
            italicButton.setSelected(textArea.isItalicActive());
        });

        underlineButton.addActionListener(e -> {
            textArea.toggleUnderline();
            underlineButton.setSelected(textArea.isUnderlineActive());
        });

        // Font size selector
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
                int size = Integer.parseInt(fontSizeBox.getSelectedItem().toString().trim());
                if (size > 0 && size <= 200) {
                    textArea.setFontSize(size);
                }
            } catch (NumberFormatException ex) {
                // Invalid input, ignore
            }
        });

        // Layout
        add(boldButton);
        add(italicButton);
        add(underlineButton);
        addSeparator(new Dimension(8, BTN_SIZE));
        add(new JLabel("  Size:") {{
            setFont(new Font("SansSerif", Font.PLAIN, 11));
            setForeground(new Color(100, 100, 100));
        }});
        add(Box.createHorizontalStrut(4));
        add(fontSizeBox);

        // Don't fill the rest of the bar
        add(Box.createHorizontalGlue());

        textArea.addCaretListener(e -> updateButtonStates());
    }

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

        // Hover effect
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

        // Active color
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(ACTIVE_COLOR);
            } else {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    private void updateButtonStates() {
        boldButton.setSelected(textArea.isBoldActive());
        italicButton.setSelected(textArea.isItalicActive());
        underlineButton.setSelected(textArea.isUnderlineActive());
    }
}