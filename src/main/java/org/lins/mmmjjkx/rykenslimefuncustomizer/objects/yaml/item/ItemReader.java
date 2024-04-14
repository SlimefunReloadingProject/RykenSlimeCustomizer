package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.PiglinBarterDrop;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.utils.ColoredMaterial;
import lombok.SneakyThrows;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomDefaultItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomUnplaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts.*;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.BaseRadiationItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun.WitherProofBlockImpl;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ClassUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemReader extends YamlReader<SlimefunItem> {
    public ItemReader(YamlConfiguration config) {
        super(config);
    }

    @SneakyThrows
    @Override
    public SlimefunItem readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + s + ": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] itemStacks = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "错误的配方类型" + recipeType + "!", recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning("找不到脚本文件 " + file.getName());
            } else {
                eval = new JavaScriptEval(file, addon);
            }
        }

        CustomItem instance;

        boolean energy = section.contains("energy_capacity");
        boolean hasRadiation = section.contains("radiation");

        if (hasRadiation) {
            return setupRadiationItem(section, stack, group.getSecondValue(), s, rt.getSecondValue(), itemStacks, eval, addon);
        }

        if (energy) {
            double energyCapacity = section.getDouble("energy_capacity");
            if (energyCapacity < 1) {
                ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + s + "能源容量不能小于1");
                return null;
            }
            
            CommonUtils.addLore(stack, true, CommonUtils.parseToComponent("&8⇨ &e⚡ &70 / "+energyCapacity+" J"));

            instance = new CustomEnergyItem(group.getSecondValue(), new SlimefunItemStack(s, stack), rt.getSecondValue(), itemStacks, (float) energyCapacity, eval);
        } else if (section.getBoolean("placeable", false)) {
            instance = new CustomDefaultItem(group.getSecondValue(), new SlimefunItemStack(s, stack), rt.getSecondValue(), itemStacks);
        } else if (section.contains("rainbow")) {
            String materialType = section.getString("rainbow", "");
            if (!stack.getType().isBlock()) {
                ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + s + "非方块无法设置彩虹属性");
                return null;
            }
            if (materialType.equalsIgnoreCase("CUSTOM")) {
                List<String> materials = section.getStringList("rainbow_materials");
                if (materials.isEmpty()) {
                    ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + s + "彩虹属性材料列表为空");
                    return null;
                }
                List<Material> colorMaterials = new ArrayList<>();

                for (String material : materials) {
                    Pair<ExceptionHandler.HandleResult, Material> materialPair =
                            ExceptionHandler.handleEnumValueOf("错误的材料类型: "+material, Material.class, material);
                    Material material1 = materialPair.getSecondValue();
                    if (materialPair.getFirstValue() == ExceptionHandler.HandleResult.FAILED || material1 == null) {
                        return null;
                    }
                    colorMaterials.add(material1);
                }

                instance = new CustomRainbowBlock(group.getSecondValue(), new SlimefunItemStack(s, stack), rt.getSecondValue(), itemStacks, colorMaterials);
            } else {
                Pair<ExceptionHandler.HandleResult, ColoredMaterial> coloredMaterialPair =
                        ExceptionHandler.handleEnumValueOf("错误的可染色材料类型: " + materialType, ColoredMaterial.class, materialType);
                ColoredMaterial coloredMaterial = coloredMaterialPair.getSecondValue();
                if (coloredMaterialPair.getFirstValue() == ExceptionHandler.HandleResult.FAILED || coloredMaterial == null) {
                    return null;
                }
                instance = new CustomRainbowBlock(group.getSecondValue(), new SlimefunItemStack(s, stack), rt.getSecondValue(), itemStacks, coloredMaterial);
            }
        } else {
            instance = new CustomUnplaceableItem(group.getSecondValue(), new SlimefunItemStack(s, stack), rt.getSecondValue(), itemStacks, eval);
        }

        Object[] constructorArgs = instance.constructorArgs();

        if (section.getBoolean("anti_wither", false)) {
            if (!stack.getType().isBlock()) {
                ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + s + "非方块无法设置防凋零属性");
                return null;
            }

            Class<? extends CustomItem> clazz = (Class<? extends CustomItem>) ClassUtils.generateClass(instance.getClass(), instance.getClass().getName(),
                    "WitherProof", "Item", new Class[]{WitherProofBlockImpl.class}, null
            );

            instance = (CustomItem) clazz.getDeclaredConstructors()[0].newInstance(constructorArgs);
        }

        if (section.getBoolean("soulbound", false)) {
            Class<? extends CustomItem> clazz = (Class<? extends CustomItem>) ClassUtils.generateClass(instance.getClass(),
                    instance.getClass().getName(), "Soulbound", "Item", new Class[]{Soulbound.class}, null
            );

            instance = (CustomItem) clazz.getDeclaredConstructors()[0].newInstance(constructorArgs);
        }

        if (section.getBoolean("piglin_trade", false)) {
            int chance = section.getInt("piglin_chance", 100);
            if (chance < 0 || chance > 100) {
                ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + s + "猪灵交易掉落几率必须在0-100之间");
                return null;
            }

            Class<? extends CustomItem> clazz = (Class<? extends CustomItem>) ClassUtils.generateClass(instance.getClass(),
                    instance.getClass().getName(), "PiglinTradeAble", "Item", new Class[]{Radioactive.class}, builder ->
                            builder.method(ElementMatchers.isDeclaredBy(PiglinBarterDrop.class))
                                    .intercept(FixedValue.value(chance))
            );

            instance = (CustomItem) clazz.getDeclaredConstructors()[0].newInstance(constructorArgs);
        }

        instance.setHidden(section.getBoolean("hidden", false));
        instance.setUseableInWorkbench(section.getBoolean("vanilla", false));

        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }

    @SneakyThrows
    private SlimefunItem setupRadiationItem(ConfigurationSection section, ItemStack original, ItemGroup itemGroup,
                                            String id, RecipeType recipeType, ItemStack[] recipe, JavaScriptEval eval,
                                            ProjectAddon addon) {
        String radio = section.getString("radiation");
        Pair<ExceptionHandler.HandleResult, Radioactivity> radioactivityPair =
                ExceptionHandler.handleEnumValueOf("错误的辐射等级级别: "+radio, Radioactivity.class, radio);
        Radioactivity radioactivity = radioactivityPair.getSecondValue();

        if (radioactivityPair.getFirstValue() == ExceptionHandler.HandleResult.FAILED || radioactivity == null) {
            return null;
        }

        boolean energy = section.contains("energy_capacity");

        CommonUtils.addLore(original, true, CommonUtils.parseToComponent(radioactivity.getLore()));

        BaseRadiationItem instance;

        if (energy) {
            double energyCapacity = section.getDouble("energy_capacity");
            instance = new CustomEnergyRadiationItem(itemGroup, new SlimefunItemStack(id, original), recipeType, recipe, radioactivity, (float) energyCapacity, eval);
        } else if (section.getBoolean("placeable", false)) {
            instance = new CustomDefaultRadiationItem(itemGroup, new SlimefunItemStack(id, original), recipeType, recipe, radioactivity);
        } else {
            instance = new CustomRadiationItem(itemGroup, new SlimefunItemStack(id, original), recipeType, recipe, eval, radioactivity);
        }

        Object[] constructorArgs = instance.constructArgs();

        if (section.getBoolean("anti_wither", false)) {
            if (!original.getType().isBlock()) {
                ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + id + "非方块无法设置防凋零属性");
                return null;
            }

            Class<? extends BaseRadiationItem> clazz = (Class<? extends BaseRadiationItem>) ClassUtils.generateClass(instance.getClass(), instance.getClass().getName(),
                    "WitherProof", "Item", new Class[]{WitherProofBlockImpl.class}, null
            );

            instance = (BaseRadiationItem) clazz.getDeclaredConstructors()[0].newInstance(constructorArgs);
        }

        if (section.getBoolean("soulbound", false)) {
            Class<? extends BaseRadiationItem> clazz = (Class<? extends BaseRadiationItem>) ClassUtils.generateClass(instance.getClass(),
                    instance.getClass().getName(), "Soulbound", "Item", new Class[]{Soulbound.class}, null
            );

            instance = (BaseRadiationItem) clazz.getDeclaredConstructors()[0].newInstance(constructorArgs);
        }

        if (section.getBoolean("piglin_trade", false)) {
            int chance = section.getInt("piglin_trade_chance", 100);
            if (chance < 0 || chance > 100) {
                ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载物品" + id + "猪灵交易掉落几率必须在0-100之间");
                return null;
            }

            Class<? extends CustomItem> clazz = (Class<? extends CustomItem>) ClassUtils.generateClass(instance.getClass(),
                    instance.getClass().getName(), "PiglinTrade", "Item", new Class[]{Radioactive.class}, builder ->
                            builder.method(ElementMatchers.isDeclaredBy(PiglinBarterDrop.class))
                                    .intercept(FixedValue.value(chance))
            );

            instance = (BaseRadiationItem) clazz.getDeclaredConstructors()[0].newInstance(constructorArgs);
        }

        instance.setHidden(section.getBoolean("hidden", false));
        instance.setUseableInWorkbench(section.getBoolean("vanilla", false));
        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }
}