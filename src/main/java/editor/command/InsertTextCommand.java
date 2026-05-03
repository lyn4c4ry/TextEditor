package editor.command;

import editor.model.Document;

/**
 * Command that inserts text into the document at a given position.
 */
public class InsertTextCommand implements Command {

    private final Document document;
    private final String text;
    private final int position;

    public InsertTextCommand(Document document, int position, String text) {
        this.document = document;
        this.position = position;
        this.text = text;
    }

    @Override
    public void execute() {
        document.insert(position, text);
    }

    @Override
    public void undo() {
        document.delete(position, position + text.length());
    }
}