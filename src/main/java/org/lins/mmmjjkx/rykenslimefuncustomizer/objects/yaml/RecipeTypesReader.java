package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class RecipeTypesReader extends YamlReader<RecipeType> {
    public RecipeTypesReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public RecipeType readEach(String s, ProjectAddon addon) {
        ConfigurationSection configurationSection = configuration.getConfigurationSection(s);
        if (configurationSection == null) return null;

        ItemStack item = CommonUtils.readItem(configurationSection, false, addon);
        if (item == null) {
            ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载配方类型" + s + ": 物品为空或格式错误导致无法加载");
            return null;
        }

        return new RecipeType(new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, s.toLowerCase()), item);
    }
}