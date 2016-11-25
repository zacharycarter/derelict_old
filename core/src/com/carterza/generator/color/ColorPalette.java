package com.carterza.generator.color;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ColorPalette {

    private static final double FACTOR = 0.7;

    public List<Color> colors;


    public ColorPalette() {

    }

    public void generate() {
        colors = ColorGenerator.generate(40, 2);
    }


    public ColorPalette sub() {

        Random random = new Random();
        ColorPalette output = new ColorPalette();

        output.colors = new ArrayList<Color>();

        for (int i = 0; i < random.nextInt(2) + 1; i++) {

            Color color = colors.get(random.nextInt(colors.size()));

            output.colors.add(darker(color));
            output.colors.add(color);
            output.colors.add(brighter(color));

            if (random.nextBoolean())
                Collections.shuffle(colors);

        }

        return output;

    }

    public static Color darker(final Color color) {
        return new Color(Math.max((int)(color.r  *FACTOR), 0) / 255.0f,
                Math.max((int)(color.g*FACTOR), 0) / 255.0f,
                Math.max((int)(color.b *FACTOR), 0) / 255.0f,
                color.a);
    }

    public static Color brighter(final Color color) {
        /*int r = getRed();
        int g = getGreen();
        int b = getBlue();
        int alpha = getAlpha();*/

        float r = color.r;
        float g = color.g;
        float b = color.b;
        float alpha = color.a;

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i / 255.0f, i / 255.0f, i / 255.0f, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/FACTOR), 255) / 255.0f,
                Math.min((int)(g/FACTOR), 255) / 255.0f,
                Math.min((int)(b/FACTOR), 255) / 255.0f,
                alpha);
    }

}
