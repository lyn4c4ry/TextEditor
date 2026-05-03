package editor.command;

import editor.model.Document;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.*;

/**
 * Command that saves the document to a new file path chosen by the user.
 * Uses Strategy pattern - different file formats can be selected.
 */
public class SaveAsFileCommand implements Command {

    private final Document document;
    private String previousFilePath;

    public SaveAsFileCommand(Document document) {
        this.document = document;
    }

    @Override
    public void execute() {
        JFileChooser fileChooser = new JFileChooser();

        // Add file format filters
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Java File (*.java)", "java"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Markdown File (*.md)", "md"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("All Files (*.*)", "*"));

        // Set default filter
        fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);

        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            previousFilePath = document.getFilePath();

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

            try {
                Files.writeString(Path.of(newPath), document.getContent());
                document.setFilePath(newPath);
                document.setModified(false);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Could not save file: " + e.getMessage());
            }
        }
    }

    @Override
    public void undo() {
        if (previousFilePath != null) {
            document.setFilePath(previousFilePath);
        }
    }
}