package editor.decorator;

/**
 * Decorator that wraps text with HTML bold tags.
 */
public class BoldDecorator implements TextFormatter {

    private final TextFormatter wrapped;

    public BoldDecorator(TextFormatter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String format(String text) {
        return "<b>" + wrapped.format(text) + "</b>";
    }
}