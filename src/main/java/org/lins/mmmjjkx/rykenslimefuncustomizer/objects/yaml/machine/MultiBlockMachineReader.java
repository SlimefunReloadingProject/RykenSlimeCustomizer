package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMultiBlockMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class MultiBlockMachineReader extends YamlReader<CustomMultiBlockMachine> {
    public MultiBlockMachineReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomMultiBlockMachine readEach(String s) {
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

        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);

        ConfigurationSection recipesSection = section.getConfigurationSection("recipes");

        int workSlot = section.getInt("work");
        if (workSlot < 1) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载多方块机器" + s + "时遇到了问题: " + "没有设置工作槽");
            return null;
        }

        if (recipe == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载多方块机器" + s + "时遇到了问题: " + "放置配方为空");
            return null;
        }

        boolean hasDispenser = false;

        for (ItemStack is : recipe) {
            if (is != null) {
                if (is.getType() == Material.DISPENSER) {
                    hasDispenser = true;
                    break;
                }
            }
        }

        if (!hasDispenser) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载多方块机器" + s + "时遇到了问题: " + "放置配方里没有发射器");
            return null;
        }

        if (recipe[workSlot - 1] == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载多方块机器" + s + "时遇到了问题: " + "对应工作方块不存在");
            return null;
        }

        Map<ItemStack[], ItemStack> recipes = readRecipes(s, recipesSection, addon);
        SoundEffect sound = null;
        if (section.contains("sound")) {
            String soundString = section.getString("sound");
            Pair<ExceptionHandler.HandleResult, SoundEffect> soundEffectPair = ExceptionHandler.handleEnumValueOf(
                    "在附属" + addon.getAddonId() + "中加载多方块机器" + s + "无法获取声音类型" + soundString,
                    SoundEffect.class,
                    soundString);
            ExceptionHandler.HandleResult result1 = soundEffectPair.getFirstValue();
            if (result1 != ExceptionHandler.HandleResult.FAILED && soundEffectPair.getSecondValue() != null) {
                sound = soundEffectPair.getSecondValue();
            }
        }

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning(
                        "在附属" + addon.getAddonId() + "中加载多方块机器" + s + "时遇到了问题: " + "找不到脚本文件 " + file.getName());
            } else {
                eval = new JavaScriptEval(file, addon);
            }
        }

        return new CustomMultiBlockMachine(
                group.getSecondValue(), slimefunItemStack, recipe, recipes, workSlot, sound, eval);
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载多方块机器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }

    private Map<ItemStack[], ItemStack> readRecipes(String s, ConfigurationSection section, ProjectAddon addon) {
        Map<ItemStack[], ItemStack> map = new HashMap<>();
        if (section == null) return map;

        for (String key : section.getKeys(false)) {
            ConfigurationSection recipe = section.getConfigurationSection(key);
            if (recipe == null) continue;
            ConfigurationSection inputs = recipe.getConfigurationSection("input");
            if (inputs == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载多方块机器" + s + "的工作配方" + key + "时遇到了问题: " + "没有输入物品");
                continue;
            }
            ItemStack[] input = CommonUtils.readRecipe(inputs, addon);
            if (input == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载多方块机器" + s + "的工作配方" + key + "时遇到了问题: " + "输入物品为空或格式错误");
                continue;
            }
            ConfigurationSection outputs = recipe.getConfigurationSection("output");
            if (outputs == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载多方块机器" + s + "的工作配方" + key + "时遇到了问题: " + "没有输出物品");
                continue;
            }
            ItemStack output = CommonUtils.readItem(outputs, true, addon);
            if (output == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载多方块机器" + s + "的工作配方" + key + "时遇到了问题: " + "输出物品为空或格式错误");
                continue;
            }
            map.put(input, output);
        }
        return map;
    }
}
