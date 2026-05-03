package editor.command;

/**
 * Base interface for all editor commands.
 * Every action (open, save, type, delete...) implements this.
 */
public interface Command {
    void execute();
    void undo();
}