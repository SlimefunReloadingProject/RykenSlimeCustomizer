package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.*;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.BookBinder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ProduceCollector;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf.AdvancedAnimalGrowthAccelerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf.AdvancedCropGrowthAccelerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf.AdvancedTreeGrowthAccelerator;
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
        String id = section.getString(s + ".id_alias", s);
        
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        
        SlimefunItemStack sfis = getPreloadItem(id);
        if (sfis == null) return null;

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED || group.getSecondValue() == null)
            return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt =
                ExceptionHandler.getRecipeType("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "错误的配方类型" + recipeType + "!", recipeType);

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED || rt.getSecondValue() == null) return null;
        
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
                        ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "处理速度小于1");
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

        SlimefunItem instance =
                switch (machineType) {
                    case ELECTRIC_FURNACE -> new ElectricFurnace(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_GOLD_PAN -> new ElectricGoldPan(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_SMELTERY -> new ElectricSmeltery(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_DUST_WASHER -> new ElectricDustWasher(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_ORE_GRINDER -> new ElectricOreGrinder(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_INGOT_FACTORY -> new ElectricIngotFactory(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_INGOT_PULVERIZER -> new ElectricIngotPulverizer(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case CHARGING_BENCH -> new ChargingBench(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case TREE_GROWTH_ACCELERATOR -> new AdvancedTreeGrowthAccelerator(
                            group.getSecondValue(),
                            sfis,
                            rt.getSecondValue(),
                            recipe,
                            capacity,
                            radius,
                            consumption);
                    case ANIMAL_GROWTH_ACCELERATOR -> new AdvancedAnimalGrowthAccelerator(
                            group.getSecondValue(),
                            sfis,
                            rt.getSecondValue(),
                            recipe,
                            capacity,
                            radius,
                            consumption);
                    case CROP_GROWTH_ACCELERATOR -> new AdvancedCropGrowthAccelerator(
                            group.getSecondValue(),
                            sfis,
                            rt.getSecondValue(),
                            recipe,
                            capacity,
                            radius,
                            consumption,
                            speed);
                    case FREEZER -> new Freezer(group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case CARBON_PRESS -> new CarbonPress(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_PRESS -> new ElectricPress(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case ELECTRIC_CRUCIBLE -> new ElectrifiedCrucible(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case FOOD_FABRICATOR -> new FoodFabricator(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case HEATED_PRESSURE_CHAMBER -> new HeatedPressureChamber(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case BOOK_BINDER -> new BookBinder(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case AUTO_ENCHANTER -> new AutoEnchanter(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case AUTO_DISENCHANTER -> new AutoDisenchanter(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case AUTO_ANVIL -> new AutoAnvil(
                                    group.getSecondValue(),
                                    repairFactor,
                                    sfis,
                                    rt.getSecondValue(),
                                    recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case AUTO_DRIER -> new AutoDrier(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case AUTO_BREWER -> new AutoBrewer(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case REFINERY -> new Refinery(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                    case PRODUCE_COLLECTOR -> new ProduceCollector(
                                    group.getSecondValue(), sfis, rt.getSecondValue(), recipe)
                            .setCapacity(capacity)
                            .setEnergyConsumption(consumption)
                            .setProcessingSpeed(speed);
                };

        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = section.getString(s + ".id_alias", s);

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载简单机器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(id, stack));
    }

    private boolean isAccelerator(SimpleMachineType type) {
        return type == SimpleMachineType.TREE_GROWTH_ACCELERATOR
                || type == SimpleMachineType.CROP_GROWTH_ACCELERATOR
                || type == SimpleMachineType.ANIMAL_GROWTH_ACCELERATOR;
    }
}
