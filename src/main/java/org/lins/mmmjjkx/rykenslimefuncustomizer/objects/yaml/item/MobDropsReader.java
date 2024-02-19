package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMobDrop;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.Collections;

public class MobDropsReader extends YamlReader<CustomMobDrop> {
    public MobDropsReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public CustomMobDrop readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section != null) {
            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String igId = section.getString("item_group");
            ConfigurationSection item = section.getConfigurationSection("item");
            ItemStack stack = CommonUtils.readItem(item, false, addon);
            if (stack == null) {
                ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中加载生物掉落" + s + ": 物品为空或格式错误导致无法加载");
                return null;
            }

            SlimefunItemStack sfis = new SlimefunItemStack(s, stack);

            Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
            if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

            String type = section.getString("entity");

            Pair<ExceptionHandler.HandleResult, EntityType> entity =
                    ExceptionHandler.handleEnumValueOf("无法在附属" + addon.getAddonName() + "中加载生物掉落" + s + ": 错误的生物类型",
                            EntityType.class, type);
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
                ExceptionHandler.handleError("在附属" + addon.getAddonName() + "中加载生物掉落" + s + "时发现问题: 掉落概率未设置或不应该小于1或大于100，已转换为1或100");
                chance = chance >= 100 ? 100 : 1;
            }

            Component translate = Component.translatable(entityType.translationKey());
            Component lore = CommonUtils.parseToComponent("&a击杀 ")
                    .append(translate).append(Component.space())
                    .append(CommonUtils.parseToComponent("&a时会有"))
                    .append(CommonUtils.parseToComponent("&b"+chance+"%"))
                    .append(CommonUtils.parseToComponent("的概率掉落"))
                    .decoration(TextDecoration.ITALIC, false);
            ItemStack itemStack = new CustomItemStack(eggMaterial, meta -> {
                meta.displayName(Component.text(entityType.toString()).decoration(TextDecoration.ITALIC, false));
                meta.lore(Collections.singletonList(lore));
            });
            ItemStack[] recipe = new ItemStack[]{null, null, null, null, itemStack};

            return new CustomMobDrop(group.getSecondValue(), sfis, recipe, chance);
        }
        return null;
    }
}
