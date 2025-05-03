package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts.CustomMobDrop;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

@SuppressWarnings("deprecation")
public class MobDropsReader extends YamlReader<CustomMobDrop> {
    public MobDropsReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomMobDrop readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section != null) {
            String id = addon.getId(s, section.getString("id_alias"));

            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String igId = section.getString("item_group");

            Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
            if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

            SlimefunItemStack sfis = getPreloadItem(id);
            if (sfis == null) return null;

            String type = section.getString("entity");

            Pair<ExceptionHandler.HandleResult, EntityType> entity = ExceptionHandler.handleEnumValueOf(
                    "在附属" + addon.getAddonId() + "中加载生物掉落物" + s + "时遇到了问题: " + "错误的生物类型", EntityType.class, type);
            if (entity.getFirstValue() == ExceptionHandler.HandleResult.FAILED) {
                return null;
            }

            Material eggMaterial;
            EntityType entityType = entity.getSecondValue();
            if (entityType == null) return null;

            Pair<ExceptionHandler.HandleResult, Material> egg =
                    ExceptionHandler.handleEnumValueOf("", Material.class, entityType + "_SPAWN_EGG");
            if (egg.getFirstValue() == ExceptionHandler.HandleResult.SUCCESS && egg.getSecondValue() != null) {
                eggMaterial = egg.getSecondValue();
            } else {
                eggMaterial = Material.EGG;
            }

            int chance = section.getInt("chance");

            if (chance < 1 || chance > 100) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载生物掉落物" + s + "时遇到了问题: " + "掉落概率未设置或不应该小于1或大于100，已转换为1或100");
                chance = chance >= 100 ? 100 : 1;
            }

            Component lore = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&a击杀 ")
                    .append(LegacyComponentSerializer.legacyAmpersand().deserialize("&b"))
                    .append(Component.translatable(entityType.translationKey()))
                    .append(LegacyComponentSerializer.legacyAmpersand().deserialize(" &a时会有"))
                    .append(LegacyComponentSerializer.legacyAmpersand().deserialize(" &b " + chance + "%"))
                    .append(LegacyComponentSerializer.legacyAmpersand().deserialize(" &a的概率掉落"));

            ItemStack itemStack = new CustomItemStack(eggMaterial, meta -> {
                meta.setDisplayName(entityType.toString());
                meta.lore(List.of(lore));
            });
            ItemStack[] recipe = new ItemStack[] {null, null, null, null, itemStack};

            return new CustomMobDrop(group.getSecondValue(), sfis, recipe, chance, entityType, sfis);
        }
        return null;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String id) {
        ConfigurationSection section = configuration.getConfigurationSection(id);

        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);
        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载生物掉落物" + id + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(id, section.getString("id_alias")), stack));
    }
}
