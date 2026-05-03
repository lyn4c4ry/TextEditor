package editor.observer;

import editor.model.Document;

/**
 * Observer interface.
 * Called whenever the document state changes.
 */
public interface EditorObserver {
    void update(String event, Document document);
}