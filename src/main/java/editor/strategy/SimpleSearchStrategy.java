package editor.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete strategy for simple case-insensitive text search.
 * Implements the SearchStrategy interface to return all match positions.
 */
public class SimpleSearchStrategy implements SearchStrategy {

    @Override
    public List<Integer> search(String content, String query) {
        List<Integer> results = new ArrayList<>();

        // Validation to prevent infinite loops or null pointer exceptions
        if (query == null || query.isEmpty() || content == null) {
            return results;
        }

        String lowerContent = content.toLowerCase();
        String lowerQuery = query.toLowerCase();
        int index = 0;

        // Iterate through the content and store every occurrence of the query
        while ((index = lowerContent.indexOf(lowerQuery, index)) != -1) {
            results.add(index);
            // Move index forward by the length of the query to find the next match
            index += lowerQuery.length();
        }

        return results;
    }
}