package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;

public abstract class RKYamlSection<T> {
    protected ConfigurationSection section;

    public RKYamlSection(ConfigurationSection section) {
        this.section = section;
    }

    public abstract T read();

    public abstract void save(T t);
}
