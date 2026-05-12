package editor;

import editor.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Document.
 * Tests core content operations and state management.
 */
class DocumentTest {

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document();
    }

    @Test
    void testSetAndGetContent() {
        document.setContent("Hello");
        assertEquals("Hello", document.getContent(), "Content should match what was set.");
    }

    @Test
    void testInsert() {
        document.setContent("Hello");
        document.insert(5, " World");
        assertEquals("Hello World", document.getContent(), "Text should be inserted correctly.");
    }

    @Test
    void testDelete() {
        document.setContent("Hello World");
        document.delete(5, 11);
        assertEquals("Hello", document.getContent(), "Text should be deleted correctly.");
    }

    @Test
    void testIsModified() {
        assertFalse(document.isModified(), "New document should not be modified.");
        document.setContent("Hello");
        assertTrue(document.isModified(), "Document should be marked as modified after changes.");
    }

    @Test
    void testFilePath() {
        assertNull(document.getFilePath(), "New document should have no file path.");
        document.setFilePath("test.txt");
        assertEquals("test.txt", document.getFilePath(), "File path should be updated.");
    }
}