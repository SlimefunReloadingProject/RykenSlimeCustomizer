package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMaterialGenerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class MaterialGeneratorReader extends YamlReader<CustomMaterialGenerator> {
    public MaterialGeneratorReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CustomMaterialGenerator> readAll(ProjectAddon addon) {
        List<CustomMaterialGenerator> generators = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var ig = readEach(key, addon);
            if (ig != null) {
                generators.add(ig);
            }
        }
        return generators;
    }

    @Override
    public CustomMaterialGenerator readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载材料生成器"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"));
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.handleField(
                "错误的配方类型" + recipeType + "!", "", RecipeType.class, recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(s));
        if (menu == null) {
            ExceptionHandler.handleError("无法加载发电机"+s+": 对应菜单不存在");
            return null;
        }

        List<Integer> output = section.getIntegerList("output");

        int capacity = section.getInt("capacity", 0);
        ConfigurationSection outputItem = section.getConfigurationSection("outputItem");
        ItemStack out = CommonUtils.readItem(outputItem, true);
        if (out == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载材料生成器"+s+": 输出物品为空或格式错误导致无法加载");
            return null;
        }

        int tickRate = section.getInt("tickRate");
        if (tickRate < 1) {
            ExceptionHandler.handleError("无法加载材料生成器"+s+": tickRate未设置或不能小于1");
            return null;
        }

        int per = section.getInt("per");
        if (per < 1) {
            ExceptionHandler.handleError("无法加载材料生成器"+s+": 单次生成能量花费未设置或不能小于1");
            return null;
        }

        int status = -1;
        if (section.contains("status")) {
            status = section.getInt("status");
        }

        return new CustomMaterialGenerator(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, capacity, output, status, tickRate, out, per);
    }
}
