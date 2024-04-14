package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomNoEnergyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.RSCClickHandler;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomMenu extends BlockMenuPreset {
    @Getter
    private final Map<Integer, ItemStack> slotMap;
    private final JavaScriptEval eval;
    @Getter
    private int progressSlot;
    @Getter
    private ItemStack progress;
    @Setter
    private InventoryBlock invb;

    private final List<ItemStack> items;

    private final String title;

    public CustomMenu(String id, String title, CustomMenu menu) {
        this(id, title, menu, menu.eval);
    }

    public CustomMenu(String id, String title, BlockMenuPreset preset, @Nullable JavaScriptEval eval) {
        this(id, title, new HashMap<>(), preset.isPlayerInventoryClickable(), eval);

        cloneOriginalInventory(preset);

        SlimefunItem item = Slimefun.getRegistry().getSlimefunItemIds().get(preset.getID());
        if (item instanceof CustomMachine cm) {
            this.progressSlot = cm.getMenu().getProgressSlot();
            this.progress = cm.getMenu().getProgress();
        } else if (item instanceof CustomNoEnergyMachine cnem) {
            this.progressSlot = cnem.getMenu().getProgressSlot();
            this.progress = cnem.getMenu().getProgress();
        } else if (item instanceof CustomRecipeMachine crm) {
            this.progressSlot = crm.getMenu() != null ? crm.getMenu().getProgressSlot() : 22;
            this.progress = crm.getProgressBar();
        } else if (item instanceof AContainer container) {
            this.progressSlot = 22;
            this.progress = container.getProgressBar();
        }
    }

    public CustomMenu(String id, String title, @NotNull Map<Integer, ItemStack> mi, boolean playerInvClickable, @Nullable JavaScriptEval eval) {
        this(id, title, mi, playerInvClickable, -1, eval);
    }

    public CustomMenu(String id, String title, @NotNull Map<Integer, ItemStack> mi, boolean playerInvClickable, int progress, @Nullable JavaScriptEval eval) {
        super(id, title);

        this.title = title;
        this.slotMap = mi;
        this.eval = eval;
        this.progress = mi.get(progress);
        this.progressSlot = progress;
        setPlayerInventoryClickable(playerInvClickable);

        if (eval != null) {
            eval.doInit();
        }

        items = new ArrayList<>();

        outSideInit();
    }

    public void outSideInit() {
        int size = 9;
        for (int i = 0; i < 54; i ++) {
            ItemStack item = slotMap.get(i);
            if (item != null) {
                addItem(i, item, (RSCClickHandler) (p, slot, is, ca) -> {
                    if (eval != null) {
                        eval.evalFunction("onClick", p, slot, is, ca);
                    }
                });
                size = calcSize(i, item);
            }
        }

        Inventory inventory = Bukkit.createInventory(this, size, CommonUtils.parseToComponent(title));

        for (int i = 0; i < size; i ++) {
            ItemStack item = slotMap.get(i);
            if (item != null) {
                inventory.setItem(i, item);
            }
        }

        this.inventory = inventory;

        if (eval != null) {
            addMenuOpeningHandler(p -> eval.evalFunction("onOpen", p));
            addMenuCloseHandler(p -> eval.evalFunction("onClose", p));
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

    private void cloneOriginalInventory(BlockMenuPreset preset) {
        preset.getContents();
        Inventory inventory = Bukkit.createInventory(this, preset.toInventory().getSize(), CommonUtils.parseToComponent(title));

        for (int i = 0; i < preset.getInventory().getSize(); i++) {
            ItemStack item = preset.getItemInSlot(i);
            if (item != null) {
                this.addItem(i, item.clone());
                inventory.setItem(i, item.clone());
            }
            if (preset.getMenuClickHandler(i) != null) {
                this.addMenuClickHandler(i, preset.getMenuClickHandler(i));
            }
        }

        this.inventory = inventory;
    }

    private int calcSize(int slot, ItemStack item) {
        int size = this.items.size();
        if (size > slot) {
            this.items.set(slot, item);
        } else {
            for(int i = 0; i < slot - size; ++i) {
                this.items.add(null);
            }

            this.items.add(item);
        }

        return items.size();
    }
}
