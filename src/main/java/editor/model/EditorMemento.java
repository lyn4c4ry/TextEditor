package editor.model;

/**
 * Memento Pattern - Stores a snapshot of the document's content.
 * Used by EditorCaretaker to support undo operations.
 */
public class EditorMemento {

    private final String content;

    EditorMemento(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}