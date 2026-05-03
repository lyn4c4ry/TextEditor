package editor.strategy;

import java.util.List;

/**
 * Strategy interface for text search operations.
 * Different search algorithms can be swapped at runtime.
 */
public interface SearchStrategy {
    List<Integer> search(String content, String query);
}