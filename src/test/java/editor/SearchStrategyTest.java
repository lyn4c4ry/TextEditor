package editor;

import editor.strategy.SearchStrategy;
import editor.strategy.SimpleSearchStrategy;
import editor.strategy.RegexSearchStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Search Strategies.
 * Verifies that simple and regex search algorithms return correct indices.
 */
class SearchStrategyTest {

    @Test
    void testSimpleSearchStrategy() {
        SearchStrategy strategy = new SimpleSearchStrategy();
        String content = "Hello world, hello again!";

        // "hello" kelimesini büyük/küçük harf duyarsız arar
        List<Integer> results = strategy.search(content, "hello");

        assertEquals(2, results.size(), "Should find exactly two occurrences.");
        assertEquals(0, results.get(0), "First occurrence should be at index 0.");
        assertEquals(13, results.get(1), "Second occurrence should be at index 13.");
    }

    @Test
    void testSimpleSearchNoResult() {
        SearchStrategy strategy = new SimpleSearchStrategy();
        String content = "Hello world!";

        List<Integer> results = strategy.search(content, "Java");
        assertTrue(results.isEmpty(), "Should return an empty list if no match is found.");
    }

    @Test
    void testRegexSearchStrategy() {
        SearchStrategy strategy = new RegexSearchStrategy();
        // İki adet test e-postası içeren bir metin
        String content = "Contact us at admin@test.com or support@test.com.";

        // Basit bir e-posta regex kalıbı
        List<Integer> results = strategy.search(content, "[a-z]+@test\\.com");

        assertEquals(2, results.size(), "Should find two regex matches.");
        assertEquals(14, results.get(0), "First match starts at index 14.");
        assertEquals(32, results.get(1), "Second match starts at index 32.");
    }

    @Test
    void testRegexSearchInvalidPattern() {
        SearchStrategy strategy = new RegexSearchStrategy();
        String content = "Some text here.";

        // Geçersiz (kapatılmamış parantez) regex kalıbı çökmemeli, boş liste dönmeli
        List<Integer> results = strategy.search(content, "[a-z+");
        assertTrue(results.isEmpty(), "Should handle invalid regex gracefully and return empty list.");
    }
}