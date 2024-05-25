package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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

@SuppressWarnings("deprecation")
public class SingleItemRecipeGuide implements Listener {
    private static final NamespacedKey RECIPE_KEY = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "rsc_recipe");
    private static final NamespacedKey RECIPE_INDEX_KEY =
            new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "rsc_recipe_index");

    public SingleItemRecipeGuide() {
        Bukkit.getPluginManager().registerEvents(this, RykenSlimefunCustomizer.INSTANCE);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        ItemStack item = e.getCurrentItem();
        if (isTaggedItem(item)) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            Integer index = pdc.get(RECIPE_INDEX_KEY, PersistentDataType.INTEGER);
            if (index != null && index >= 0) {
                ItemStack sfItem = inv.getItem(16);
                if (sfItem != null) {
                    SlimefunItem sfItemObj = SlimefunItem.getByItem(sfItem);
                    if (sfItemObj != null) {
                        ChestMenu menu = createGUI(p, sfItemObj, index);
                        if (menu != null) {
                            menu.open(p);
                        }
                    }
                }
            }
        }
    }

    private static boolean isTaggedItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getKeys().contains(RECIPE_KEY) && pdc.getKeys().contains(RECIPE_INDEX_KEY);
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
        private final AsyncChanceRecipeTask recipeTask = new AsyncChanceRecipeTask();

        public RecipeMenu(AContainer item, Player p, int index) {
            super(Slimefun.getLocalization().getMessage(p, "guide.title.main"));

            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            setEmptySlotsClickable(false);
            setPlayerInventoryClickable(false);

            boolean defaultRecipeGUI = true;

            int progressSlot = 31;
            ItemStack progressBar = item.getProgressBar();

            int[] inputSlots = item.getInputSlots();
            int[] outputSlots = item.getOutputSlots();

            if (item instanceof CustomRecipeMachine crm) {
                CustomMenu menu = crm.getMenu();
                if (menu != null) {
                    defaultRecipeGUI = false;
                    progressSlot = menu.getProgressSlot();
                    if (menu.getProgressBarItem() != null) {
                        progressBar = menu.getProgressBarItem();
                    }
                }
            } else {
                inputSlots = new int[] {28, 29};
                outputSlots = new int[] {33, 34};
            }

            BlockMenuPreset preset = Slimefun.getRegistry().getMenuPresets().get(item.getId());

            if (preset == null) {
                return;
            }

            if (!defaultRecipeGUI) {
                for (int slot : preset.getPresetSlots()) {
                    ItemStack itemStack = preset.getItemInSlot(slot);
                    if (itemStack != null) {
                        addItem(slot, itemStack, (pl, s, is, action) -> false);
                    }
                }
            }

            if (defaultRecipeGUI) {
                int[] backgroundSlots = {
                    1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 22, 31, 40, 45, 46, 47, 48, 49, 50, 51, 52,
                    53
                };
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

                profile.ifPresent(prof -> addItem(
                        0,
                        ChestMenuUtils.getBackButton(p, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单"),
                        (pl, s, is, action) -> {
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
            ItemStack[] input = recipe.getInput();
            for (int i = 0; i < input.length; i++) {
                ItemStack inputItem = input[i];
                if (inputItem != null) {
                    addItem(inputSlots[i], inputItem, (pl, s, is, action) -> false);
                }
            }

            if (recipe instanceof RecipeMachineRecipe rmr) {
                int outputSlot = outputSlots[0];
                List<ItemStack> inputs = List.of(rmr.getInput());
                ItemStack[] outputs = recipe.getOutput();
                if (rmr.isChooseOneIfHas()) {
                    List<ItemStack> taggedChanceOutputs = new ArrayList<>();
                    for (int i = 0; i < outputs.length; i++) {
                        Integer chance = rmr.getChances().get(i);
                        ItemStack output = outputs[i];
                        if (chance != null && chance > 0 && output != null) {
                            taggedChanceOutputs.add(tagOutputChance(output, chance));
                        }
                    }

                    recipeTask.add(outputSlot, taggedChanceOutputs);
                    addMenuClickHandler(outputSlot, (pl, s, is, action) -> false);
                } else {
                    List<Integer> chances = rmr.getChances();

                    for (int i = 0; i < outputs.length; i++) {
                        int chance = chances.get(i);
                        ItemStack originalOutput = outputs[i];
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

            int seconds = recipe.getTicks() / 2;

            String rawName = "&e制作时间: &b" + seconds + "&es";

            if (seconds > 60) {
                rawName = rawName.concat("("+formatSeconds(seconds)+"&e)");
            }

            progressBar = new CustomItemStack(progressBar, rawName);

            addItem(progressSlot, progressBar, (pl, s, is, action) -> false);
        }

        @Override
        public void open(Player... players) {
            super.open(players);

            if (!recipeTask.isEmpty()) {
                recipeTask.start(getInventory());
            }
        }

        private ItemStack tagOutputChance(ItemStack item, int chance) {
            item = item.clone();
            CommonUtils.addLore(item, true, CMIChatColor.translate("&a有&b " + chance + "% &a的概率产出"));
            return item;
        }
    }

    public static String formatSeconds(int seconds) {
        if (seconds < 60) {
            return "&b" + seconds + "&es";
        } else if (seconds > 60 && seconds < 3600) {
            int m = seconds / 60;
            int s = seconds % 60;
            return "&b" + m + "&emin" + (s != 0 ? "&b" + s + "&es" : "");
        } else {
            int h = seconds / 3600;
            int m = (seconds % 3600) / 60;
            int s = (seconds % 3600) % 60;
            return "&b" + h + "&eh" + (m != 0 ? "&b" + m + "&emin" : "" ) + (s != 0 ? "&b" + s + "&es" : "");
        }
    }
}
