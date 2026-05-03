package editor.command;

import editor.model.Document;

/**
 * Command that deletes a section of text from the document.
 * Stores the deleted text so it can be restored on undo.
 */
public class DeleteTextCommand implements Command {

    private final Document document;
    private final int start;
    private final int end;
    private String deletedText;

    public DeleteTextCommand(Document document, int start, int end) {
        this.document = document;
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute() {
        // Save deleted text before removing it
        deletedText = document.getContent().substring(start, end);
        document.delete(start, end);
    }

    @Override
    public void undo() {
        document.insert(start, deletedText);
    }
}