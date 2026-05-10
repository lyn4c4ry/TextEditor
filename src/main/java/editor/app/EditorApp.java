package editor.app;

import editor.command.CommandManager;
import editor.model.Document;

/**
 * Singleton Pattern - Single instance of the application.
 * All classes access the Document and CommandManager through here.
 */
public class EditorApp {

    private static EditorApp instance;

    private final Document document;
    private final CommandManager commandManager;

    // Private constructor - cannot be instantiated from outside
    private EditorApp() {
        this.document = new Document();
        this.commandManager = new CommandManager();
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

}