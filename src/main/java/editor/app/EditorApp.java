package editor.app;

import editor.command.CommandManager;
import editor.model.Document;
import editor.model.EditorCaretaker;

/**
 * Singleton Pattern - Single instance of the application.
 * All classes access the Document and CommandManager through here.
 */
public class EditorApp {

    private static EditorApp instance;

    private final Document document;
    private final CommandManager commandManager;
    private final EditorCaretaker caretaker;

    // Private constructor - cannot be instantiated from outside
    private EditorApp() {
        this.document = new Document();
        this.commandManager = new CommandManager();
        this.caretaker = new EditorCaretaker();
    }

    // Returns the single instance, creates it if it doesn't exist
    public static EditorApp getInstance() {
        if (instance == null) {
            instance = new EditorApp();
        }
        return instance;
    }

    public Document getDocument() {
        return document;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public EditorCaretaker getCaretaker() {
        return caretaker;
    }
}