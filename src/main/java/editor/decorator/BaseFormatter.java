package editor.decorator;

/**
 * Default formatter that returns text as-is.
 */
public class BaseFormatter implements TextFormatter {

    @Override
    public String format(String text) {
        return text;
    }
}