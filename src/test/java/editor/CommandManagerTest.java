package editor;

import editor.command.CommandManager;
import editor.model.Document;
import editor.command.InsertTextCommand;
import editor.command.DeleteTextCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandManager.
 * Tests undo/redo functionality with insert and delete commands.
 */
class CommandManagerTest {

    private CommandManager commandManager;
    private Document document;

    @BeforeEach
    void setUp() {
        commandManager = new CommandManager();
        document = new Document();
    }

    @Test
    void testInsertText() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        assertEquals("Hello", document.getContent());
    }

    @Test
    void testUndoInsert() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        commandManager.undo();
        assertEquals("", document.getContent());
    }

    @Test
    void testRedoInsert() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        commandManager.undo();
        commandManager.redo();
        assertEquals("Hello", document.getContent());
    }

    @Test
    void testDeleteText() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello World"));
        commandManager.executeCommand(new DeleteTextCommand(document, 5, 11));
        assertEquals("Hello", document.getContent());
    }

    @Test
    void testUndoDelete() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello World"));
        commandManager.executeCommand(new DeleteTextCommand(document, 5, 11));
        commandManager.undo();
        assertEquals("Hello World", document.getContent());
    }

    @Test
    void testCanUndo() {
        assertFalse(commandManager.canUndo());
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        assertTrue(commandManager.canUndo());
    }

    @Test
    void testCanRedo() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        assertFalse(commandManager.canRedo());
        commandManager.undo();
        assertTrue(commandManager.canRedo());
    }
}