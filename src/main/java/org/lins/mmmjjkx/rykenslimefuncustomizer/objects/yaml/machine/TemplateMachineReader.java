package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.List;

public class TemplateMachineReader extends YamlReader<CustomTemplateMachine> {
    public TemplateMachineReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomTemplateMachine readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;


        return null;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(s, stack));
    }
}
