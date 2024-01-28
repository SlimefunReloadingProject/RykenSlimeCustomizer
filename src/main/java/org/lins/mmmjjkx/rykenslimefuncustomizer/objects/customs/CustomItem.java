package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record.CommandOperation;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomItem extends SlimefunItem {
    private static final Map<String, String> tempValue = new HashMap<>();
    private boolean hasOperation = false;
    private @Nullable CommandOperation operation;

    public CustomItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        register(RykenSlimefunCustomizer.INSTANCE);
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

    public void addOperation(CommandOperation operation) {
        if (!hasOperation) {
            this.addItemHandler((ItemUseHandler) e -> operation.run(e.getPlayer(), this));
            this.operation = operation;
            hasOperation = true;
        }
    }
}
