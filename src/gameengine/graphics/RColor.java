package gameengine.graphics;

/**
 * Represents a color.
 */
public class RColor {
    public static final RColor VIOLET = new RColor(143f / 255f, 0.0f, 1.0f);
    public static final RColor GREY = new RColor(0.5f, 0.5f, 0.5f);
    public static final RColor BROWN = new RColor(150f / 255f, 75f / 255f, 0.0f);
    public static final RColor ORANGE = new RColor(1.0f, 127f / 255f, 0.0f);
    public static final RColor YELLOW = new RColor(1.0f, 1.0f, 0.0f);
    public static final RColor PINK = new RColor(1.0f, 192f / 255f, 203f / 255f);
    public static final RColor MAGENTA = new RColor(1.0f, 0.0f, 1.0f);
    public static final RColor CYAN = new RColor(0.0f, 1.0f, 1.0f);
    public static final RColor WHITE = new RColor(1.0f, 1.0f, 1.0f);
    public static final RColor BLACK = new RColor(0.0f, 0.0f, 0.0f);
    public static final RColor BLUE = new RColor(0.0f, 0.0f, 1.0f);
    public static final RColor GREEN = new RColor(0.0f, 1.0f, 0.0f);
    public static final RColor RED = new RColor(1.0f, 0.0f, 0.0f);

    /**
     * used for making colors brighter or darker.
     */
    public static final float DARKNESS_FACTOR = 0.7f;

    /**
     * The individual color components.
     */
    private final float red, green, blue;

    /**
     * Creates a RColor instance.
     *
     * @param red   The red component (0 <= red <= 1)
     * @param green The green component (0 <= green <= 1)
     * @param blue  The blue component (0 <= blue <= 1)
     */
    public RColor(float red, float green, float blue) {
        assert red >= 0f & red <= 1f;
        assert green >= 0f & green <= 1f;
        assert blue >= 0f & blue <= 1f;

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Creates a RColor instance.
     *
     * @param red   The red component (0 <= red <= 255)
     * @param green The green component (0 <= green <= 255)
     * @param blue  The blue component (0 <= blue <= 255)
     */
    public RColor(int red, int green, int blue) {
        assert red >= 0 & red <= 255;
        assert green >= 0 & green <= 255;
        assert blue >= 0 & blue <= 255;

        this.red = red / 255f;
        this.green = green / 255f;
        this.blue = blue / 255f;
    }

    /**
     * Creates a new RColor with the components based on the linear interpolated values.  This will
     * result in the same color as 'from' when position is 0 and the same color as 'to' when
     * position is 1.
     *
     * @param from     The starting color
     * @param to       The ending color
     * @param position The position.  0 <= position <= 1;
     * @return A new RColor with the interpolated values
     */
    public RColor interpolateValues(RColor from, RColor to, float position) {
        assert position >= 0.0f & position <= 1.0f;

        float fromFactor = 1.0f - position;
        return new RColor(from.red * fromFactor + to.red * position, from.green * fromFactor + to
                .green * position, from.blue * fromFactor + to.blue * position);
    }

    /**
     * @return The red component.
     */
    public final float getRed() {
        return red;
    }

    /**
     * @return The green component.
     */
    public final float getGreen() {
        return green;
    }

    /**
     * @return The blue component.
     */
    public final float getBlue() {
        return blue;
    }

    /**
     * @return A new RColor that is darker by the default DARKNESS_FACTOR.
     */
    public RColor darken() {
        return darken(DARKNESS_FACTOR);
    }

    /**
     * Creates a new RColor that is darker by the specified factor.  A factor close to 1.0 (eg.
     * 0.99) represents a small change.  A factor close to 0 represents a large change.
     *
     * @param factor The factor by which to make this color darker (0 <= factor <= 1)
     * @return A new darker color
     */
    public RColor darken(float factor) {
        assert factor >= 0f & factor <= 1.0f;

        return new RColor(red * factor, green * factor, blue * factor);
    }

    /**
     * @return A new RColor that is brighter by using the default DARKNESS_FACTOR.
     */
    public RColor brighten() {
        return brighten(DARKNESS_FACTOR);
    }

    /**
     * Makes the this color brighter by the specified factor.  A factor close to 1.0 (eg. 0.99)
     * represents a small change.  A factor close to 0 represents a large change.
     *
     * @param factor The factor by which to make this color brighter (0 <= factor <= 1)
     */
    public RColor brighten(float factor) {
        assert factor >= 0f & factor <= 1f;

        float temp = 1f - factor;
        return new RColor(temp + red * factor, temp + green * factor, temp + blue * factor);
    }
}
