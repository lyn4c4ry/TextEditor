package editor.ui;

import editor.model.Document;
import editor.observer.EditorObserver;

import javax.swing.*;
import java.awt.*;

/**
 * Status bar at the bottom of the editor window.
 * Implements Observer pattern - updates automatically when document changes.
 */
public class StatusBar extends JPanel implements EditorObserver {

    private final JLabel charCountLabel;
    private final JLabel modifiedLabel;
    private final JLabel filePathLabel;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());

        charCountLabel = new JLabel("Characters: 0");
        modifiedLabel = new JLabel("Saved");
        filePathLabel = new JLabel("Untitled");

        add(filePathLabel, BorderLayout.WEST);
        add(charCountLabel, BorderLayout.CENTER);
        add(modifiedLabel, BorderLayout.EAST);
    }

    @Override
    public void update(String event, Document document) {
        charCountLabel.setText("Characters: " + document.getContent().length());
        modifiedLabel.setText(document.isModified() ? "Unsaved" : "Saved");
        filePathLabel.setText(document.getFilePath() != null ? document.getFilePath() : "Untitled");
    }
}