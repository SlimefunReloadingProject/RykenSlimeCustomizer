package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineTemplate;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun.AsyncChanceRecipeTask;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

@SuppressWarnings("deprecation")
public class SingleItemRecipeGuideListener implements Listener {
    private static final NamespacedKey RECIPE_KEY = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "rsc_recipe");
    private static final NamespacedKey RECIPE_INDEX_KEY =
            new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "rsc_recipe_index");
    private static final NamespacedKey RECIPE_TEMPLATE_INDEX_KEY =
            new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "rsc_recipe_template_index");

    public SingleItemRecipeGuideListener() {
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
                        ChestMenu menu = createGUI(p, sfItemObj, item.getItemMeta());
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

    public static ItemStack tagItemTemplateRecipe(ItemStack item, int templateIndex, int recipeIndex) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(RECIPE_KEY, PersistentDataType.INTEGER, 2);
        pdc.set(RECIPE_INDEX_KEY, PersistentDataType.INTEGER, recipeIndex);
        pdc.set(RECIPE_TEMPLATE_INDEX_KEY, PersistentDataType.INTEGER, templateIndex);
        item.setItemMeta(meta);
        return item;
    }

    private ChestMenu createGUI(Player p, SlimefunItem machine, PersistentDataHolder holder) {
        int type = PersistentDataAPI.getInt(holder, RECIPE_KEY, 1);
        if (machine instanceof AContainer ac && type == 1) {
            int index = PersistentDataAPI.getInt(holder, RECIPE_INDEX_KEY, 0);
            return new RecipeMenu(ac, p, index);
        } else if (machine instanceof CustomTemplateMachine ctm && type == 2) {
            int templateIndex = PersistentDataAPI.getInt(holder, RECIPE_TEMPLATE_INDEX_KEY, 0);
            int recipeIndex = PersistentDataAPI.getInt(holder, RECIPE_INDEX_KEY, 0);
            return new TemplateRecipeMenu(ctm, p, templateIndex, recipeIndex);
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
                        ChestMenuUtils.getBackButton(
                                p,
                                "",
                                "&fLeft click: &7back to previous page",
                                "&fShift + left click: &7back to the main menu"),
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

            if (recipe instanceof CustomMachineRecipe rmr) {
                int outputSlot = outputSlots[0];
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
                                        chanceOutput,
                                        true,
                                        CMIChatColor.translate(
                                                "&aThere's a &b " + chance + "% &aprobability of producing"));
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
            String rawName = "&eProduction time: &b" + seconds + "&es";

            if (seconds > 60) {
                rawName = rawName.concat("(" + CommonUtils.formatSeconds(seconds) + "&e)");
            }

            progressBar = new CustomItemStack(progressBar, rawName);

            addItem(progressSlot, progressBar, (pl, s, is, action) -> false);
        }

        @Override
        public void open(Player... players) {
            super.open(players);

            if (!recipeTask.isEmpty()) {
                recipeTask.start(toInventory());
            }
        }

        private ItemStack tagOutputChance(ItemStack item, int chance) {
            item = item.clone();
            CommonUtils.addLore(
                    item, true, CMIChatColor.translate("&aThere's a &b " + chance + "% &aprobability of producing"));
            return item;
        }
    }

    private static class TemplateRecipeMenu extends ChestMenu {
        private final AsyncChanceRecipeTask recipeTask = new AsyncChanceRecipeTask();

        public TemplateRecipeMenu(CustomTemplateMachine ctm, Player p, int templateIndex, int recipeIndex) {
            super(Slimefun.getLocalization().getMessage(p, "guide.title.main"));

            setEmptySlotsClickable(false);
            setPlayerInventoryClickable(false);

            CustomMenu menu = ctm.getMenu();
            for (int i = 0; i < 54; i++) {
                ItemStack item = menu.getItems().get(i);
                if (item != null) {
                    addItem(i, item, (pl, s, is, action) -> false);
                }
            }

            int maxSlot = menu.getSize() - 1;
            if (maxSlot > 0) {
                addItem(maxSlot, new ItemStack(Material.AIR), ((player, i, itemStack, clickAction) -> false));
            }

            int[] inputSlots = ctm.getInputSlots();
            int[] outputSlots = ctm.getOutputSlots();

            int progressSlot = menu.getProgressSlot();
            ItemStack progressBar = menu.getProgressBarItem();

            int templateSlot = ctm.getTemplateSlot();

            MachineTemplate template = ctm.getTemplates().get(templateIndex);
            if (template == null) return;

            CustomMachineRecipe recipe = template.recipes().get(recipeIndex);
            if (recipe == null) return;

            int seconds = recipe.getTicks() / 2;
            ItemStack templateItem = template.template().clone();
            CommonUtils.addLore(templateItem, true, "&d&l&o*Template item doesn't consuming*");
            addItem(templateSlot, templateItem, (pl, s, is, action) -> false);

            if (inputSlots.length != 0 && recipe.getInput().length != 0) {
                for (int i = 0; i < inputSlots.length; i++) {
                    if (i >= recipe.getInput().length) {
                        break;
                    }

                    ItemStack inputItem = recipe.getInput()[i];
                    if (inputItem != null) {
                        addItem(inputSlots[i], inputItem.clone(), (pl, s, is, action) -> false);
                    }
                }
            }

            ItemStack[] outputs = recipe.getOutput();
            if (recipe.isChooseOneIfHas()) {
                List<ItemStack> taggedChanceOutputs = new ArrayList<>();
                for (int i = 0; i < outputs.length; i++) {
                    Integer chance = recipe.getChances().get(i);
                    ItemStack output = outputs[i];
                    if (chance != null && chance > 0 && output != null) {
                        taggedChanceOutputs.add(tagOutputChance(output, chance));
                    }
                }

                recipeTask.add(outputSlots[0], taggedChanceOutputs);
                addMenuClickHandler(outputSlots[0], (pl, s, is, action) -> false);
            } else {
                List<Integer> chances = recipe.getChances();

                for (int i = 0; i < outputSlots.length; i++) {
                    if (i >= outputs.length) {
                        return;
                    }

                    int chance = chances.get(i);
                    ItemStack originalOutput = outputs[i];
                    if (originalOutput != null) {
                        ItemStack chanceOutput = originalOutput.clone();
                        if (chance < 100) {
                            CommonUtils.addLore(
                                    chanceOutput,
                                    true,
                                    CMIChatColor.translate(
                                            "&aThere's a &b " + chance + "% &aprobability of producing"));
                        }

                        if (chance > 0) {
                            addItem(outputSlots[i], chanceOutput, (pl, s, is, action) -> false);
                        }
                    }
                }
            }

            if (progressSlot >= 0 && progressBar != null) {
                String rawName = "&eProduction time: &b" + seconds + "&es";

                if (seconds > 60) {
                    rawName = rawName.concat("(" + CommonUtils.formatSeconds(seconds) + "&e)");
                }

                progressBar = new CustomItemStack(progressBar, rawName);

                addItem(progressSlot, progressBar, (pl, s, is, action) -> false);
            }
        }

        @Override
        public void open(Player... players) {
            super.open(players);

            if (!recipeTask.isEmpty()) {
                recipeTask.start(toInventory());
            }
        }

        private ItemStack tagOutputChance(ItemStack item, int chance) {
            item = item.clone();
            CommonUtils.addLore(
                    item, true, CMIChatColor.translate("&aThere's a &b " + chance + "% &aprobability of producing"));
            return item;
        }
    }
}
