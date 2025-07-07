package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMaterialGenerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class MaterialGeneratorReader extends YamlReader<CustomMaterialGenerator> {
    public MaterialGeneratorReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomMaterialGenerator readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = addon.getId(s, section.getString("id_alias"));

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        Pair<RecipeType, ItemStack[]> recipePair = getRecipe(section, addon);
        RecipeType rt = recipePair.getFirstValue();
        ItemStack[] recipe = recipePair.getSecondValue();

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(id));
        if (menu == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载材料生成器" + s + "时遇到了问题: " + "对应菜单不存在");
            return null;
        }

        List<Integer> output = section.getIntegerList("output");
        int capacity = section.getInt("capacity", 0);
        ConfigurationSection outputItems = section.getConfigurationSection("outputs");
        ItemStack[] out = CommonUtils.readRecipe(outputItems, addon, output.size());
        List<Integer> chances = new ArrayList<>();
        boolean chooseOne = section.getBoolean("chooseOne", false);

        ItemStack[] out2 = CommonUtils.removeNulls(out);

        if (out2.length == 0) {
            ConfigurationSection outputItem = section.getConfigurationSection("outputItem");
            ItemStack outItem = CommonUtils.readItem(outputItem, true, addon);
            if (outItem == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载材料生成器" + s + "时遇到了问题: " + "输出物品为空或格式错误导致无法加载");
                return null;
            } else {
                out = new ItemStack[] {outItem};
                chances = List.of(outputItem.getInt("chance", 100));
            }
        } else if (out.length > 1 && outputItems != null) {
            for (String key : outputItems.getKeys(false)) {
                ConfigurationSection chanceItem = outputItems.getConfigurationSection(key);
                if (CommonUtils.readItem(chanceItem, true, addon) == null) {
                    continue;
                }
                chances.add(chanceItem.getInt("chance", 100));
            }
        }

        out = CommonUtils.removeNulls(out);

        int tickRate = section.getInt("tickRate");
        if (tickRate < 1) {
            ExceptionHandler.handleError(
                    "在附属" + addon.getAddonId() + "中加载材料生成器" + s + "时遇到了问题: " + "tickRate未设置或不能小于1");
            return null;
        }

        int per = section.getInt("per");
        if (per < 1) {
            ExceptionHandler.handleError(
                    "在附属" + addon.getAddonId() + "中加载材料生成器" + s + "时遇到了问题: " + "单次生成能量花费未设置或不能小于1");
            return null;
        }

        int status = -1;
        if (section.contains("status")) {
            status = section.getInt("status");
        }

        CustomMaterialGenerator cmg = new CustomMaterialGenerator(
                group.getSecondValue(),
                slimefunItemStack,
                rt,
                recipe,
                capacity,
                output,
                status,
                tickRate,
                Arrays.asList(out),
                menu,
                per,
                chances,
                chooseOne);

        menu.addMenuClickHandler(status, ChestMenuUtils.getEmptyClickHandler());

        return cmg;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载材料生成器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }
}
