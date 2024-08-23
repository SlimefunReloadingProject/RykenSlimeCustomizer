package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomNoEnergyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.RSCClickHandler;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.MenuReader;

@SuppressWarnings("deprecation")
public class CustomMenu {
    @Getter
    private final JavaScriptEval eval;

    @Getter
    private final String title;

    private final String id;

    @Getter
    private int progressSlot;

    @Getter
    private int size;

    @Getter
    private ItemStack progress;

    @Setter
    private boolean playerInvClickable;

    @Setter
    private ChestMenu.MenuOpeningHandler menuOpeningHandler = p -> {};

    @Setter
    private ChestMenu.MenuCloseHandler menuCloseHandler = p -> {};

    @Getter
    private final Map<Integer, ItemStack> items;

    private final Map<Integer, ChestMenu.MenuClickHandler> clickHandlers;

    public CustomMenu(String id, String title, CustomMenu menu) {
        this.eval = menu.eval;
        this.title = CMIChatColor.translate(title);
        this.id = id;
        this.items = menu.items;
        this.progressSlot = menu.progressSlot;
        this.clickHandlers = menu.clickHandlers;
        this.size = menu.size;

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

            this.size = preset.getSize();
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
        this.eval = eval;
        this.progress = progressBar != null ? progressBar.clone() : mi.get(progress);
        this.progressSlot = progress;
        this.playerInvClickable = playerInvClickable;

        this.items = mi;
        this.clickHandlers = new HashMap<>();

        if (eval != null) {
            eval.doInit();
        }

        for (int i = 0; i < 54; i++) {
            ItemStack item = items.get(i);
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

    public String getId() {
        return id;
    }

    public CustomMenu setSize(int size) {
        if (size == MenuReader.NOT_SET) {
            this.size = MenuReader.NOT_SET;
            return this;
        }

        if (size > 54 || size < 0) {
            throw new IllegalArgumentException("Size must be between 0 and 54");
        }
        if (size % 9 != 0) {
            throw new IllegalArgumentException("Size must be a multiple of 9");
        }

        this.size = size;
        return this;
    }

    public void addItem(int i, ItemStack item, ChestMenu.MenuClickHandler onClick) {
        items.put(i, item);
        clickHandlers.put(i, onClick);
    }

    public void addItem(int i, ItemStack item) {
        items.put(i, item);
    }

    private ChestMenu.MenuClickHandler getClickHandler(int slot) {
        return clickHandlers.getOrDefault(slot, (player, i, itemStack, clickAction) -> true);
    }

    @Nullable public ItemStack getProgressBarItem() {
        return progress;
    }

    public String getID() {
        return id;
    }

    private void cloneFromPresetInventory(BlockMenuPreset preset) {
        preset.getContents();
        Inventory inventory =
                Bukkit.createInventory(null, preset.toInventory().getSize(), CMIChatColor.translate(title));

        for (int i = 0; i < preset.toInventory().getSize(); i++) {
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

    public void apply(BlockMenuPreset preset) {
        preset.setPlayerInventoryClickable(playerInvClickable);

        if (size != MenuReader.NOT_SET) {
            preset.setSize(size);
        }

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
