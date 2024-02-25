package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class CustomMenu extends BlockMenuPreset {
    private final Map<Integer, ItemStack> slotMap;
    private final JavaScriptEval eval;
    @Getter
    private int progressSlot;
    @Getter
    private ItemStack progress;
    @Setter
    private InventoryBlock invb;

    public CustomMenu(BlockMenuPreset preset, @Nullable JavaScriptEval eval) {
        this(preset.getID(), preset.getTitle(), new HashMap<>(), true, eval);

        setInventory(preset.toInventory());

        SlimefunItem item = Slimefun.getRegistry().getSlimefunItemIds().get(preset.getID());
        if (item instanceof AContainer container) {
            this.progressSlot = 22;
            this.progress = container.getProgressBar();
        }
    }

    public CustomMenu(String id, String title, @NotNull Map<Integer, ItemStack> mi, boolean playerInvClickable, @Nullable JavaScriptEval eval) {
        this(id, title, mi, playerInvClickable, -1, eval);
    }

    public CustomMenu(String id, String title, @NotNull Map<Integer, ItemStack> mi, boolean playerInvClickable, int progress, @Nullable JavaScriptEval eval) {
        super(id, title);
        this.slotMap = mi;
        this.eval = eval;
        this.progress = mi.get(progress);
        this.progressSlot = progress;
        setPlayerInventoryClickable(playerInvClickable);

        if (eval != null) {
            eval.doInit();
        }
    }

    public void outSideInit() {
        for (int i = 0; i <  54; i ++) {
            ItemStack item = slotMap.get(i);
            if (item != null) {
                addItem(i, item, ChestMenuUtils.getEmptyClickHandler());
            }
        }

        if (eval != null) {
            if (eval.hasFunction("onOpen")) {
                addMenuOpeningHandler(p -> eval.evalFunction("onOpen", p));
            }
            if (eval.hasFunction("onClose")) {
                addMenuCloseHandler(p -> eval.evalFunction("onClose", p));
            }
        }
    }

    @Override
    public void init() {}

    @Override
    public boolean canOpen(@NotNull Block b, @NotNull Player p) {
        return Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
        if (invb != null) {
            return flow == ItemTransportFlow.INSERT ? invb.getInputSlots() : invb.getOutputSlots();
        }
        return new int[0];
    }

    public void reInit() {
        Slimefun.getRegistry().getMenuPresets().remove(getID());
        Slimefun.getRegistry().getMenuPresets().put(getID(), this);
    }
}
