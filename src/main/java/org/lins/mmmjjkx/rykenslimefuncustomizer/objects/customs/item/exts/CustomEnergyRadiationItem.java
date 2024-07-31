package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.BaseRadiationItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;

public class CustomEnergyRadiationItem extends BaseRadiationItem implements NotPlaceable, Rechargeable {
    private final Radioactivity radioactivity;
    private final float capacity;
    private final Object[] constructArgs;

    public CustomEnergyRadiationItem(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            Radioactivity radioactivity,
            float capacity,
            ScriptEval eval) {
        super(itemGroup, radioactivity, item, recipeType, recipe);
        this.radioactivity = radioactivity;
        this.capacity = capacity;
        this.constructArgs = new Object[] {itemGroup, item, recipeType, recipe, radioactivity, capacity, eval};

        if (eval != null) {
            eval.doInit();

            this.addItemHandler((ItemUseHandler) e -> {
                eval.evalFunction("onUse", e, this);
                e.cancel();
            });
        } else {
            this.addItemHandler((ItemUseHandler) PlayerRightClickEvent::cancel);
        }
    }

    public void setItemCharge(ItemStack item, int charge) {
        Rechargeable.super.setItemCharge(item, charge);
    }

    public void setItemCharge(ItemStack item, double charge) {
        Rechargeable.super.setItemCharge(item, (float) charge);
    }

    public void addItemCharge(ItemStack item, int charge) {
        Rechargeable.super.addItemCharge(item, charge);
    }

    public void addItemCharge(ItemStack item, double charge) {
        Rechargeable.super.addItemCharge(item, (float) charge);
    }
    
    public void removeItemCharge(ItemStack item, int charge) {
        Rechargeable.super.removeItemCharge(item, charge);
    }

    public void removeItemCharge(ItemStack item, double charge) {
        Rechargeable.super.addItemCharge(item, (float) charge);
    }

    public float getItemCharge(ItemStack item) {
        return Rechargeable.super.getItemCharge(item);
    }

    @NotNull @Override
    public Radioactivity getRadioactivity() {
        return radioactivity;
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return capacity;
    }

    @Override
    public Object[] constructArgs() {
        return constructArgs;
    }
}
