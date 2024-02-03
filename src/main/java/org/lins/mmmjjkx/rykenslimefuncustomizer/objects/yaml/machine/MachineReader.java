package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomNoEnergyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineRecord;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MachineReader extends YamlReader<AbstractEmptyMachine> {
    public MachineReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<AbstractEmptyMachine> readAll(ProjectAddon addon) {
        List<AbstractEmptyMachine> machines = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var machine = readEach(key, addon);
            if (machine != null) {
                machines.add(machine);
            }
        }
        return machines;
    }

    @Override
    public AbstractEmptyMachine readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载机器"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.handleField(
                "错误的配方类型" + recipeType + "!", "", RecipeType.class, recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning("找不到脚本文件 " + file.getName());
            } else {
                eval = new JavaScriptEval(file);
            }
        }

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");
        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(s));

        AbstractEmptyMachine machine;

        if (section.contains("energy")) {
            ConfigurationSection energySettings = section.getConfigurationSection("energy");
            if (energySettings == null) {
                ExceptionHandler.handleWarning("无法获取机器"+s+"的能源设置，已转为无电机器");
                machine = new CustomNoEnergyMachine(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, menu, input, output, eval, -1);
                return machine;
            }
            int capacity = energySettings.getInt("capacity");
            if (capacity < 1) {
                ExceptionHandler.handleError("无法读取机器"+s+"的能源设置，已转为无电机器，原因: 容量不能小于1");
                machine = new CustomNoEnergyMachine(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, menu, input, output, eval, -1);
                return machine;
            }
            int totalTicks = energySettings.getInt("totalTicks");
            if (totalTicks < 1) {
                ExceptionHandler.handleError("无法读取机器"+s+"的能源设置，已转为无电机器，原因: 总粘液刻不能小于1");
                machine = new CustomNoEnergyMachine(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, menu, input, output, eval, -1);
                return machine;
            }
            MachineRecord record = new MachineRecord(capacity, totalTicks);
            String encType = energySettings.getString("type");
            Pair<ExceptionHandler.HandleResult, EnergyNetComponentType> enc = ExceptionHandler.handleEnumValueOf(
                    "无法读取机器"+s+"的能源设置，已转为无电机器，原因: 错误的能源网络组件类型"+encType,
                    "无法读取机器"+s+"的能源设置，已转为无电机器，原因: 能源网络组件类型不能为空",
                    EnergyNetComponentType.class, encType);
            if (enc.getFirstValue() == ExceptionHandler.HandleResult.FAILED) {
                machine = new CustomNoEnergyMachine(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, menu, input, output, eval, -1);
                return machine;
            }
            machine = new CustomMachine(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, menu, input, output, record, enc.getSecondValue(), eval);
        } else {
            int workSlot = section.getInt("work", -1);
            machine = new CustomNoEnergyMachine(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, menu, input, output, eval, workSlot);
        }

        if (menu != null) {
            menu.setInvb(machine);
        }
        machine.register(RykenSlimefunCustomizer.INSTANCE);

        ExceptionHandler.handleItemGroupAddItem(addon, igId, machine);
        return machine;
    }
}
