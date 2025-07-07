package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.LinkedOutput;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomWorkbench;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomLinkedMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

/*
 * RSC_EXAMPLE_WORKBENCH:
 *   item_group: RSC_EXAMPLE_NORMAL_GROUP
 *   item:
 *     material: GOLD_BLOCK
 *   input: [19, 20]
 *   output: [24, 25]
 *   click: 42
 *   capacity: 1000
 *   energyPerCraft: 1000
 *   hideAllRecipes: false
 *   script: your_script_name
 *   recipes:
 *     your_recipe_name:
 *       chooseOne: false
 *       forDisplay: false
 *       hide: false
 *       input:
 *         1:
 *           slot: 19
 *           material: IRON_INGOT
 *           amount: 64
 *         2:
 *           slot: 20
 *           material: IRON_INGOT
 *           amount: 64
 *       output:
 *         # free output
 *         1:
 *           material: GOLD_INGOT
 *         # linked output
 *         2:
 *           slot: 25
 *           material: OBSIDIAN
 *           amount: 7
 *         # chanced output
 *         3:
 *           material: DIAMOND
 *           chance: 50
 *
 */
public class WorkbenchReader extends YamlReader<CustomWorkbench> {
    public WorkbenchReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomWorkbench readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = addon.getId(s, section.getString("id_alias"));

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        Pair<RecipeType, ItemStack[]> recipePair = getRecipe(section, addon);
        RecipeType rt = recipePair.getFirstValue();
        ItemStack[] recipe = recipePair.getSecondValue();

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(id));
        if (menu == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "未找到菜单");
            return null;
        }

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");

        if (input.isEmpty()) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "输入槽为空");
            return null;
        }

        if (output.isEmpty()) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "输出槽为空");
            return null;
        }

        ConfigurationSection recipes = section.getConfigurationSection("recipes");

        int capacity = section.getInt("capacity");

        if (capacity < 0) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "能源容量小于0");
            return null;
        }

        int energy = section.getInt("energyPerCraft");

        if (energy <= 0) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "合成一次的消耗能量未设置或小于等于0");
            return null;
        }

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning(
                        "在附属" + addon.getAddonId() + "中加载机器" + s + "时遇到了问题: " + "找不到脚本文件 " + file.getName());
            } else {
                eval = new JavaScriptEval(file, addon);
            }
        }

        int click = section.getInt("click", -1);
        if (click == -1) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "点击槽位未设置");
            return null;
        }

        if (click < 0 || click > 53) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "点击槽位超出范围");
            return null;
        }

        boolean hideAllRecipes = section.getBoolean("hideAllRecipes", false);

        List<CustomLinkedMachineRecipe> mr = readRecipes(s, input.size(), output.size(), recipes, addon);

        return new CustomWorkbench(
                group.getSecondValue(),
                slimefunItemStack,
                rt,
                recipe,
                input.stream().mapToInt(i -> i).toArray(),
                output.stream().mapToInt(i -> i).toArray(),
                mr,
                energy,
                capacity,
                menu,
                hideAllRecipes,
                click,
                eval);
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "时遇到了问题: " + "物品为空或格式错误");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }

    private List<CustomLinkedMachineRecipe> readRecipes(
            String s, int inputSize, int outputSize, ConfigurationSection section, ProjectAddon addon) {
        List<CustomLinkedMachineRecipe> list = new ArrayList<>();
        if (section == null) {
            return list;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection recipes = section.getConfigurationSection(key);
            if (recipes == null) continue;
            ConfigurationSection inputs = recipes.getConfigurationSection("input");
            if (inputs == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载工作台" + s + "的工作配方" + key + "时遇到了问题: " + "没有输入物品");
                continue;
            }

            ConfigurationSection outputs = recipes.getConfigurationSection("output");
            if (outputs == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载工作台" + s + "的工作配方" + key + "时遇到了问题: " + "没有输出物品");
                continue;
            }

            List<ItemStack> freeOutput = new ArrayList<>();
            List<Integer> freeChances = new ArrayList<>();

            Map<Integer, ItemStack> linkedOutput = new HashMap<>();
            Map<Integer, Integer> linkedChances = new HashMap<>();

            for (int i = 0; i < outputSize; i++) {
                ConfigurationSection section1 = outputs.getConfigurationSection(String.valueOf(i + 1));
                var item = CommonUtils.readItem(section1, true, addon);
                if (item != null && item.getType() != Material.AIR) {
                    int chance = section1.getInt("chance", 100);

                    if (chance < 1) {
                        ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载工作台" + s + "的工作配方" + key
                                + "时遇到了问题: " + "概率不应该小于1，已转为1");
                        chance = 1;
                    }

                    int slot = section1.getInt("slot", -1);
                    if (slot == -1) {
                        freeOutput.add(item);
                        freeChances.add(chance);
                    } else {
                        linkedOutput.put(slot, item);
                        linkedChances.put(slot, chance);
                    }
                }
            }

            boolean chooseOne = recipes.getBoolean("chooseOne", false);
            boolean forDisplay = recipes.getBoolean("forDisplay", false);
            boolean hide = recipes.getBoolean("hide", false);

            Set<Integer> noConsumes = new HashSet<>();
            Map<Integer, ItemStack> finalInput = new HashMap<>();
            for (int i = 0; i < inputSize; i++) {
                ConfigurationSection section1 = inputs.getConfigurationSection(String.valueOf(i + 1));
                if (section1 == null) {
                    continue;
                }

                ItemStack itemStack = CommonUtils.readItem(section1, true, addon);
                if (itemStack == null) {
                    continue;
                }

                int slot = section1.getInt("slot", -1);
                if (slot == -1) {
                    ExceptionHandler.handleError(
                            "在附属" + addon.getAddonId() + "中加载工作台" + s + "的工作配方" + key + "时遇到了问题: " + "输入槽位不能为空");
                    continue;
                }

                if (slot < 0 || slot > 53) {
                    ExceptionHandler.handleError(
                            "在附属" + addon.getAddonId() + "中加载工作台" + s + "的工作配方" + key + "时遇到了问题: " + "输入槽位超出范围");
                    continue;
                }

                finalInput.put(slot, itemStack);

                boolean noConsume1 = section1.getBoolean("noConsume", false);
                if (noConsume1) {
                    noConsumes.add(slot);
                }
            }

            int[] array = new int[freeChances.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = i;
            }

            list.add(new CustomLinkedMachineRecipe(
                    0,
                    finalInput,
                    new LinkedOutput(freeOutput.toArray(new ItemStack[0]), linkedOutput, array, linkedChances),
                    chooseOne,
                    forDisplay,
                    hide,
                    noConsumes));
        }
        return list;
    }
}
