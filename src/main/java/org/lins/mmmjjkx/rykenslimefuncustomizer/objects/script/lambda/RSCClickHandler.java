package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
@FunctionalInterface
public interface RSCClickHandler extends ChestMenu.MenuClickHandler {
    boolean mainFunction(Player player, int slot, ItemStack itemStack, ClickAction action);

    default boolean onClick(Player var1, int var2, ItemStack var3, ClickAction var4) {
        boolean b = mainFunction(var1, var2, var3, var4);
        boolean b2 = andThen(var1, var2, var3, var4);

        if (b != b2) {
            b = b2;
        }

        return b;
    }

    default boolean andThen(Player player, int slot, ItemStack itemStack, ClickAction action) {
        return false;
    }
}
