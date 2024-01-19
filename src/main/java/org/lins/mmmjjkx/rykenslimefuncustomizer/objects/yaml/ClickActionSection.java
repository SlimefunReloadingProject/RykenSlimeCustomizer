package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.configuration.ConfigurationSection;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record.CommandOperation;

import java.util.List;
import java.util.Map;

public class ClickActionSection extends RKYamlSection<Map<ClickAction, List<CommandOperation>>> {
    public ClickActionSection(ConfigurationSection section) {
        super(section);
    }

    @Override
    public Map<ClickAction, List<CommandOperation>> read() {
        return null;
    }

    @Override
    public void save(Map<ClickAction, List<CommandOperation>> clickActionListMap) {

    }
}
