package hmperson1.apps.autosign.sign;

/**
 * Represents a Sign.
 */
public final class Sign {
    private final String text;
    private final String typeface;
    private final int fontColor;
    private final int bgColor;

    /**
     * Creates a new Sign. Do not use this directly.
     *
     * @param text      the text
     * @param typeface  the typeface for the text
     * @param fontColor the color of the font for the text
     * @param bgColor   the color of the background
     */
    Sign(String text, String typeface, int fontColor,
         int bgColor) {
        //@formatter:off
        this.text       = text;
        this.typeface      = typeface;
        this.fontColor     = fontColor;
        this.bgColor       = bgColor;
        //@formatter:on
    }

    @Override
    public String toString() {
        return text.replace('\n', ' ');
    }

    public String getText() {
        return text;
    }

    public String getTypeface() {
        return typeface;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getBgColor() {
        return bgColor;
    }
}
