package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoAnvil;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class AdvancedAutoAnvil extends AutoAnvil {
    private final int repairFactor;
    private final int speed;

    public AdvancedAutoAnvil(ItemGroup itemGroup, int repairFactor, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int speed) {
        super(itemGroup, repairFactor, item, recipeType, recipe);

        this.repairFactor = repairFactor;
        this.speed = speed;
    }

    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for(int slot : this.getInputSlots()) {
            ItemStack ductTape = menu.getItemInSlot(slot == this.getInputSlots()[0] ? this.getInputSlots()[1] : this.getInputSlots()[0]);
            ItemStack item = menu.getItemInSlot(slot);
            if (item != null && item.getType().getMaxDurability() > 0 && ((Damageable)item.getItemMeta()).getDamage() > 0) {
                if (SlimefunUtils.isItemSimilar(ductTape, SlimefunItems.DUCT_TAPE, true, false)) {
                    ItemStack repairedItem = this.repair(item);
                    if (!menu.fits(repairedItem, this.getOutputSlots())) {
                        return null;
                    }

                    for(int inputSlot : this.getInputSlots()) {
                        menu.consumeItem(inputSlot);
                    }

                    return new MachineRecipe(30 / this.speed, new ItemStack[]{ductTape, item}, new ItemStack[]{repairedItem});
                }
                break;
            }
        }

        return null;
    }

    private ItemStack repair(ItemStack item) {
        ItemStack repaired = item.clone();
        ItemMeta meta = repaired.getItemMeta();
        short maxDurability = item.getType().getMaxDurability();
        int repairPercentage = 100 / this.repairFactor;
        short durability = (short)(((Damageable)meta).getDamage() - maxDurability / repairPercentage);
        if (durability < 0) {
            durability = 0;
        }

        ((Damageable)meta).setDamage(durability);
        repaired.setItemMeta(meta);
        return repaired;
    }
}
