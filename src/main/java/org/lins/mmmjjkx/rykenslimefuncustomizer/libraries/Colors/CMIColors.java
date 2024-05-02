package org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.Colors;

import java.awt.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

public enum CMIColors {
    White(0, "White", Material.WHITE_DYE, new Color(249, 255, 254)),
    Orange(1, "Orange", Material.ORANGE_DYE, new Color(249, 128, 29)),
    Magenta(2, "Magenta", Material.MAGENTA_DYE, new Color(199, 78, 189)),
    Light_Blue(3, "Light Blue", Material.LIGHT_BLUE_DYE, new Color(58, 179, 218)),
    Yellow(4, "Yellow", Material.YELLOW_DYE, new Color(254, 216, 61)),
    Lime(5, "Lime", Material.LIME_DYE, new Color(128, 199, 31)),
    Pink(6, "Pink", Material.PINK_DYE, new Color(243, 139, 170)),
    Gray(7, "Gray", Material.GRAY_DYE, new Color(71, 79, 82)),
    Light_Gray(8, "Light Gray", Material.LIGHT_GRAY_DYE, new Color(157, 157, 151)),
    Cyan(9, "Cyan", Material.CYAN_DYE, new Color(22, 156, 156)),
    Purple(10, "Purple", Material.PURPLE_DYE, new Color(137, 50, 184)),
    Blue(11, "Blue", Material.BLUE_DYE, new Color(60, 68, 170)),
    Brown(12, "Brown", Material.BROWN_DYE, new Color(131, 84, 50)),
    Green(13, "Green", Material.GREEN_DYE, new Color(94, 124, 22)),
    Red(14, "Red", Material.RED_DYE, new Color(176, 46, 38)),
    Black(15, "Black", Material.BLACK_DYE, new Color(29, 29, 33));

    @Getter
    private int id;

    @Getter
    private String name;

    @Setter
    private Material material;

    private Color color;

    CMIColors(int id, String name, Material material, Color color) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.color = color;
    }

    CMIColors(int id, String name, Material material) {
        this.id = id;
        this.name = name;
        this.material = material;
    }

    public static CMIColors getById(int id) {
        for (CMIColors one : CMIColors.values()) {
            if (one.getId() == id) return one;
        }
        return CMIColors.White;
    }

    public Material getMaterial() {
        return material;
    }

    public Color getColor() {
        return color;
    }
}
