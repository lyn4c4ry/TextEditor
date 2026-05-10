package editor.command;

import editor.model.Document;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;

/**
 * Command that opens a file and loads its content into the document.
 */
public class OpenFileCommand implements Command {

    private final Document document;
    private String previousContent;
    private String previousFilePath;

    public OpenFileCommand(Document document) {
        this.document = document;
    }

    @Override
    public void execute() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                // Save current state before opening new file
                previousContent = document.getContent();
                previousFilePath = document.getFilePath();

                File file = fileChooser.getSelectedFile();
                String content = Files.readString(file.toPath());
                document.setContent(content);
                document.setFilePath(file.getAbsolutePath());
                document.setModified(false);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Could not open file: " + e.getMessage());
            }
        }
    }

    @Override
    public void undo() {
        if (previousContent != null) {
            document.setContent(previousContent);
            document.setFilePath(previousFilePath);
            document.setModified(false);
        }
    }
}