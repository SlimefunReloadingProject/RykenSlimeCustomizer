package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

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

        ItemStack stack = CommonUtils.readItem(section.getConfigurationSection("item"));
        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载机器"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }


    }
}
