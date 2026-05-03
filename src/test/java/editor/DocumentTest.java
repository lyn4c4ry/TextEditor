package editor;

import editor.model.Document;
import editor.model.EditorMemento;
import editor.model.EditorCaretaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Document.
 * Tests content operations and Memento pattern.
 */
class DocumentTest {

    private Document document;
    private EditorCaretaker caretaker;

    @BeforeEach
    void setUp() {
        document = new Document();
        caretaker = new EditorCaretaker();
    }

    @Test
    void testSetAndGetContent() {
        document.setContent("Hello");
        assertEquals("Hello", document.getContent());
    }

    @Test
    void testInsert() {
        document.setContent("Hello");
        document.insert(5, " World");
        assertEquals("Hello World", document.getContent());
    }

    @Test
    void testDelete() {
        document.setContent("Hello World");
        document.delete(5, 11);
        assertEquals("Hello", document.getContent());
    }

    @Test
    void testIsModified() {
        assertFalse(document.isModified());
        document.setContent("Hello");
        assertTrue(document.isModified());
    }

    @Test
    void testMementoSaveAndRestore() {
        document.setContent("Hello");
        caretaker.save(document.createMemento());

        document.setContent("Hello World");
        assertEquals("Hello World", document.getContent());

        document.restoreFromMemento(caretaker.restore());
        assertEquals("Hello", document.getContent());
    }

    @Test
    void testFilePath() {
        assertNull(document.getFilePath());
        document.setFilePath("test.txt");
        assertEquals("test.txt", document.getFilePath());
    }
}