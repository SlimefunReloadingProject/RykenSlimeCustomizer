package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.factories.SimpleMachineFactory;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.SimpleMachineType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class SimpleMachineReader extends YamlReader<SlimefunItem> {
    public SimpleMachineReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public SlimefunItem readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = addon.getId(s, section.getString("id_alias"));

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        Pair<ExceptionHandler.HandleResult, ItemGroup> itemGroupPair = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (itemGroupPair.getFirstValue() == ExceptionHandler.HandleResult.FAILED
                || itemGroupPair.getSecondValue() == null) return null;

        Pair<RecipeType, ItemStack[]> recipePair = getRecipe(section, addon);
        RecipeType rt = recipePair.getFirstValue();
        ItemStack[] recipe = recipePair.getSecondValue();

        String machineTypeStr = section.getString("type");

        Pair<ExceptionHandler.HandleResult, SimpleMachineType> machineTypePair = ExceptionHandler.handleEnumValueOf(
                "错误的简单机器类型 " + machineTypeStr, SimpleMachineType.class, machineTypeStr);
        if (machineTypePair.getFirstValue() == ExceptionHandler.HandleResult.FAILED
                || machineTypePair.getSecondValue() == null) {
            return null;
        }

        SimpleMachineType machineType = machineTypePair.getSecondValue();
        ConfigurationSection settings = section.getConfigurationSection("settings");

        if (settings == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "机器没有设置");
            return null;
        }

        int capacity = 0;
        int consumption = 0;
        int speed = 1;
        int radius = 1;
        int repairFactor = 10;

        if (machineType.isEnergy()) {
            capacity = settings.getInt("capacity");
            if (capacity < 1) {
                ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "容量小于1");
                return null;
            }

            consumption = settings.getInt("consumption");
            if (consumption < 1) {
                ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "消耗能量小于1");
                return null;
            }

            if (!isAccelerator(machineType)) {
                speed = settings.getInt("speed", 1);
                if (speed < 1) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "处理速度小于1");
                    return null;
                }
            } else {
                radius = settings.getInt("radius", 1);
                if (radius < 1) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "范围小于1");
                    return null;
                }

                if (machineType == SimpleMachineType.CROP_GROWTH_ACCELERATOR) {
                    speed = settings.getInt("speed", 1);
                    if (speed < 1) {
                        ExceptionHandler.handleError(
                                "在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "处理速度小于1");
                        return null;
                    }
                }
            }

            if (machineType == SimpleMachineType.AUTO_ANVIL) {
                repairFactor = settings.getInt("repair_factor", 10);
                if (repairFactor < 1) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "修理因子小于1");
                    return null;
                }
            }
        }

        SlimefunItem instance = SimpleMachineFactory.create(
                itemGroupPair.getSecondValue(),
                slimefunItemStack,
                rt,
                recipe,
                machineType,
                capacity,
                consumption,
                speed,
                radius,
                repairFactor);

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
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }

    private boolean isAccelerator(SimpleMachineType type) {
        return type == SimpleMachineType.TREE_GROWTH_ACCELERATOR
                || type == SimpleMachineType.CROP_GROWTH_ACCELERATOR
                || type == SimpleMachineType.ANIMAL_GROWTH_ACCELERATOR;
    }
}
