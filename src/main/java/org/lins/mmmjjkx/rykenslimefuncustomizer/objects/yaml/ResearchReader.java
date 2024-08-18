package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class ResearchReader extends YamlReader<Research> {
    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

    public ResearchReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public Research readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        if (!VALID_KEY.matcher(s).matches()) {
            ExceptionHandler.handleError("Found errors while reading research " + s + " in " + addon.getAddonId()
                    + " addon: Research ID must only contain [a-z0-9/._-] characters.");
            return null;
        }

        int researchId = section.getInt("id");
        String name = section.getString("name");
        int cost = section.getInt("levelCost");
        List<String> items = section.getStringList("items");

        if (researchId <= 0) {
            ExceptionHandler.handleError("Found errors while reading research " + s + " in " + addon.getAddonId()
                    + " addon: id must be greater than 0.");
            return null;
        }

        if (cost <= 0) {
            ExceptionHandler.handleError("Found errors while reading research " + s + " in " + addon.getAddonId()
                    + " addon: levelCost must be greater than 0.");
            return null;
        }

        if (name == null || name.isBlank()) {
            ExceptionHandler.handleError("Found errors while reading research " + s + " in " + addon.getAddonId()
                    + " addon: name must be set.");
            return null;
        }

        name = CMIChatColor.translate(name);

        Research research =
                new Research(new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, s), researchId, name, cost);

        for (String item : items) {
            SlimefunItem sfItem = SlimefunItem.getById(item);
            if (sfItem == null) {
                ExceptionHandler.handleWarning("Found errors while reading research " + s + " in " + addon.getAddonId()
                        + " addon: " + item + " is not a Slimefun item. Skipping adding to the research.");
                ;
                continue;
            }
            research.addItems(sfItem);
        }

        research.register();

        return research;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        return List.of();
    }
}
