package editor.model;

import editor.observer.EditorObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * Core model class that holds the document content.
 * Implements Observer pattern to notify UI components on changes.
 * Supports Memento pattern for undo/redo functionality.
 */
public class Document {

    private StringBuilder content;
    private String filePath;
    private boolean isModified;

    private final List<EditorObserver> observers = new ArrayList<>();

    public Document() {
        this.content = new StringBuilder();
        this.filePath = null;
        this.isModified = false;
    }

    // Insert text at a given position
    public void insert(int position, String text) {
        content.insert(position, text);
        this.isModified = true;
        notifyObservers("contentChanged");
    }

    // Delete text between start and end positions
    public void delete(int start, int end) {
        content.delete(start, end);
        this.isModified = true;
        notifyObservers("contentChanged");
    }

    // Replace entire content
    public void setContent(String text) {
        this.content = new StringBuilder(text);
        this.isModified = true;
        notifyObservers("contentChanged");
    }

    public String getContent() {
        return content.toString();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        notifyObservers("filePathChanged");
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        this.isModified = modified;
        notifyObservers("modifiedChanged");
    }

    // Observer Pattern - register a new observer
    public void addObserver(EditorObserver observer) {
        observers.add(observer);
    }

    // Observer Pattern - notify all observers about a change
    public void notifyObservers(String event) {
        for (EditorObserver observer : observers) {
            observer.update(event, this);
        }
    }
}