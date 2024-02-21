package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;

public class SimpleMachineReader extends YamlReader<SlimefunItem> {
    public SimpleMachineReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public SlimefunItem readEach(String section, ProjectAddon addon) {
        return null;
    }
}
