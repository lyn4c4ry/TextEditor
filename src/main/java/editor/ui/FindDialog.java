package editor.ui;

import editor.strategy.SearchStrategy;
import editor.strategy.SimpleSearchStrategy;
import editor.strategy.RegexSearchStrategy;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for finding text in the document.
 * Uses Strategy pattern - can switch between simple and regex search.
 */
public class FindDialog extends JDialog {

    private final JTextField searchField;
    private final JCheckBox regexCheckBox;
    private final EditorTextArea textArea;
    private SearchStrategy strategy;

    public FindDialog(JFrame parent, EditorTextArea textArea) {
        super(parent, "Find", false);
        this.textArea = textArea;
        this.strategy = new SimpleSearchStrategy();

        setLayout(new BorderLayout(10, 10));
        setSize(400, 120);
        setLocationRelativeTo(parent);

        // Search input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        inputPanel.add(new JLabel("Find:"), BorderLayout.WEST);
        inputPanel.add(searchField, BorderLayout.CENTER);

        // Options panel
        regexCheckBox = new JCheckBox("Use Regex");
        regexCheckBox.addActionListener(e -> {
            if (regexCheckBox.isSelected()) {
                strategy = new RegexSearchStrategy();
            } else {
                strategy = new SimpleSearchStrategy();
            }
        });

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton findButton = new JButton("Find");
        JButton closeButton = new JButton("Close");

        findButton.addActionListener(e -> findText());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(regexCheckBox);
        buttonPanel.add(findButton);
        buttonPanel.add(closeButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void findText() {
        String query = searchField.getText();
        String content = textArea.getText();

        List<Integer> results = strategy.search(content, query);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results found.");
        } else {
            // Highlight first result
            int index = results.get(0);
            textArea.setCaretPosition(index);
            textArea.select(index, index + query.length());
            textArea.requestFocus();
        }
    }
}