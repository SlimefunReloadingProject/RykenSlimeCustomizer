package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun.AdvancedNestedItemGroup;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun.ItemGroupButton;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class ItemGroupReader extends YamlReader<ItemGroup> {
    public ItemGroupReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public ItemGroup readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult conflict = ExceptionHandler.handleGroupIdConflict(s);

        if (conflict == ExceptionHandler.HandleResult.FAILED) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);
        if (stack == null) {
            ExceptionHandler.handleError("Found error while loading recipe type " + s + " in " + addon.getAddonId()
                    + " addon: Item is null or has invalid format.");
            return null;
        }

        String type = section.getString("type", "");
        NamespacedKey key = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, s);

        int tier = section.getInt("tier", 3);

        ItemGroup group =
                switch (type) {
                    default -> new ItemGroup(key, stack, tier);
                    case "sub" -> {
                        NamespacedKey parent = new NamespacedKey(
                                RykenSlimefunCustomizer.INSTANCE,
                                section.getString("parent", "").toLowerCase());
                        ItemGroup raw = CommonUtils.getIf(Slimefun.getRegistry().getAllItemGroups(), ig -> ig.getKey()
                                .equals(parent));
                        if (raw == null) {
                            ExceptionHandler.handleError(
                                    "Found error while loading item group " + s + " in " + addon.getAddonId()
                                            + " addon: " + "the parent group " + parent.getKey() + " not found.");
                            yield null;
                        }
                        if (!(raw instanceof NestedItemGroup nig)) {
                            ExceptionHandler.handleError("Found error while loading item group " + s + " in "
                                    + addon.getAddonId() + " addon: " + "the parent group " + parent.getKey()
                                    + " is not a nested group.");
                            yield null;
                        }

                        yield new SubItemGroup(key, nig, stack, tier);
                    }
                    case "locked" -> {
                        List<NamespacedKey> parents = new ArrayList<>();
                        for (String ig : section.getStringList("parents")) {
                            NamespacedKey nk = NamespacedKey.fromString(ig);
                            if (nk == null) {
                                ExceptionHandler.handleWarning("Found error while loading item group " + s + " in "
                                        + addon.getAddonId() + " addon: " + "the parent group " + ig
                                        + " is not a valid NamespacedKey. Skipping.");
                                continue;
                            }
                            parents.add(nk);
                        }

                        yield new LockedItemGroup(key, stack, tier, parents.toArray(new NamespacedKey[] {}));
                    }
                    case "nested", "parent" -> new AdvancedNestedItemGroup(key, stack, tier);
                    case "seasonal" -> {
                        Month month = Month.of(section.getInt("month", 1));
                        yield new SeasonalItemGroup(key, month, tier, stack);
                    }
                    case "button" -> {
                        NamespacedKey parent = new NamespacedKey(
                                RykenSlimefunCustomizer.INSTANCE,
                                section.getString("parent", "").toLowerCase());
                        ItemGroup raw = CommonUtils.getIf(Slimefun.getRegistry().getAllItemGroups(), ig -> ig.getKey()
                                .equals(parent));
                        if (raw == null) {
                            ExceptionHandler.handleError(
                                    "Found error while loading item group " + s + " in " + addon.getAddonId()
                                            + " addon: " + "the parent group " + parent.getKey() + " not found.");
                            yield null;
                        }

                        if (!(raw instanceof AdvancedNestedItemGroup nig)) {
                            ExceptionHandler.handleError("Found error while loading item group " + s + " in "
                                    + addon.getAddonId() + " addon: " + "the parent group " + parent.getKey()
                                    + " is not a nested group from RykenSlimeCustomizer.");
                            yield null;
                        }

                        List<String> actions = section.getStringList("actions");

                        yield new ItemGroupButton(key, nig, stack, tier, actions);
                    }
                };

        if (group != null) {
            group.register(RykenSlimefunCustomizer.INSTANCE);
        }

        return group;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        return List.of();
    }
}
