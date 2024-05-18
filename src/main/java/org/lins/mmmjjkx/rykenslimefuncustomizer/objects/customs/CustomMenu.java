package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
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

@SuppressWarnings("deprecation")
public class CustomMenu {
    @Getter
    private final Map<Integer, ItemStack> slotMap;

    @Getter
    private final JavaScriptEval eval;

    @Getter
    private final String title;

    @Getter
    private final String id;

    @Getter
    private int progressSlot;

    @Getter
    private ItemStack progress;

    @Setter
    private InventoryBlock invb;

    @Setter
    private boolean playerInvClickable;

    @Setter
    private ChestMenu.MenuOpeningHandler menuOpeningHandler = p -> {};

    @Setter
    private ChestMenu.MenuCloseHandler menuCloseHandler = p -> {};

    private final Map<Integer, ItemStack> items;
    private final Map<Integer, ChestMenu.MenuClickHandler> clickHandlers;

    public CustomMenu(String id, String title, CustomMenu menu) {
        this.slotMap = menu.slotMap;
        this.eval = menu.eval;
        this.title = CMIChatColor.translate(title);
        this.id = id;
        this.items = menu.items;
        this.progressSlot = menu.progressSlot;
        this.clickHandlers = menu.clickHandlers;

        if (eval != null) {
            menuOpeningHandler = menu.menuOpeningHandler;
            menuCloseHandler = menu.menuCloseHandler;
        }
    }

    public CustomMenu(
            String id,
            String title,
            @Nullable BlockMenuPreset preset,
            @Nullable ItemStack progressBar,
            @Nullable JavaScriptEval eval) {
        this(id, title, new HashMap<>(), preset == null || preset.isPlayerInventoryClickable(), 22, progressBar, eval);

        if (preset != null) {
            cloneFromPresetInventory(preset);

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
    }

    public CustomMenu(
            String id,
            String title,
            @NotNull Map<Integer, ItemStack> mi,
            boolean playerInvClickable,
            int progress,
            @Nullable ItemStack progressBar,
            @Nullable JavaScriptEval eval) {

        this.id = id;
        this.title = CMIChatColor.translate(title);
        this.slotMap = mi;
        this.eval = eval;
        this.progress = progressBar != null ? progressBar.clone() : mi.get(progress);
        this.progressSlot = progress;
        this.playerInvClickable = playerInvClickable;

        this.items = new HashMap<>();
        this.clickHandlers = new HashMap<>();

        if (eval != null) {
            eval.doInit();
        }

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
            menuOpeningHandler = (p -> eval.evalFunction("onOpen", p));
            menuCloseHandler = (p -> eval.evalFunction("onClose", p));
        }
    }

    private void addItem(int i, ItemStack item, ChestMenu.MenuClickHandler onClick) {
        items.put(i, item);
        clickHandlers.put(i, onClick);
    }

    private void addItem(int i, ItemStack item) {
        items.put(i, item);
    }

    private ChestMenu.MenuClickHandler getClickHandler(int slot) {
        return clickHandlers.getOrDefault(slot, ((player, i, itemStack, clickAction) -> true));
    }

    @Nullable public ItemStack getProgressBarItem() {
        return progress;
    }

    public String getID() {
        return id;
    }

    public void reInit() {
        Slimefun.getRegistry().getMenuPresets().remove(id);
        BlockMenuPreset preset = createSimplePreset();
        apply(preset);
        Slimefun.getRegistry().getMenuPresets().put(id, preset);
    }

    private void cloneFromPresetInventory(BlockMenuPreset preset) {
        preset.getContents();
        Inventory inventory =
                Bukkit.createInventory(null, preset.toInventory().getSize(), CMIChatColor.translate(title));

        for (int i = 0; i < preset.getInventory().getSize(); i++) {
            ItemStack item = preset.getItemInSlot(i);
            if (item != null) {
                this.addItem(i, item.clone());
                inventory.setItem(i, item.clone());
            }

            ChestMenu.MenuClickHandler handler = preset.getMenuClickHandler(i);

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
    }

    public void addMenuClickHandler(int i, ChestMenu.MenuClickHandler onClick) {
        clickHandlers.put(i, onClick);
    }

    protected BlockMenuPreset createSimplePreset() {
        return new BlockMenuPreset(id, title) {
            @Override
            public void init() {
                apply(this);
            }

            @Override
            public boolean canOpen(@NotNull Block b, @NotNull Player p) {
                return Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (invb == null) {
                    return new int[0];
                }
                return flow == ItemTransportFlow.INSERT ? invb.getInputSlots() : invb.getOutputSlots();
            }
        };
    }

    public void apply(BlockMenuPreset preset) {
        preset.setPlayerInventoryClickable(playerInvClickable);

        for (int slot : items.keySet()) {
            preset.addItem(slot, items.get(slot), getClickHandler(slot));
        }

        preset.addMenuOpeningHandler(menuOpeningHandler);
        preset.addMenuCloseHandler(menuCloseHandler);
    }

    @Nullable public ChestMenu.MenuClickHandler getMenuClickHandler(int workSlot) {
        return clickHandlers.get(workSlot);
    }
}
