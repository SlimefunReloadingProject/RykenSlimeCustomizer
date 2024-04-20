package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.function.BiFunction;

public class GeoResourceReader extends YamlReader<CustomGeoResource> {
    public GeoResourceReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public CustomGeoResource readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section != null) {
            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String igId = section.getString("item_group");
            ConfigurationSection item = section.getConfigurationSection("item");
            ItemStack stack = CommonUtils.readItem(item, false, addon);
            if (stack == null) {
                ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载GEO资源"+s+": 物品为空或格式错误导致无法加载");
                return null;
            }

            Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
            if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
            ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
            String recipeType = section.getString("recipe_type", "NULL");
            int maxDeviation = section.getInt("max_deviation", 1);
            boolean obtainableFromGEOMiner = section.getBoolean("obtain_from_geo_miner", true);
            String name = section.getString("geo_name", "");

            Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                    "错误的配方类型" + recipeType + "!", recipeType
            );

            if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
            SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

            ConfigurationSection sup = section.getConfigurationSection("supply");
            BiFunction<World.Environment, Biome, Integer> supply = (e, b) -> {
                if (sup == null) {
                    return 0;
                }

                if (e == World.Environment.CUSTOM) return 0;

                String env = e.toString().toLowerCase();
                String path = b.toString().toLowerCase();
                boolean isSection = sup.isConfigurationSection(env);
                if (!isSection) {
                    return sup.getInt(env, 0);
                }
                ConfigurationSection biomes = sup.getConfigurationSection(env);
                if (biomes == null) return 0;
                if (biomes.contains(path)) {
                    return biomes.getInt(path, 0);
                } else {
                    if (biomes.contains("others")) {
                        return biomes.getInt("others", 0);
                    } else {
                        return 0;
                    }
                }
            };

            if (recipe == null) recipe = new ItemStack[9];

            return new CustomGeoResource(group.getSecondValue(), slimefunItemStack,
                    rt.getSecondValue(), recipe, supply, maxDeviation, obtainableFromGEOMiner, name);
        }
        return null;
    }
}