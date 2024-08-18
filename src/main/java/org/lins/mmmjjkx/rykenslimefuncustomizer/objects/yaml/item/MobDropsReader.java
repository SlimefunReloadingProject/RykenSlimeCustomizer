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
            String id = section.getString("id_alias", s);

            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String igId = section.getString("item_group");

            Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
            if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

            SlimefunItemStack sfis = getPreloadItem(id);
            if (sfis == null) return null;

            String type = section.getString("entity");

            Pair<ExceptionHandler.HandleResult, EntityType> entity = ExceptionHandler.handleEnumValueOf(
                    "Found an error while loading mob drop " + s + " in " + addon.getAddonId()
                            + ": Invalid entity type " + type,
                    EntityType.class,
                    type);
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
                ExceptionHandler.handleError("Found an error while loading mob drop " + s + " in " + addon.getAddonId()
                        + ": Chance must be between 1 and 100. Using 1 or 100 instead.");
                chance = chance >= 100 ? 100 : 1;
            }

            Component lore = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&aKill &b")
                    .append(Component.translatable(entityType.translationKey()))
                    .append(LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(" &awill have a chance of &b" + chance + "% &ato drop"));

            ItemStack itemStack = new CustomItemStack(eggMaterial, meta -> {
                meta.setDisplayName(entityType.toString());
                meta.lore(List.of(lore));
            });
            ItemStack[] recipe = new ItemStack[] {null, null, null, null, itemStack};

            return new CustomMobDrop(group.getSecondValue(), sfis, recipe, chance, entityType);
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
            ExceptionHandler.handleError("Found an error while loading mob drop " + id + " in addon "
                    + addon.getAddonId() + ": " + "The item is null or has an invalid format");
            return null;
        }

        SlimefunItemStack sfis = new SlimefunItemStack(id, stack);

        return List.of(sfis);
    }
}
