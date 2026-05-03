package editor.model;

import java.util.Stack;

/**
 * Caretaker class for the Memento Pattern.
 * Stores and manages document snapshots for undo operations.
 */
public class EditorCaretaker {

    private final Stack<EditorMemento> history = new Stack<>();

    // Save a new snapshot
    public void save(EditorMemento memento) {
        history.push(memento);
    }

    // Get the last snapshot and remove it from history
    public EditorMemento restore() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null;
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }
}