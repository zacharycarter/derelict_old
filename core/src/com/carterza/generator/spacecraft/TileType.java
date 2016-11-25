package com.carterza.generator.spacecraft;

import com.badlogic.gdx.graphics.Color;
import com.carterza.generator.color.ColorPalette;
import com.stewsters.util.mapgen.CellType;

public enum TileType implements CellType {

    AETHER("Aether", Color.GREEN, 1.0f, true), // This is used as unknown
    VACUUM("Vacuum", new Color(0, 0, 0, 0), 1.0f, true),
    FLOOR("Floor", Color.SLATE, 0.05f, false),
    WALL("Wall", Color.DARK_GRAY, 4.0f, true),
    REINFORCED_WALL("Reinforced Wall", ColorPalette.darker(Color.BLUE), 100.0f, true), // There should not be a path through here unless required
    INTERNALS("Internals", Color.DARK_GRAY, 3.0f, true),
    ARMOR("Armor", Color.GRAY, 1.0f, true),
    DOOR("Door", ColorPalette.darker(Color.GREEN), 0.1f, true);

    public String name;
    public Color color;
    public float digTunnelCost; // Cost of running a hallway through this tile
    public boolean blocks; // Prevents movement
    public boolean visible = true;

    TileType(String name, Color color, float digTunnelCost, boolean blocks) {
        this.name = name;
        this.color = color;
        this.digTunnelCost = digTunnelCost;
        this.blocks = blocks;
    }

}
