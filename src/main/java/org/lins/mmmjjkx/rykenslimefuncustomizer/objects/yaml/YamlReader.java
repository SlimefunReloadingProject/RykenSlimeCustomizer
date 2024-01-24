package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public abstract class YamlReader<T> {
    protected YamlConfiguration configuration;

    public YamlReader(YamlConfiguration config) {
        configuration = config;
    }

    public abstract List<T> readAll();

    public abstract T readEach(String section);

    public abstract void save(T t);
}
