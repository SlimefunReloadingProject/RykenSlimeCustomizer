package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.AbstractEmptyMachine;

import java.util.ArrayList;
import java.util.List;

public class MachineReader extends YamlReader<AbstractEmptyMachine> {
    public MachineReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<AbstractEmptyMachine> readAll(ProjectAddon addon) {
        List<AbstractEmptyMachine> machines = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var machine = readEach(key, addon);
            if (machine != null) {
                machines.add(machine);
            }
        }
        return machines;
    }

    @Override
    public AbstractEmptyMachine readEach(String section, ProjectAddon addon) {
        return null;
    }
}
