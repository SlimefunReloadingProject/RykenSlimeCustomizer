package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class SuperReader extends YamlReader<SlimefunItem> {
    public SuperReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public SlimefunItem readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);
        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt =
                ExceptionHandler.getRecipeType("错误的配方类型" + recipeType + "!", recipeType);
        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        String className = section.getString("class", "");
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            ExceptionHandler.handleError("未找到基类", e);
            return null;
        }

        if (!clazz.isAssignableFrom(SlimefunItem.class)) {
            ExceptionHandler.handleError("基类不是粘液物品");
            return null;
        }
        // a zero-based number
        int ctorIndex = section.getInt("ctor", 0);
        if (clazz.getConstructors().length < ctorIndex + 1) {
            ExceptionHandler.handleError("无效的构造函数");
            return null;
        }
        Constructor<? extends SlimefunItem> ctor =
                (Constructor<? extends SlimefunItem>) clazz.getConstructors()[ctorIndex];
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);
        Object[] args = section.getObject("args", Object[].class, null);
        try {
            if (args == null) return ctor.newInstance(group.getSecondValue(), slimefunItemStack, recipeType, recipe);
            List<Object> newArgs = Arrays.asList(group.getSecondValue(), slimefunItemStack, recipeType, recipe);
            newArgs.addAll(List.of(args));
            return ctor.newInstance(newArgs.toArray());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            ExceptionHandler.handleError("无法创建类", e);
            return null;
        }
    }
}
