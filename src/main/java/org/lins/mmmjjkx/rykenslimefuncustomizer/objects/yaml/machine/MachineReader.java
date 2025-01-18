package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomEnergyGenerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomNoEnergyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineRecord;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class MachineReader extends YamlReader<AbstractEmptyMachine<?>> {
    public MachineReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public AbstractEmptyMachine<?> readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = addon.getId(s, section.getString("id_alias"));

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "在附属" + addon.getAddonId() + "中加载机器" + s + "时遇到了问题: " + "错误的配方类型" + recipeType + "!", recipeType);

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning(
                        "在附属" + addon.getAddonId() + "中加载机器" + s + "时遇到了问题: " + "找不到脚本文件 " + file.getName());
            } else {
                eval = new JavaScriptEval(file, addon);
            }
        }

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");
        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(id));

        AbstractEmptyMachine<?> machine;
        CustomNoEnergyMachine defaultNoEnergyMachine = new CustomNoEnergyMachine(
                group.getSecondValue(),
                slimefunItemStack,
                rt.getSecondValue(),
                recipe,
                menu,
                input,
                output,
                eval,
                -1);

        if (section.contains("energy")) {
            ConfigurationSection energySettings = section.getConfigurationSection("energy");
            if (energySettings == null) {
                ExceptionHandler.handleWarning("无法读取在附属" + addon.getAddonId() + "中的机器" + s + "的能源设置，已转为无电机器");
                return defaultNoEnergyMachine;
            }
            int capacity = energySettings.getInt("capacity");
            if (capacity < 0) {
                ExceptionHandler.handleError("无法读取在附属" + addon.getAddonId() + "中的机器" + s + "的能源设置，已转为无电机器，原因: 容量不能小于0");
                return defaultNoEnergyMachine;
            }
            MachineRecord record = new MachineRecord(capacity);
            String encType = energySettings.getString("type");
            Pair<ExceptionHandler.HandleResult, EnergyNetComponentType> enc = ExceptionHandler.handleEnumValueOf(
                    "无法读取在附属" + addon.getAddonId() + "中的机器" + s + "的能源设置，已转为无电机器，原因: 错误的能源网络组件类型" + encType,
                    EnergyNetComponentType.class,
                    encType);
            if (enc.getFirstValue() == ExceptionHandler.HandleResult.FAILED) {
                return defaultNoEnergyMachine;
            }
            
            if (energySettings.contains("energyOutput")) {
                int energyOutput = section.getInt("energyOutput");
                if (energyOutput < 0) {
                    ExceptionHandler.handleError("无法读取在附属" + addon.getAddonId() + "中的自定义发电机" + s + "的能源设置，已转为普通有电机器，原因: 能量输出不能小于0");
                    machine = new CustomMachine(
                            group.getSecondValue(),
                            slimefunItemStack,
                            rt.getSecondValue(),
                            recipe,
                            menu,
                            input,
                            output,
                            record,
                            enc.getSecondValue(),
                            eval);
                    return machine;
                } else {
                    machine = new CustomEnergyGenerator(
                            group.getSecondValue(),
                            slimefunItemStack,
                            rt.getSecondValue(),
                            recipe,
                            menu,
                            input,
                            output,
                            record,
                            enc.getSecondValue(),
                            eval,
                            energyOutput);
                }
            } else {
                machine = new CustomMachine(
                        group.getSecondValue(),
                        slimefunItemStack,
                        rt.getSecondValue(),
                        recipe,
                        menu,
                        input,
                        output,
                        record,
                        enc.getSecondValue(),
                        eval);
            }
        } else {
            List<Integer> workSlots = new ArrayList<>();
            if (section.isInt("work")) {
                workSlots = Collections.singletonList(section.getInt("work", -1));
            } else if (section.isList("work")) {
                workSlots = section.getIntegerList("work");
            }

            machine = new CustomNoEnergyMachine(
                    group.getSecondValue(),
                    slimefunItemStack,
                    rt.getSecondValue(),
                    recipe,
                    menu,
                    input,
                    output,
                    eval,
                    workSlots);
        }

        machine.register(RykenSlimefunCustomizer.INSTANCE);
        return machine;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载机器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }
}
