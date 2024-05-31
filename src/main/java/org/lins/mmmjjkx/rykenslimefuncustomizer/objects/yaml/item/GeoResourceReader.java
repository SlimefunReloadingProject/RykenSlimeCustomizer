package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.DropFromBlock;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.XMaterial;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class GeoResourceReader extends YamlReader<CustomGeoResource> {
    public GeoResourceReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomGeoResource readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section != null) {
            String id = section.getString(s + ".id_alias", s);

            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String igId = section.getString("item_group");

            Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
            if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

            SlimefunItemStack sfis = getPreloadItem(id);
            if (sfis == null) return null;

            ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
            String recipeType = section.getString("recipe_type", "NULL");
            int maxDeviation = section.getInt("max_deviation", 1);
            boolean obtainableFromGEOMiner = section.getBoolean("obtain_from_geo_miner", true);
            String name = section.getString("geo_name", "");

            Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                    "在附属" + addon.getAddonId() + "中加载自然资源" + s + "时遇到了问题: " + "错误的配方类型" + recipeType + "!", recipeType);

            if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

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
                    return biomes.getInt("others", 0);
                }
            };

            if (section.contains("drop_from")) {
                int chance = section.getInt("drop_chance", 100);
                int amount = section.isInt("drop_amount") ? section.getInt("drop_amount", 1) : -1;

                if (chance < 0 || chance > 100) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载自然资源" + s + "时遇到了问题: " + "掉落几率"
                            + chance + "不在0-100范围内! 已转为100");
                    chance = 100;
                }

                String dropMaterial = section.getString("drop_from", "");

                Optional<XMaterial> xm = XMaterial.matchXMaterial(dropMaterial);
                if (xm.isPresent()) {
                    Material material = xm.get().parseMaterial();
                    if (material == null) {
                        ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载自然资源" + s + "时遇到了问题: "
                                + "无法指定掉落方块材料类型" + dropMaterial + "，已转为石头");
                    } else {
                        if (amount == -1) {
                            String between = section.getString("drop_amount", "1");
                            if (between.contains("-")) {
                                String[] split = between.split("-");
                                if (split.length == 2) {
                                    int min = Integer.parseInt(split[0]);
                                    int max = Integer.parseInt(split[1]);
                                    DropFromBlock.addDrop(
                                            material, new DropFromBlock.Drop(sfis, chance, addon, min, max));
                                } else {
                                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载自然资源" + s + "时遇到了问题: "
                                            + "无法读取掉落数量区间" + between + "，已把掉落数量转为1");
                                    DropFromBlock.addDrop(material, new DropFromBlock.Drop(sfis, chance, addon));
                                }
                            }
                        } else {
                            DropFromBlock.addDrop(
                                    material, new DropFromBlock.Drop(sfis, chance, addon, amount, amount));
                        }
                    }
                } else {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载自然资源" + s + "时遇到了问题: " + "指定掉落方块材料类型"
                            + dropMaterial + "不存在!");
                }
            }

            if (recipe == null) recipe = new ItemStack[9];

            return new CustomGeoResource(
                    group.getSecondValue(),
                    sfis,
                    rt.getSecondValue(),
                    recipe,
                    supply,
                    maxDeviation,
                    obtainableFromGEOMiner,
                    name);
        }
        return null;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);

        if (section == null) return null;

        String id = section.getString(s + ".id_alias", s);

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);
        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载自然资源" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(id, stack));
    }
}
