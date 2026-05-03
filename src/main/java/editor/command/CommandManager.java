package editor.command;

import java.util.Stack;

/**
 * Manages the command history for undo and redo operations.
 * Uses two stacks - one for executed commands, one for undone commands.
 */
public class CommandManager {

    private final Stack<Command> history = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    // Execute a command and add it to history
    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
        redoStack.clear();
    }

    // Undo the last command
    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    // Redo the last undone command
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            history.push(command);
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}