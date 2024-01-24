package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ItemGroupReader extends YamlReader<ItemGroup> {
    public ItemGroupReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<ItemGroup> readAll() {
        return null;
    }

    @Override
    public void save(ItemGroup group) {

    }
}
