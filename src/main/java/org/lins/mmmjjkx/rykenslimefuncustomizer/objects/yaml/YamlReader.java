package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.util.List;

public abstract class YamlReader<T> {
    protected YamlConfiguration configuration;

    public YamlReader(YamlConfiguration config) {
        configuration = config;
    }

    public abstract List<T> readAll(ProjectAddon addon);

    public abstract T readEach(String section, ProjectAddon addon);

    public abstract void save(T t);
}
