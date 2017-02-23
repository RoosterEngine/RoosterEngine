package gameengine.graphics;

/**
 * Represents a color.
 */
public class MutableColor {
    /**
     * used for making colors brighter or darker.
     */
    public static final float DARKNESS_FACTOR = 0.7f;

    /**
     * The individual color components.
     */
    private float red, green, blue;

    /**
     * Creates a MutableColor instance by copying the provided instance.
     *
     * @param c The color to be copied
     */
    public MutableColor(MutableColor c) {
        this(c.red, c.green, c.blue);
    }

    /**
     * Creates a MutableColor instance.
     *
     * @param red   The red component (0 <= red <= 1)
     * @param green The green component (0 <= green <= 1)
     * @param blue  The blue component (0 <= blue <= 1)
     */
    public MutableColor(float red, float green, float blue) {
        setValues(red, green, blue);
    }

    /**
     * Creates a MutableColor instance.
     *
     * @param red   The red component (0 <= red <= 255)
     * @param green The green component (0 <= green <= 255)
     * @param blue  The blue component (0 <= blue <= 255)
     */
    public MutableColor(int red, int green, int blue) {
        setValues(red, green, blue);
    }

    /**
     * Sets the color components based on the linear interpolated values.  This will result in the
     * same color as 'from' when position is 0 and the same color as 'to' when position is 1.
     *
     * @param from     The starting color
     * @param to       The ending color
     * @param position The position.  0 <= position <= 1;
     */
    public void interpolateValues(MutableColor from, MutableColor to, float position) {
        assert position >= 0.0f & position <= 1.0f;

        float fromFactor = 1.0f - position;
        red = from.red * fromFactor + to.red * position;
        green = from.green * fromFactor + to.green * position;
        blue = from.blue * fromFactor + to.blue * position;
    }

    /**
     * Set the color components.
     *
     * @param red   The red component (0 <= red <= 1)
     * @param green The green component (0 <= green <= 1)
     * @param blue  The blue component (0 <= blue <= 1)
     */
    public final void setValues(float red, float green, float blue) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    /**
     * Sets the color components.
     *
     * @param red   The red component (0 <= red <= 255)
     * @param green The green component (0 <= green <= 255)
     * @param blue  The blue component (0 <= blue <= 255)
     */
    public final void setValues(int red, int green, int blue) {
        setRed(red / 255f);
        setGreen(green / 255f);
        setBlue(blue / 255f);
    }

    /**
     * @return The red component.
     */
    public final float getRed() {
        return red;
    }

    /**
     * Sets the red component.
     *
     * @param red The red component (0 <= red <= 1)
     */
    public final void setRed(float red) {
        assert red >= 0f & red <= 1f;

        this.red = red;
    }

    /**
     * @return The green component.
     */
    public final float getGreen() {
        return green;
    }

    /**
     * Sets the green component.
     *
     * @param green The green component (0 <= green <= 1)
     */
    public final void setGreen(float green) {
        assert green >= 0f & green <= 1f;

        this.green = green;
    }

    /**
     * @return The blue component.
     */
    public final float getBlue() {
        return blue;
    }

    /**
     * Sets the blue component.
     *
     * @param blue The blue component (0 <= blue <= 1)
     */
    public final void setBlue(float blue) {
        assert blue >= 0f & blue <= 1f;

        this.blue = blue;
    }

    /**
     * Makes this color darker by the default DARKNESS_FACTOR.
     */
    public void darken() {
        darken(DARKNESS_FACTOR);
    }

    /**
     * Makes the this color darker by the specified factor.  A factor close to 1.0 (eg. 0.99)
     * represents a small change.  A factor close to 0 represents a large change.
     *
     * @param factor The factor by which to make this color darker (0 <= factor <= 1)
     */
    public void darken(float factor) {
        assert factor >= 0f & factor <= 1.0f;

        red *= factor;
        green *= factor;
        blue *= factor;
    }

    /**
     * Makes this color brighter by using the default DARKNESS_FACTOR.
     */
    public void brighten() {
        brighten(DARKNESS_FACTOR);
    }

    /**
     * Makes the this color brighter by the specified factor.  A factor close to 1.0 (eg. 0.99)
     * represents a small change.  A factor close to 0 represents a large change.
     *
     * @param factor The factor by which to make this color brighter (0 <= factor <= 1)
     */
    public void brighten(float factor) {
        assert factor >= 0f & factor <= 1f;

        float temp = 1f - factor;
        red = temp + red * factor;
        green = temp + green * factor;
        blue = temp + blue * factor;
    }

    /**
     * @return A new red MutableColor.
     */
    public static MutableColor createRedInstance() {
        return new MutableColor(1.0f, 0.0f, 0.0f);
    }

    /**
     * @return A new green MutableColor.
     */
    public static MutableColor createGreenInstance() {
        return new MutableColor(0.0f, 1.0f, 0.0f);
    }

    /**
     * @return A new blue MutableColor.
     */
    public static MutableColor createBlueInstance() {
        return new MutableColor(0.0f, 0.0f, 1.0f);
    }

    /**
     * @return A new black MutableColor.
     */
    public static MutableColor createBlackInstance() {
        return new MutableColor(0.0f, 0.0f, 0.0f);
    }

    /**
     * @return A new white MutableColor.
     */
    public static MutableColor createWhiteInstance() {
        return new MutableColor(1.0f, 1.0f, 1.0f);
    }

    /**
     * @return A new cyan MutableColor.
     */
    public static MutableColor createCyanInstance() {
        return new MutableColor(0.0f, 1.0f, 1.0f);
    }

    /**
     * @return A new magenta MutableColor.
     */
    public static MutableColor createMagentaInstance() {
        return new MutableColor(1.0f, 0.0f, 1.0f);
    }

    /**
     * @return A new pink MutableColor.
     */
    public static MutableColor createPinkInstance() {
        return new MutableColor(1.0f, 192f / 255f, 203f / 255f);
    }

    /**
     * @return A new yellow MutableColor.
     */
    public static MutableColor createYellowInstance() {
        return new MutableColor(1.0f, 1.0f, 0.0f);
    }

    /**
     * @return A new orange MutableColor.
     */
    public static MutableColor createOrangeInstance() {
        return new MutableColor(1.0f, 127f / 255f, 0.0f);
    }

    /**
     * @return A new brown MutableColor.
     */
    public static MutableColor createBrownInstance() {
        return new MutableColor(150f / 255f, 75f / 255f, 0.0f);
    }

    /**
     * @return A new gray MutableColor.
     */
    public static MutableColor createGrayInstance() {
        return new MutableColor(0.5f, 0.5f, 0.5f);
    }

    /**
     * @return A new violet MutableColor.
     */
    public static MutableColor createVioletInstance() {
        return new MutableColor(143f / 255f, 0.0f, 1.0f);
    }
}
