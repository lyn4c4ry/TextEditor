package editor.command;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import editor.model.Document;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.*;

/**
 * Command that saves the document to a new file path chosen by the user.
 * Supports .txt, .java, .md, .html, .xml, .csv, .log, .pdf formats.
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
        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Java Source File (*.java)", "java"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Markdown File (*.md)", "md"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("HTML File (*.html)", "html"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML File (*.xml)", "xml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Log File (*.log)", "log"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF Document (*.pdf)", "pdf"));
        fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);

        int result = fileChooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        previousFilePath = document.getFilePath();
        String newPath = fileChooser.getSelectedFile().getAbsolutePath();
        String selectedFilter = fileChooser.getFileFilter().getDescription();

        // Auto-add extension if missing
        String ext = getExtension(selectedFilter);
        if (!ext.isEmpty() && !newPath.endsWith("." + ext)) {
            newPath += "." + ext;
        }

        try {
            if (ext.equals("pdf")) {
                savePdf(newPath);
            } else if (ext.equals("html")) {
                saveHtml(newPath);
            } else {
                Files.writeString(Path.of(newPath), document.getContent());
            }
            document.setFilePath(newPath);
            document.setModified(false);
            JOptionPane.showMessageDialog(null, "File saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not save file: " + e.getMessage());
        }
    }

    private void savePdf(String path) throws Exception {
        com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
        PdfWriter.getInstance(pdf, new FileOutputStream(path));
        pdf.open();

        // Split content into lines and add each as paragraph
        String[] lines = document.getContent().split("\n");
        Font font = FontFactory.getFont(FontFactory.COURIER, 11);
        for (String line : lines) {
            pdf.add(new Paragraph(line.isEmpty() ? " " : line, font));
        }
        pdf.close();
    }

    private void saveHtml(String path) throws IOException {
        String content = document.getContent()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>\n");

        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Document</title>
                <style>
                    body { font-family: monospace; font-size: 14px;
                           padding: 20px; line-height: 1.6; }
                </style>
            </head>
            <body>
            """ + content + """
            </body>
            </html>
            """;

        Files.writeString(Path.of(path), html);
    }

    private String getExtension(String filterDescription) {
        if (filterDescription.contains("*.txt"))  return "txt";
        if (filterDescription.contains("*.java")) return "java";
        if (filterDescription.contains("*.md"))   return "md";
        if (filterDescription.contains("*.html")) return "html";
        if (filterDescription.contains("*.xml"))  return "xml";
        if (filterDescription.contains("*.csv"))  return "csv";
        if (filterDescription.contains("*.log"))  return "log";
        if (filterDescription.contains("*.pdf"))  return "pdf";
        return "";
    }

    @Override
    public void undo() {
        if (previousFilePath != null) {
            document.setFilePath(previousFilePath);
        }
    }
}