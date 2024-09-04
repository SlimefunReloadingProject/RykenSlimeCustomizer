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
        String id = section.getString("id_alias", s);

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
                "Found an error while loading super item " + s + " in addon " + addon.getAddonId()
                        + ": Invalid recipe type '" + recipeType + "'!",
                recipeType);

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        String className = section.getString("class", "");
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            ExceptionHandler.handleError(
                    "Found an error while loading super item " + s + " in addon " + addon.getAddonId() + ": "
                            + "Could not find class " + className,
                    e);
            return null;
        }

        if (!SlimefunItem.class.isAssignableFrom(clazz)) {
            ExceptionHandler.handleError("Found an error while loading super item " + s + " in addon "
                    + addon.getAddonId() + ": " + "Class " + className + " is not a SlimefunItem");
            return null;
        }
        // a zero-based number
        int ctorIndex = section.getInt("ctor", 0);
        if (clazz.getConstructors().length < ctorIndex + 1) {
            ExceptionHandler.handleError("Found an error while loading super item " + s + " in addon "
                    + addon.getAddonId() + ": " + "Invalid constructor at index " + ctorIndex);
            return null;
        }

        Constructor<? extends SlimefunItem> ctor =
                (Constructor<? extends SlimefunItem>) clazz.getConstructors()[ctorIndex];
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
            instance = ctor.newInstance(newArgs.toArray());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            ExceptionHandler.handleError(
                    "An unexpected error occurred while loading super item " + s + " in addon " + addon.getAddonId()
                            + ": Could not instantiate class",
                    e);
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
                            "Found an error while loading super item " + s + " in addon " + addon.getAddonId() + ": "
                                    + "Could not find method, but item still can be loaded" + methodName);
                    continue;
                }

                try {
                    method.setAccessible(true);
                    method.invoke(instance, args1);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    ExceptionHandler.handleError(
                            "An unexpected error occurred while loading super item " + s + " in addon "
                                    + addon.getAddonId() + ": Could not invoke method, but item still can be loaded",
                            e);
                }
            }
        }

        if (section.contains("field")) {
            ConfigurationSection fieldArray = section.getConfigurationSection("field");
            for (String fieldName : fieldArray.getKeys(false)) {
                try {
                    Field field = getField(clazz, fieldName);

                    if (field == null) throw new NoSuchFieldException(fieldName);
                    if (Modifier.isStatic(field.getModifiers()))
                        throw new IllegalAccessException("Found an error while loading super item " + s + " in addon "
                                + addon.getAddonId() + ": Field " + fieldName + "'s value cannot be modified");

                    if (Modifier.isFinal(field.getModifiers()))
                        throw new IllegalAccessException("Found an error while loading super item " + s + " in addon "
                                + addon.getAddonId() + ": Field " + fieldName + "'s value cannot be modified");

                    field.setAccessible(true);
                    Object object = fieldArray.getObject(fieldName, field.getType());
                    field.set(instance, object);
                } catch (Exception e) {
                    ExceptionHandler.handleError(
                            "An unexpected error occurred while loading super item " + s + " in addon "
                                    + addon.getAddonId()
                                    + ": Could not modify field's value, but item still can be loaded",
                            e);
                }
            }
        }
        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("Found an error while loading super item " + s + " in addon "
                    + addon.getAddonId() + ": " + "The item is null or has an invalid format");
            return null;
        }
        return List.of(new SlimefunItemStack(s, stack));
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
