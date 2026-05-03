package editor.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

/**
 * Regex-based search strategy.
 * Allows pattern matching in the document content.
 */
public class RegexSearchStrategy implements SearchStrategy {

    @Override
    public List<Integer> search(String content, String query) {
        List<Integer> results = new ArrayList<>();
        if (query == null || query.isEmpty()) return results;

        try {
            Pattern pattern = Pattern.compile(query);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                results.add(matcher.start());
            }
        } catch (PatternSyntaxException e) {
            // Invalid regex, return empty list
        }

        return results;
    }
}