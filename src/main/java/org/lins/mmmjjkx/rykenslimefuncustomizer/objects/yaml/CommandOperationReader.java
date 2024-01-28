package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record.CommandOperation;

import java.util.ArrayList;
import java.util.List;

public class CommandOperationReader extends YamlReader<CommandOperation>{
    public CommandOperationReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CommandOperation> readAll(ProjectAddon addon) {
        List<CommandOperation> list = new ArrayList<>();
        for (String id : configuration.getKeys(false)) {
            list.add(readEach(id, addon));
        }
        return list;
    }

    @Override
    public CommandOperation readEach(String section, ProjectAddon addon) {
        ConfigurationSection item = configuration.getConfigurationSection(section);
        if (item != null) {
            List<String> actions = item.getStringList("actions");
            return new CommandOperation(section, actions);
        }
        return new CommandOperation(section, List.of());
    }
}
