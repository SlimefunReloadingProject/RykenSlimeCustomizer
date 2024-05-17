package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.Colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.RecipeMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun.AsyncChanceRecipeTask;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class SingleItemRecipeGuide implements Listener {
    private static final NamespacedKey RECIPE_KEY = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "rsc_recipe");
    private static final NamespacedKey RECIPE_INDEX_KEY = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "rsc_recipe_index");

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        InventoryView view = e.getView();
        Inventory inv = e.getInventory();

        if (view.getTitle().equals(Slimefun.getLocalization().getMessage(p, "guide.title.main"))) {
            e.setCancelled(true);

            ItemStack item = e.getCurrentItem();
            if (item != null && isTaggedItem(item)) {
                PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                Integer index = pdc.get(RECIPE_INDEX_KEY, PersistentDataType.INTEGER);
                if (index != null && index >= 0) {
                    ItemStack sfItem = inv.getItem(16);
                    SlimefunItem sfItemObj = SlimefunItem.getByItem(sfItem);
                    if (sfItem instanceof SlimefunItemStack sfis || sfItemObj != null) {
                        ChestMenu menu = createGUI(p, SlimefunItem.getByItem(sfItem), index);
                        if (menu != null) {
                            menu.open(p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if (inv.getHolder() instanceof RecipeMenu rm && !rm.defaultRecipeGUI) {
            PlayerProfile.find(p).ifPresent(profile -> profile.getGuideHistory().goBack(new SurvivalSlimefunGuide(false, false)));
        }
    }

    private static boolean isTaggedItem(ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getKeys().contains(RECIPE_KEY)
                && pdc.getKeys().contains(RECIPE_INDEX_KEY)
                && SlimefunItem.getByItem(item) != null;
    }

    public static ItemStack tagItemRecipe(ItemStack item, int index) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(RECIPE_KEY, PersistentDataType.INTEGER, 1);
        pdc.set(RECIPE_INDEX_KEY, PersistentDataType.INTEGER, index);
        item.setItemMeta(meta);
        return item;
    }

    private ChestMenu createGUI(Player p, SlimefunItem machine, int index) {
        if (machine instanceof AContainer ac) {
            return new RecipeMenu(ac, p, index);
        }
        return null;
    }

    private static class RecipeMenu extends ChestMenu {
        private final boolean defaultRecipeGUI;

        public RecipeMenu(AContainer item, Player p, int index) {
            super(Slimefun.getLocalization().getMessage(p, "guide.title.main"));
            boolean b;

            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            setEmptySlotsClickable(false);
            setPlayerInventoryClickable(false);

            b = true;

            int[] inputSlots = item.getInputSlots();
            int[] outputSlots = item.getOutputSlots();

            if (item instanceof CustomRecipeMachine crm) {
                CustomMenu menu = crm.getMenu();
                if (menu != null) {
                    b = false;
                }
            } else {
                inputSlots = new int[]{28, 29};
                outputSlots = new int[]{33, 34};
            }

            defaultRecipeGUI = b;
            BlockMenuPreset preset = Slimefun.getRegistry().getMenuPresets().get(item.getId());

            if (preset == null) {
                return;
            }

            if (!defaultRecipeGUI) {
                for (int i = 0; i < preset.getSize(); i++) {
                    ItemStack itemStack = preset.getItemInSlot(i);
                    if (itemStack != null) {
                        addItem(i, itemStack, (pl, s, is, action) -> false);
                    }
                }
            }

            if (defaultRecipeGUI) {
                int[] backgroundSlots = {1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 22, 31, 40, 45, 46, 47, 48, 49, 50, 51, 52, 53};
                for (int background : backgroundSlots) {
                    addItem(background, ChestMenuUtils.getBackground(), (pl, s, is, action) -> false);
                }

                int[] inputBorderSlots = {18, 19, 20, 21, 27, 30, 36, 37, 38, 39};
                for (int inputBackground : inputBorderSlots) {
                    addItem(inputBackground, ChestMenuUtils.getInputSlotTexture(), (pl, s, is, action) -> false);
                }

                int[] outputBorderSlots = {23, 24, 25, 26, 32, 35, 41, 42, 43, 44};
                for (int outputBackground : outputBorderSlots) {
                    addItem(outputBackground, ChestMenuUtils.getOutputSlotTexture(), (pl, s, is, action) -> false);
                }

                profile.ifPresent(prof -> addItem(0, ChestMenuUtils.getBackButton(p, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单"), (pl, s, is, action) -> {
                    SurvivalSlimefunGuide guide = new SurvivalSlimefunGuide(false, false);
                    GuideHistory history = prof.getGuideHistory();
                    if (action.isShiftClicked()) {
                        guide.openMainMenu(prof, history.getMainMenuPage());
                    } else {
                        history.goBack(guide);
                    }
                    return false;
                }));
            }

            List<MachineRecipe> recipes = item.getMachineRecipes();
            MachineRecipe recipe = recipes.get(index);
            for (int i = 0; i < inputSlots.length; i++) {
                ItemStack input = recipe.getInput()[i];
                if (input != null) {
                    addItem(inputSlots[i], input, (pl, s, is, action) -> false);
                }
            }

            AsyncChanceRecipeTask task = new AsyncChanceRecipeTask();

            if (recipe instanceof RecipeMachineRecipe rmr) {
                int outputSlot = outputSlots[0];
                List<ItemStack> inputs = List.of(rmr.getInput());
                if (rmr.isChooseOneIfHas()) {
                    task.add(outputSlot, inputs);
                } else {
                    List<Integer> chances = rmr.getChances();
                    for (int i = 0; i < outputSlots.length; i++) {
                        int chance = chances.get(i);
                        ItemStack originalOutput = recipe.getOutput()[i];
                        if (originalOutput != null) {
                            ItemStack chanceOutput = originalOutput.clone();
                            if (chance < 100) {
                                CommonUtils.addLore(
                                        chanceOutput, true, CMIChatColor.translate("&a有&b " + chance + "% &a的概率产出"));
                            }

                            if (chance > 0) {
                                addItem(outputSlots[i], chanceOutput, (pl, s, is, action) -> false);
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < outputSlots.length; i++) {
                    ItemStack output = recipe.getOutput()[i];
                    if (output != null) {
                        addItem(outputSlots[i], output, (pl, s, is, action) -> false);
                    }
                }
            }

            getContents();

            if (!task.isEmpty()) {
                task.start(getInventory());
            }
        }
    }
}
