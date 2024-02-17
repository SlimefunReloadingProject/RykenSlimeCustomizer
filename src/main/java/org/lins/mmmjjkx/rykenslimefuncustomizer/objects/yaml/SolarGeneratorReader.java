package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

public class SolarGeneratorReader extends YamlReader<SolarGenerator> {
    public SolarGeneratorReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public SolarGenerator readEach(String section, ProjectAddon addon) {
        return null;
    }
}
