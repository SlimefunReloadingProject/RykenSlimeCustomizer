package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class GeoResourceReader extends YamlReader<CustomGeoResource> {
    public GeoResourceReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CustomGeoResource> readAll() {
        List<CustomGeoResource> resources = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var geo = readEach(key);
            if (geo != null) {
                resources.add(geo);
            }
        }
        return resources;
    }

    @Override
    public CustomGeoResource readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section != null) {
            String id = section.getString("id");
            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String igId = section.getString("item_group");
            ConfigurationSection item = configuration.getConfigurationSection("item");
            ItemStack stack = CommonUtils.readItem(item);

        }
        return null;
    }

    @Override
    public void save(CustomGeoResource customGeoResource) {

    }

}