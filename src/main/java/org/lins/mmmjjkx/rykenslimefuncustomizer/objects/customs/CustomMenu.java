package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
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
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.Colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomNoEnergyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.RSCClickHandler;

public class CustomMenu extends BlockMenuPreset {
    @Getter
    private final Map<Integer, ItemStack> slotMap;

    private final JavaScriptEval eval;

    @Getter
    private int progressSlot;

    private ItemStack progress;

    @Setter
    private InventoryBlock invb;

    private final String title;

    public CustomMenu(String id, String title, CustomMenu menu) {
        this(id, title, menu, menu.getProgressBarItem(), menu.eval);

        this.progressSlot = menu.progressSlot;
    }

    public CustomMenu(
            String id,
            String title,
            BlockMenuPreset preset,
            @Nullable ItemStack progressBar,
            @Nullable JavaScriptEval eval) {
        this(id, title, new HashMap<>(), preset.isPlayerInventoryClickable(), 22, progressBar, eval);

        cloneOriginalInventory(preset);

        SlimefunItem item = Slimefun.getRegistry().getSlimefunItemIds().get(preset.getID());
        if (item instanceof CustomMachine cm) {
            this.progressSlot = cm.getMenu().getProgressSlot();
            this.progress = cm.getMenu().getProgressBarItem();
        } else if (item instanceof CustomNoEnergyMachine cnem) {
            this.progressSlot = cnem.getMenu().getProgressSlot();
            this.progress = cnem.getMenu().getProgressBarItem();
        } else if (item instanceof CustomRecipeMachine crm) {
            this.progressSlot = crm.getMenu() != null ? crm.getMenu().getProgressSlot() : 22;
            this.progress = crm.getProgressBar();
        } else if (item instanceof AContainer container) {
            this.progressSlot = 22;
            this.progress = container.getProgressBar();
        }
    }

    public CustomMenu(
            String id,
            String title,
            @NotNull Map<Integer, ItemStack> mi,
            boolean playerInvClickable,
            int progress,
            @Nullable ItemStack progressBar,
            @Nullable JavaScriptEval eval) {
        super(id, CMIChatColor.translate(title));

        this.title = title;
        this.slotMap = mi;
        this.eval = eval;
        this.progress = progressBar != null ? progressBar.clone() : mi.get(progress);
        this.progressSlot = progress;
        setPlayerInventoryClickable(playerInvClickable);

        if (eval != null) {
            eval.doInit();
        }

        outSideInit();
    }

    public void outSideInit() {
        for (int i = 0; i < 54; i++) {
            ItemStack item = slotMap.get(i);
            if (item != null) {
                addItem(i, item, (RSCClickHandler) (p, slot, is, ca) -> {
                    if (eval != null) {
                        eval.evalFunction("onClick", p, slot, is, ca);
                    }
                });
            }
        }

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

    @Nullable public ItemStack getProgressBarItem() {
        return progress;
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
        Inventory inventory =
                Bukkit.createInventory(this, preset.toInventory().getSize(), CMIChatColor.translate(title));

        for (int i = 0; i < preset.getInventory().getSize(); i++) {
            ItemStack item = preset.getItemInSlot(i);
            if (item != null) {
                this.addItem(i, item.clone());
                inventory.setItem(i, item.clone());
            }

            MenuClickHandler handler = preset.getMenuClickHandler(i);

            if (handler != null) {
                this.addMenuClickHandler(i, new RSCClickHandler() {
                    @Override
                    public void mainFunction(Player player, int slot, ItemStack itemStack, ClickAction action) {
                        handler.onClick(player, slot, itemStack, action);
                    }

                    @Override
                    public void andThen(Player player, int slot, ItemStack itemStack, ClickAction action) {
                        if (eval != null) {
                            eval.evalFunction("onClick", player, slot, itemStack, action);
                        }
                    }
                });
            }
        }

        this.inventory = inventory;
    }
}
