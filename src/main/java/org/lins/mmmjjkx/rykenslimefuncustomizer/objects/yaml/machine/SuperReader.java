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
    public SuperReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public SlimefunItem readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = addon.getId(s, section.getString("id_alias"));

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        SlimefunItemStack sfis = getPreloadItem(id);
        if (sfis == null) return null;

        String igId = section.getString("item_group");

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "错误的配方类型" + recipeType + "!", recipeType);
        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        String className = section.getString("class", "");
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "未找到基类", e);
            return null;
        }

        if (!SlimefunItem.class.isAssignableFrom(clazz)) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "基类不是粘液物品");
            return null;
        }

        // a dangerous option to ignore accessibility
        // Author: balugaq
        boolean ignoreAccessible = section.getBoolean("ignore_accessible", false);

        if (ignoreAccessible) {
            ExceptionHandler.handleWarning(
                    "在附属" + addon.getAddonId() + "中加载继承物品" + s + "发现了 ignore_accessible 选项被启用，这可能会导致潜在的安全漏洞!");
        }
        // a zero-based number
        int constructorIndex = section.getInt("ctor", 0);
        if (clazz.getConstructors().length < constructorIndex + 1) {
            if (ignoreAccessible) {
                // try to find a private constructor
                if (clazz.getDeclaredConstructors().length < constructorIndex + 1) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "无法找到第 "
                            + constructorIndex + " 个构造函数");
                    return null;
                }
            } else {
                ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "无法找到第 "
                        + constructorIndex + " 个构造函数");
                return null;
            }
        }
        Constructor<? extends SlimefunItem> constructor;
        if (!ignoreAccessible) {
            constructor = (Constructor<? extends SlimefunItem>) clazz.getConstructors()[constructorIndex];
        } else {
            constructor = (Constructor<? extends SlimefunItem>) clazz.getDeclaredConstructors()[constructorIndex];
        }

        Object[] args = section.getList("args", new ArrayList<>()).toArray();
        List<Object> argTemplate =
                (List<Object>) section.getList("arg_template", List.of("group", "item", "recipe_type", "recipe"));
        Object[] originArgs = argTemplate.stream()
                .map(x -> {
                    if (x.equals("group")) return group.getSecondValue();
                    if (x.equals("item")) return sfis;
                    if (x.equals("recipe_type")) return rt.getSecondValue();
                    if (x.equals("recipe")) return recipe;
                    return x;
                })
                .filter(Objects::nonNull)
                .toArray();
        SlimefunItem instance;
        try {
            List<Object> newArgs = new ArrayList<>(List.of(originArgs));
            newArgs.addAll(List.of(args));
            instance = constructor.newInstance(newArgs.toArray());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "无法创建类", e);
            return null;
        }

        if (section.contains("method")) {
            ConfigurationSection methodArray = section.getConfigurationSection("method");
            for (String methodName : methodArray.getKeys(false)) {
                Object[] args1;

                if (methodArray.isList(methodName)) {
                    args1 = methodArray.getList(methodName, new ArrayList<>()).toArray();
                } else {
                    args1 = new Object[] {methodArray.get(methodName)};
                }

                Method method = getMethod(
                        clazz,
                        methodName,
                        Arrays.stream(args1).map(Object::getClass).toArray(Class<?>[]::new));
                if (method == null) {
                    ExceptionHandler.handleError(
                            "在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "没有找到方法" + methodName);
                    continue;
                }

                try {
                    if (ignoreAccessible) {
                        method.setAccessible(true);
                    }
                    method.invoke(instance, args1);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "方法调用异常", e);
                }
            }
        }

        if (section.contains("field")) {
            ConfigurationSection fieldArray = section.getConfigurationSection("field");
            for (String fieldName : fieldArray.getKeys(false)) {
                try {
                    Field field = getField(clazz, fieldName);

                    if (field == null) {
                        ExceptionHandler.handleError(
                                "在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "没有找到字段" + fieldName);
                        continue;
                    }
                    if (Modifier.isStatic(field.getModifiers())) {
                        ExceptionHandler.handleError(
                                "在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "字段" + fieldName + "为static");
                        return null;
                    }

                    if (Modifier.isFinal(field.getModifiers())) {
                        ExceptionHandler.handleError(
                                "在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "字段" + fieldName + "为final");
                        return null;
                    }

                    if (ignoreAccessible) {
                        field.setAccessible(true);
                    }
                    Object object = fieldArray.getObject(fieldName, field.getType());
                    field.set(instance, object);
                } catch (Throwable e) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "字段修改异常", e);
                }
            }
        }
        try {
            instance.register(RykenSlimefunCustomizer.INSTANCE);
        } catch (Throwable e) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "注册失败", e);
        }

        return instance;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }
        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }

    private Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        for (Method method : ReflectionUtils.getAllMethods(clazz)) {
            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                return method;
            }
        }

        return null;
    }

    private Field getField(Object obj, String name) {
        for (Field field : ReflectionUtils.getAllFields(obj.getClass())) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }
}
