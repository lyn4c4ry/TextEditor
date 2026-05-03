package editor.command;

import editor.model.Document;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.*;

/**
 * Command that saves the document to its current file path.
 * If no file path is set, prompts the user to choose one with format options.
 */
public class SaveFileCommand implements Command {

    private final Document document;

    public SaveFileCommand(Document document) {
        this.document = document;
    }

    @Override
    public void execute() {
        if (document.getFilePath() == null) {
            // No file path yet, ask user with format options
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Java File (*.java)", "java"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Markdown File (*.md)", "md"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("All Files (*.*)", "*"));
            fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);

            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String newPath = fileChooser.getSelectedFile().getAbsolutePath();

                // Auto-add extension if not present
                String selectedFilter = fileChooser.getFileFilter().getDescription();
                if (selectedFilter.contains("*.txt") && !newPath.endsWith(".txt")) {
                    newPath += ".txt";
                } else if (selectedFilter.contains("*.java") && !newPath.endsWith(".java")) {
                    newPath += ".java";
                } else if (selectedFilter.contains("*.md") && !newPath.endsWith(".md")) {
                    newPath += ".md";
                }

                document.setFilePath(newPath);
            } else {
                return;
            }
        }

        try {
            Files.writeString(Path.of(document.getFilePath()), document.getContent());
            document.setModified(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not save file: " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        // Save cannot be undone
    }
}