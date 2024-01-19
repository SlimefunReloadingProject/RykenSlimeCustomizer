package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;

import java.util.List;

public class MenusSection extends RKYamlSection<List<CustomMenu>> {
    public MenusSection(ConfigurationSection section) {
        super(section);
    }

    @Override
    public List<CustomMenu> read() {
        return null;
    }

    @Override
    public void save(List<CustomMenu> customMenus) {

    }
}
