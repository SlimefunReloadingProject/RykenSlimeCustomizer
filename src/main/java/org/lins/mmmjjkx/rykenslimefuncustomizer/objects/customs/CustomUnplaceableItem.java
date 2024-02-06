package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;

import java.util.List;
import java.util.Optional;

public class CustomUnplaceableItem extends CustomItem implements NotPlaceable {
    private final List<Material> materials = List.of(
            Material.CRAFTING_TABLE, Material.CHEST, Material.DISPENSER
    );

    public CustomUnplaceableItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable JavaScriptEval eval) {
        super(itemGroup, item, recipeType, recipe, eval);

        this.addItemHandler((ItemUseHandler) e -> {
            Player p = e.getPlayer();
            Optional<Block> clicked = e.getClickedBlock();
            ItemStack itemStack = e.getItem();
            if (clicked.isPresent()) {
                Block b = clicked.get();

                if (itemStack.getType().isBlock()) {
                    if (p.isSneaking()) {
                        e.cancel();
                    }
                }

                if (materials.contains(b.getType())) {
                    if (p.isSneaking()) {
                        e.cancel();
                    }
                }
            } else {
                e.cancel();
            }
        });

        register(RykenSlimefunCustomizer.INSTANCE);
    }
}
