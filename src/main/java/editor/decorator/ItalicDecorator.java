package editor.decorator;

/**
 * Decorator that wraps text with HTML italic tags.
 */
public class ItalicDecorator implements TextFormatter {

    private final TextFormatter wrapped;

    public ItalicDecorator(TextFormatter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String format(String text) {
        return "<i>" + wrapped.format(text) + "</i>";
    }
}