package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.inventory.ItemStack;
import org.graalvm.polyglot.HostAccess;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;

public class CustomEnergyItem extends CustomItem implements Rechargeable, NotPlaceable {
    private final float capacity;

    private final Object[] constructorArgs;

    public CustomEnergyItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, float capacity, @Nullable ScriptEval eval) {
        super(itemGroup, item, recipeType, recipe);

        this.capacity = capacity;

        if (eval != null) {
            eval.doInit();

            this.addItemHandler((ItemUseHandler) e -> {
                eval.evalFunction("onUse", e, this);
                e.cancel();
            });
        } else {
            this.addItemHandler((ItemUseHandler) PlayerRightClickEvent::cancel);
        }

        this.constructorArgs = new Object[]{itemGroup, item, recipeType, recipe, capacity, eval};
    }

    @HostAccess.Export
    public void setItemCharge(ItemStack item, int charge) {
        Rechargeable.super.setItemCharge(item, charge);
    }

    @HostAccess.Export
    public void setItemCharge(ItemStack item, double charge) {
        Rechargeable.super.setItemCharge(item, (float) charge);
    }

    @HostAccess.Export
    public void addItemCharge(ItemStack item, int charge) {
        Rechargeable.super.addItemCharge(item, charge);
    }

    @HostAccess.Export
    public void addItemCharge(ItemStack item, double charge) {
        Rechargeable.super.addItemCharge(item, (float) charge);
    }

    @HostAccess.Export
    public void removeItemCharge(ItemStack item, int charge) {
        Rechargeable.super.removeItemCharge(item, charge);
    }

    @HostAccess.Export
    public void removeItemCharge(ItemStack item, double charge) {
        Rechargeable.super.addItemCharge(item, (float) charge);
    }

    @HostAccess.Export
    public float getItemCharge(ItemStack item) {
        return Rechargeable.super.getItemCharge(item);
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return capacity;
    }

    @Override
    public Object[] constructorArgs() {
        return constructorArgs;
    }
}
