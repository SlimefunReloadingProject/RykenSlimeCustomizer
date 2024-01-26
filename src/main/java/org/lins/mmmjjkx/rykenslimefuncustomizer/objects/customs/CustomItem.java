package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomItem extends SlimefunItem {
    private static final Map<String, String> tempValue = new HashMap<>();

    public CustomItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public void storeData(String key, String value) {
        tempValue.put(key, value);
    }

    public void removeData(String key) {
        tempValue.remove(key);
    }

    public Map<String, String> getDataMap() {
        return tempValue;
    }
}
