package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.Map;

@Getter
public class CustomMenu extends ChestMenu {
    private final String id;
    private final Map<Integer, ItemStack> slotMap;
    private final String title;
    private ItemStack progress;

    public CustomMenu(String id, String title, Map<Integer, ItemStack> mi, boolean emptySlotsClickable, boolean playerInvClickable, @Nullable JavaScriptEval eval) {
        this(id, title, mi, emptySlotsClickable, playerInvClickable, -1, eval);
    }

    public CustomMenu(String id, String title, Map<Integer, ItemStack> mi, boolean emptySlotsClickable, boolean playerInvClickable, int progress, @Nullable JavaScriptEval eval) {
        super(title);
        this.id = id;
        this.slotMap = mi;
        this.title = title;

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

        if (progress > -1 && progress < 54) {
            this.progress = mi.get(progress);
        }

        if (eval != null) {
            addMenuOpeningHandler(p -> eval.evalFunction("onOpen", p));
            addMenuCloseHandler(p -> eval.evalFunction("onClose", p));
        }
    }

    public void setupPreset(BlockMenuPreset preset) {
        for (Map.Entry<Integer, ItemStack> e : slotMap.entrySet()) {
            preset.addItem(e.getKey(), e.getValue());
            preset.addMenuClickHandler(e.getKey(), (p, s, is, a) -> false);
        }
    }
}
