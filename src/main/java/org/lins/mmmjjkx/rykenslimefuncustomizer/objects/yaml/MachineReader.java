package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.AbstractEmptyMachine;

import java.util.List;

public class MachineReader extends YamlReader<AbstractEmptyMachine> {
    public MachineReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<AbstractEmptyMachine> readAll(ProjectAddon addon) {
        return null;
    }

    @Override
    public AbstractEmptyMachine readEach(String section, ProjectAddon addon) {
        return null;
    }
}
