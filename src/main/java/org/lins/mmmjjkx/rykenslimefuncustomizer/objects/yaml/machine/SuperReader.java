package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ReflectionUtils;

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
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            ExceptionHandler.handleError("未找到基类", e);
            return null;
        }

        if (!SlimefunItem.class.isAssignableFrom(clazz)) {
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
        Object[] args =
                section.getList("args") == null ? null : section.getList("args", new ArrayList<>()).toArray();
        List<Object> argTemplate =
                (List<Object>) section.getList("arg_template", List.of("group", "item", "recipe_type", "recipe"));
        Object[] originArgs = argTemplate.stream()
                .map(x -> {
                    if (x.equals("group")) return group.getSecondValue();
                    if (x.equals("item")) return slimefunItemStack;
                    if (x.equals("recipe_type")) return rt.getSecondValue();
                    if (x.equals("recipe")) return recipe;
                    return x;
                })
                .filter(Objects::nonNull)
                .toArray();
        SlimefunItem instance;
        try {
            if (args == null) instance = ctor.newInstance(originArgs);
            else {
                List<Object> newArgs = new ArrayList<>(List.of(originArgs));
                newArgs.addAll(List.of(args));
                instance = ctor.newInstance(newArgs.toArray());
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            ExceptionHandler.handleError("无法创建类", e);
            return null;
        }

        if (section.contains("method")) {
            ConfigurationSection methodArray = section.getConfigurationSection("method");
            for (String methodName : methodArray.getKeys(false)) {
                Object[] args1 = methodArray.getList(methodName).toArray();
                Method method = null;
                try {
                    method = clazz.getDeclaredMethod(
                            methodName,
                            Arrays.stream(args1).map(Object::getClass).toArray(Class<?>[]::new));
                } catch (NoSuchMethodException e) {
                    ExceptionHandler.handleError("没有找到方法", e);
                }
                if (method != null) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance, args1);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        ExceptionHandler.handleError("方法调用异常", e);
                    }
                }
            }
        }

        if (section.contains("field")) {
            ConfigurationSection fieldArray = section.getConfigurationSection("field");
            for (String fieldName : fieldArray.getKeys(false)) {
                try {
                    Field[] fields = ReflectionUtils.getAllFields(instance);
                    Field field = null;
                    for (Field f : fields) {
                        if (f.getName().equals(fieldName)) {
                            field = f;
                            break;
                        }
                    }
                    if (field == null) throw new NoSuchFieldException(fieldName);
                    if (Modifier.isStatic(field.getModifiers())) throw new IllegalAccessException(fieldName + "为static");
                    field.setAccessible(true);
                    Object object = fieldArray.getObject(fieldName, field.getType());
                    field.set(instance, object);
                } catch (Exception e) {
                    ExceptionHandler.handleError("属性修改异常", e);
                }
            }
        }

        if (group.getSecondValue() != null) {
            instance.setItemGroup(group.getSecondValue());
        }

        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }
}
