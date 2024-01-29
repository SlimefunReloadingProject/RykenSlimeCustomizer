package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.Map;

@Getter
public class CustomMenu extends ChestMenu {
    private ItemStack progress;

    public CustomMenu(String title, Map<Integer, ItemStack> mi, boolean emptySlotsClickable, boolean playerInvClickable) {
        this(title, mi, emptySlotsClickable, playerInvClickable, -1);
    }

    public CustomMenu(String title, Map<Integer, ItemStack> mi, boolean emptySlotsClickable, boolean playerInvClickable, int progress) {
        super(title);
        int maxSlot = 0;
        for (Map.Entry<Integer, ItemStack> e : mi.entrySet()) {
            addItem(e.getKey(), e.getValue());
            addMenuClickHandler(e.getKey(), (p, s, is, a) -> false);
            maxSlot = e.getKey();
        }
        if (maxSlot > 54) {
            maxSlot = 54;
        } else if (maxSlot < 9) {
            maxSlot = 9;
        } else if (maxSlot % 9 > 0) {
            maxSlot = (maxSlot/9) * 9;
        }
        int line = maxSlot / 9;
        Inventory inventory = Bukkit.createInventory(null, line * 9, CommonUtils.parseToComponent(title));
        setInventory(inventory);
        setEmptySlotsClickable(emptySlotsClickable);
        setPlayerInventoryClickable(playerInvClickable);

        if (progress > -1) {
            this.progress = mi.get(progress);
        }
    }
}
