package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.util.ArrayList;
import java.util.List;

public abstract class YamlReader<T> {
    private final List<String> lateInits;
    protected YamlConfiguration configuration;

    public YamlReader(YamlConfiguration config) {
        configuration = config;
        lateInits = new ArrayList<>();
    }

    public abstract T readEach(String section, ProjectAddon addon);

    public final List<T> readAll(ProjectAddon addon) {
        List<T> objects = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection(key);
            if (section != null && section.getBoolean("lateInit", false)) {
                putLateInit(key);
                continue;
            }

            var object = readEach(key, addon);
            if (object != null) {
                objects.add(object);
            }
        }
        return objects;
    }

    protected void putLateInit(String key) {
        lateInits.add(key);
    }

    public void loadLateInits(ProjectAddon addon) {
        lateInits.forEach(s -> readEach(s, addon));

        lateInits.clear();
    }
}
