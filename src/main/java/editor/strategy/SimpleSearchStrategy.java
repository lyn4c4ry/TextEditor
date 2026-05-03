package editor.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple case-insensitive text search strategy.
 */
public class SimpleSearchStrategy implements SearchStrategy {

    @Override
    public List<Integer> search(String content, String query) {
        List<Integer> results = new ArrayList<>();
        if (query == null || query.isEmpty()) return results;

        String lowerContent = content.toLowerCase();
        String lowerQuery = query.toLowerCase();
        int index = 0;

        while ((index = lowerContent.indexOf(lowerQuery, index)) != -1) {
            results.add(index);
            index += lowerQuery.length();
        }

        return results;
    }
}