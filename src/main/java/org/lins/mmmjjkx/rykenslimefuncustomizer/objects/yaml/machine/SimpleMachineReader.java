package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.*;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.SimpleMachineType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class SimpleMachineReader extends YamlReader<SlimefunItem> {
    public SimpleMachineReader(YamlConfiguration config) {
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

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载简单机器"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED || group.getSecondValue() == null) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "错误的配方类型" + recipeType + "!", recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED || rt.getSecondValue() == null) return null;
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        String machineTypeStr = section.getString("type");

        Pair<ExceptionHandler.HandleResult, SimpleMachineType> machineTypePair = ExceptionHandler.handleEnumValueOf(
                "错误的简单机器类型 " + machineTypeStr, SimpleMachineType.class, machineTypeStr);
        if (machineTypePair.getFirstValue() == ExceptionHandler.HandleResult.FAILED || machineTypePair.getSecondValue() == null) {
            return null;
        }

        SimpleMachineType machineType = machineTypePair.getSecondValue();
        ConfigurationSection settings = section.getConfigurationSection("settings");

        if (settings == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载简单机器"+s+": 机器没有设置");
            return null;
        }

        int capacity = 0;
        int consumption = 0;
        int speed = 1;
        
        if (machineType.isEnergy()) {
            capacity = settings.getInt("capacity");
            if (capacity < 1) {
                ExceptionHandler.handleError("无法加载简单机器"+s+": 容量未设置或小于1");
                return null;
            }
            
            consumption = settings.getInt("consumption");
            if (consumption < 1) {
                ExceptionHandler.handleError("无法加载简单机器"+s+": 消耗能量未设置或小于1");
                return null;
            }

            speed = settings.getInt("speed", 1);
            if (speed < 1) {
                ExceptionHandler.handleError("无法加载简单机器" + s + ": 处理速度未设置或小于1");
                return null;
            }
        }

        SlimefunItem instance = switch (machineType) {
            case ELECTRIC_FURNACE -> new ElectricFurnace(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
            case ELECTRIC_GOLD_PAN -> new ElectricGoldPan(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
            case ELECTRIC_SMELTERY -> new ElectricSmeltery(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
            case ELECTRIC_DUST_WASHER -> new ElectricDustWasher(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
            case ELECTRIC_ORE_GRINDER -> new ElectricOreGrinder(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
            case ELECTRIC_INGOT_FACTORY -> new ElectricIngotFactory(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
            case ELECTRIC_INGOT_PULVERIZER -> new ElectricIngotPulverizer(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
            case CHARGING_BENCH -> new ChargingBench(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe)
                    .setCapacity(capacity).setEnergyConsumption(consumption).setProcessingSpeed(speed);
        };

        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }
}
