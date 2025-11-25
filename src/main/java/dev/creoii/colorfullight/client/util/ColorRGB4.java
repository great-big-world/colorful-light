package dev.creoii.colorfullight.client.util;

import net.minecraft.util.DyeColor;

import java.awt.*;

public class ColorRGB4 {
    public static final ColorRGB4 ZERO = new ColorRGB4(0, 0, 0);
    private static final Color PURPLE = new Color(DyeColor.PURPLE.getSignColor());
    public static final ColorRGB4 ENCHANTED = ColorRGB4.fromRGB8(PURPLE.getRed(), PURPLE.getGreen(), PURPLE.getBlue());
    public int red4, green4, blue4;

    public static ColorRGB4 fromRGB8(ColorRGB8 other) {
        return fromRGB8(other.red, other.green, other.blue);
    }
    public static ColorRGB4 fromRGB8(int r, int g, int b) {
        return fromRGB4(r / 17, g / 17, b / 17); // 0..255 range to 0..15 range
    }

    public static ColorRGB4 fromRGB4(int r, int g, int b) {
        return new ColorRGB4(r, g, b);
    }

    public static ColorRGB4 fromRGBFloat(float r, float g, float b) {
        return new ColorRGB4((int)(r * 15), (int)(g * 15), (int)(b * 15));
    }

    private ColorRGB4(int r4, int g4, int b4) {
        red4 = r4;
        green4 = g4;
        blue4 = b4;
    }

    public boolean isInValidState() {
        return red4 >= 0 && red4 < 16 && green4 >= 0 && green4 < 16 && blue4 >= 0 && blue4 < 16;
    }

    public boolean isZero() {
        return red4 == 0 && green4 == 0 && blue4 == 0;
    }

    public boolean isWhite() {
        return red4 >= 15 && green4 >= 15 && blue4 >= 15;
    }

    public ColorRGB4 add(ColorRGB4 other) {
        return new ColorRGB4(red4 + other.red4, green4 + other.green4, blue4 + other.blue4);
    }

    public ColorRGB4 mul(float scalar) {
        return new ColorRGB4((int)(red4 * scalar), (int)(green4 * scalar), (int)(blue4 * scalar));
    }

    @Override
    public String toString() {
        return "ColorRGB4["+ red4 +", " + green4 + ", " + blue4 + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        return  obj instanceof ColorRGB4 other &&
                other.red4 == red4 &&
                other.green4 == green4 &&
                other.blue4 == blue4;
    }

    public static ColorRGB4 linearInterpolation(ColorRGB4 a, ColorRGB4 b, float x) {
        if(a.isZero()) return b;
        if(b.isZero()) return a;
        return a.mul(1f - x).add(b.mul(x));
    }

    public static ColorRGB4 max(ColorRGB4 a, ColorRGB4 b) {
        if(a.isZero()) return b;
        if(b.isZero()) return a;
        int maxR = Math.max(a.red4, b.red4);
        int maxG = Math.max(a.green4, b.green4);
        int maxB = Math.max(a.blue4, b.blue4);
        return new ColorRGB4(maxR, maxG, maxB);
    }
}