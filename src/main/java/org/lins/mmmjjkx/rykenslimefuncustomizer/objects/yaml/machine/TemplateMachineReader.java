package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineTemplate;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class TemplateMachineReader extends YamlReader<CustomTemplateMachine> {
    public TemplateMachineReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomTemplateMachine readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        String id = addon.getId(s, section.getString("id_alias"));
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        SlimefunItemStack sfis = getPreloadItem(id);
        if (sfis == null) return null;

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "错误的配方类型" + recipeType + "!", recipeType);
        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        boolean fasterIfMoreTemplates = section.getBoolean("fasterIfMoreTemplates", false);
        boolean moreOutputIfMoreTemplates = section.getBoolean("moreOutputIfMoreTemplates", false);

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");

        if (output.isEmpty()) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "输出槽为空");
            return null;
        }

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(id));

        if (menu == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "未找到菜单");
            return null;
        }

        if (menu.getProgressSlot() < 0) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "进度槽位未设置");
            return null;
        }

        List<MachineTemplate> templates =
                readTemplates(id, input.size(), output.size(), section.getConfigurationSection("recipes"), addon);

        int templateSlot = section.getInt("templateSlot");

        if (templateSlot < 0 || templateSlot >= 54) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "模板槽位不合法");
            return null;
        }

        int capacity = section.getInt("capacity");

        if (capacity < 0) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "能源容量小于0");
            return null;
        }

        int energy = section.getInt("consumption");

        if (energy <= 0) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "消耗能量未设置或小于等于0");
            return null;
        }

        boolean hideAllRecipes = section.getBoolean("hideAllRecipes", false);

        return new CustomTemplateMachine(
                group.getSecondValue(),
                sfis,
                rt.getSecondValue(),
                recipe,
                menu,
                input,
                output,
                templateSlot,
                templates,
                energy,
                capacity,
                fasterIfMoreTemplates,
                moreOutputIfMoreTemplates,
                hideAllRecipes);
    }

    private List<MachineTemplate> readTemplates(
            String s, int inputSize, int outputSize, ConfigurationSection section, ProjectAddon addon) {
        List<MachineTemplate> list = new ArrayList<>();
        if (section == null) {
            return list;
        }

        for (String key : section.getKeys(false)) {
            SlimefunItemStack item = getPreloadItem(key) == null
                    ? SlimefunItem.getById(key) == null
                            ? null
                            : ((SlimefunItemStack)
                                    SlimefunItem.getById(key).getItem().clone())
                    : getPreloadItem(key);

            if (item == null) {
                ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: 无法找到作为模板的物品" + key);
                continue;
            }

            List<CustomMachineRecipe> recipes =
                    readRecipes(s, inputSize, outputSize, section.getConfigurationSection(key), addon);
            list.add(new MachineTemplate(item, recipes));
        }

        return list;
    }

    private List<CustomMachineRecipe> readRecipes(
            String s, int inputSize, int outputSize, ConfigurationSection section, ProjectAddon addon) {
        List<CustomMachineRecipe> list = new ArrayList<>();
        if (section == null) {
            return list;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection recipes = section.getConfigurationSection(key);
            if (recipes == null) continue;
            int seconds = recipes.getInt("seconds");
            if (seconds < 0) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载模板机器" + s + "的工作配方" + key + "时遇到了问题: " + "间隔时间未设置或不能小于0");
                continue;
            }

            ConfigurationSection inputs = recipes.getConfigurationSection("input");
            ItemStack[] input = CommonUtils.readRecipe(inputs, addon, inputSize);
            ConfigurationSection outputs = recipes.getConfigurationSection("output");
            if (outputs == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载模板机器" + s + "的工作配方" + key + "时遇到了问题: " + "没有输出物品");
                continue;
            }

            List<Integer> chances = new ArrayList<>();

            ItemStack[] output = new ItemStack[outputSize];
            for (int i = 0; i < outputSize; i++) {
                ConfigurationSection section1 = outputs.getConfigurationSection(String.valueOf(i + 1));
                var item = CommonUtils.readItem(section1, true, addon);
                if (item != null) {
                    int chance = section1.getInt("chance", 100);

                    if (chance < 1) {
                        ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "的工作配方" + key
                                + "时遇到了问题: " + "概率不应该小于1，已转为1");
                        chance = 1;
                    }

                    output[i] = item;
                    chances.add(chance);
                }
            }

            boolean chooseOne = recipes.getBoolean("chooseOne", false);
            boolean forDisplay = recipes.getBoolean("forDisplay", false);
            boolean hide = recipes.getBoolean("hide", false);

            input = CommonUtils.removeNulls(input);
            output = CommonUtils.removeNulls(output);

            list.add(new CustomMachineRecipe(seconds, input, output, chances, chooseOne, forDisplay, hide));
        }
        return list;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }
}
