package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
@FunctionalInterface
public interface RSCClickHandler extends ChestMenu.MenuClickHandler {
    void mainFunction(Player player, int slot, ItemStack itemStack, ClickAction action);

    default boolean onClick(Player var1, int var2, ItemStack var3, ClickAction var4) {
        mainFunction(var1, var2, var3, var4);
        andThen(var1, var2, var3, var4);
        return false;
    }

    default void andThen(Player player, int slot, ItemStack itemStack, ClickAction action) {}
}
