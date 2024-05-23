package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class RecipeTypesReader extends YamlReader<RecipeType> {
    public RecipeTypesReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public RecipeType readEach(String s) {
        ConfigurationSection configurationSection = configuration.getConfigurationSection(s);
        if (configurationSection == null) return null;

        ItemStack item = CommonUtils.readItem(configurationSection, false, addon);
        if (item == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载配方类型" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return new RecipeType(new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, s.toLowerCase()), item);
    }

    // 配方类型不需要预加载物品
    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        return List.of();
    }
}
