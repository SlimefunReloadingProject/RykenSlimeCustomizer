package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.Range;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.generations.GenerationArea;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.generations.GenerationInfo;

public class GenerationReader extends YamlReader<GenerationInfo> {
    public GenerationReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public GenerationInfo readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        List<GenerationArea> areas = new ArrayList<>();
        ConfigurationSection areaSection = section.getConfigurationSection("areas");
        if (areaSection == null) return null;

        String id = addon.getId(s, section.getString("slimefun_id"));
        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        int c = 1;
        while (areaSection.contains(String.valueOf(c))) {
            areas.add(readArea(areaSection.getConfigurationSection(String.valueOf(c))));

            c++;
        }

        return new GenerationInfo(slimefunItemStack, areas);
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        return new ArrayList<>();
    }

    public static GenerationArea readArea(@Nonnull ConfigurationSection section) {
        int maxHeight = section.getInt("maxHeight");
        int mixHeight = section.getInt("mixHeight");
        int most = section.getInt("most");
        int amount = section.getInt("amount");
        int maxSize = section.getInt("maxSize");
        int minSize = section.getInt("minSize");
        Optional<Material> materialOptional =
                Optional.ofNullable(Material.matchMaterial(section.getString("replacement", "")));
        Material replacement = Material.STONE;

        if (materialOptional.isPresent()) {
            replacement = materialOptional.get();
        }
        @Nonnull World.Environment environment = World.Environment.valueOf(section.getString("environment"));

        return new GenerationArea(
                new Range(mixHeight, maxHeight), most, amount, new Range(minSize, maxSize), replacement, environment);
    }
}
