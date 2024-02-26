package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomGenerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class GeneratorReader extends YamlReader<CustomGenerator> {
    public GeneratorReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public CustomGenerator readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载发电机"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "错误的配方类型" + recipeType + "!", recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(s));

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");

        ConfigurationSection fuelsSection = section.getConfigurationSection("fuels");
        List<MachineFuel> fuels = readFuels(s, fuelsSection, addon);
        int capacity = section.getInt("capacity", 0);
        int production = section.getInt("production");

        if (production < 1) {
            ExceptionHandler.handleError("无法加载发电机"+s+": 产量不能小于1");
            return null;
        }

        return new CustomGenerator(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, menu, capacity, input, output, production, fuels);
    }

    private List<MachineFuel> readFuels(String id, ConfigurationSection section, ProjectAddon addon) {
        List<MachineFuel> fuels = new ArrayList<>();

        if (section == null) return fuels;

        for (String key : section.getKeys(false)) {
            ConfigurationSection section1 = section.getConfigurationSection(key);
            if (section1 == null) continue;
            ConfigurationSection item = section1.getConfigurationSection("item");
            ItemStack stack = CommonUtils.readItem(item, true, addon);
            if (stack == null) {
                ExceptionHandler.handleError("无法在发电机"+id+"中加载燃料"+key+": 物品为空或格式错误，跳过加载");
                continue;
            }
            int seconds = section1.getInt("seconds");

            if (seconds < 1) {
                ExceptionHandler.handleError("无法在发电机"+id+"中加载燃料"+key+": 秒数小于0，跳过加载");
                continue;
            }

            ItemStack output = null;
            if (section1.contains("output")) {
                ConfigurationSection outputSet = section1.getConfigurationSection("output");
                output = CommonUtils.readItem(outputSet, true, addon);
                if (output == null) {
                    ExceptionHandler.handleError("无法在发电机"+id+"中读取燃料"+key+"的输出: 物品为空或格式错误，已转为空");
                }
            }

            MachineFuel fuel = new MachineFuel(seconds, stack, output);
            fuels.add(fuel);
        }
        return fuels;
    }
}
