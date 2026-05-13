package editor.ui;

import editor.strategy.SearchStrategy;
import editor.strategy.SimpleSearchStrategy;
import editor.strategy.RegexSearchStrategy;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced search dialog that fixes indexing offsets caused by platform-specific line endings.
 * Implements Strategy Pattern and visual highlighting.
 */
public class FindDialog extends JDialog {

    private final JTextField searchField;
    private final JCheckBox regexCheckBox;
    private final JLabel countLabel;
    private final EditorTextArea textArea;
    private SearchStrategy strategy;

    private List<Integer> results = new ArrayList<>();
    private int currentIndex = -1;

    private final Highlighter.HighlightPainter generalPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    private final Highlighter.HighlightPainter currentPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);

    public FindDialog(JFrame parent, EditorTextArea textArea) {
        super(parent, "Find", false);
        this.textArea = textArea;
        this.strategy = new SimpleSearchStrategy();

        // Clear highlights when dialog is closed via 'X' button
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                clearHighlights();
            }
        });

        setLayout(new BorderLayout(10, 10));
        setSize(450, 150);
        setLocationRelativeTo(parent);

        // UI Setup: Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        searchField = new JTextField();
        countLabel = new JLabel("0 / 0", SwingConstants.CENTER);
        countLabel.setPreferredSize(new Dimension(60, 20));

        inputPanel.add(new JLabel("Find:"), BorderLayout.WEST);
        inputPanel.add(searchField, BorderLayout.CENTER);
        inputPanel.add(countLabel, BorderLayout.EAST);

        // UI Setup: Logic & Control
        regexCheckBox = new JCheckBox("Use Regex");
        regexCheckBox.addActionListener(e -> {
            strategy = regexCheckBox.isSelected() ? new RegexSearchStrategy() : new SimpleSearchStrategy();
            resetSearchState();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        JButton findButton = new JButton("Find All");
        JButton closeButton = new JButton("Close");

        findButton.addActionListener(e -> startNewSearch());
        nextButton.addActionListener(e -> navigateResults(1));
        prevButton.addActionListener(e -> navigateResults(-1));
        closeButton.addActionListener(e -> {
            clearHighlights();
            dispose();
        });

        buttonPanel.add(regexCheckBox);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(findButton);
        buttonPanel.add(closeButton);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void resetSearchState() {
        results.clear();
        currentIndex = -1;
        clearHighlights();
        updateUI();
    }

    private void startNewSearch() {
        String query = searchField.getText();
        if (query.isEmpty()) return;

        // CRITICAL FIX: Standardize line endings to prevent index offsets (\r\n -> \n)
        String content = textArea.getText().replace("\r\n", "\n");

        results = strategy.search(content, query);

        if (results.isEmpty()) {
            currentIndex = -1;
            clearHighlights();
            JOptionPane.showMessageDialog(this, "No results found.");
        } else {
            currentIndex = 0;
            applyVisualFeedback();
        }
        updateUI();
    }

    private void navigateResults(int direction) {
        if (results.isEmpty()) return;

        currentIndex += direction;
        if (currentIndex >= results.size()) currentIndex = 0;
        if (currentIndex < 0) currentIndex = results.size() - 1;

        applyVisualFeedback();
        updateUI();
    }

    private void applyVisualFeedback() {
        if (currentIndex == -1 || results.isEmpty()) return;

        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();

        String query = searchField.getText();
        // Standardize content here as well to sync with highlighter coordinates
        String content = textArea.getText().replace("\r\n", "\n");

        for (int i = 0; i < results.size(); i++) {
            try {
                int start = results.get(i);
                int end;

                if (regexCheckBox.isSelected()) {
                    Pattern p = Pattern.compile(query);
                    Matcher m = p.matcher(content);
                    // Search starting from the stored index to find actual match boundaries
                    if (m.find(start)) {
                        end = m.end();
                    } else {
                        end = start + query.length();
                    }
                } else {
                    end = start + query.length();
                }

                Highlighter.HighlightPainter painter = (i == currentIndex) ? currentPainter : generalPainter;
                highlighter.addHighlight(start, end, painter);

                if (i == currentIndex) {
                    textArea.select(start, end); // This will now align perfectly
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearHighlights() {
        if (textArea != null) {
            textArea.getHighlighter().removeAllHighlights();
        }
    }

    private void updateUI() {
        if (currentIndex == -1) {
            countLabel.setText("0 / 0");
        } else {
            countLabel.setText((currentIndex + 1) + " / " + results.size());
        }
    }
}