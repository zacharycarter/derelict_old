package com.carterza.generator.spacecraft;

import com.badlogic.gdx.graphics.Pixmap;
import com.carterza.generator.color.ColorPalette;

public class Blueprint {

    // A black, white, and transparent image used to lay out the spacecraft.
    public Pixmap spec;

    // The colors we will use on the outside of the spacecraft
    public ColorPalette colorPalette;

    // All ships are assumed to go face upwards
    public int width; //in pixels
    public int height;

}
