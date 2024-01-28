package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.util.List;

public class ResearchReader extends YamlReader<Research> {
    public ResearchReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<Research> readAll(ProjectAddon addon) {
        return null;
    }

    @Override
    public Research readEach(String section, ProjectAddon addon) {
        return null;
    }
}
