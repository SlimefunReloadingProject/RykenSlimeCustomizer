package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.GeoResourceReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.ItemGroupReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.ItemReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.MenuReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectAddonLoader {
    public static final String INFO_FILE = "info.yml";
    public static final String MENUS_FILE = "menus.yml";
    public static final String ITEMS_FILE = "items.yml";
    public static final String GROUPS_FILE = "groups.yml";
    public static final String GEO_RES_FILE = "geo_resources.yml";
    private final File file;

    public ProjectAddonLoader(File dir) {
        Validate.notNull(dir, "File cannot be null!");
        Validate.isTrue(dir.isDirectory(), "File must be a directory!");

        this.file = dir;
    }

    @Nullable
    public ProjectAddon load() {
        ProjectAddon addon = null;
        YamlConfiguration info = YamlConfiguration.loadConfiguration(new File(file, INFO_FILE));
        if (info.contains("name") && info.contains("version") && info.contains("id")) {
            String name = info.getString("name");
            String version = info.getString("version", "1.0");
            String id = info.getString("id");
            List<String> depends = new ArrayList<>();
            List<String> pluginDepends = new ArrayList<>();

            if (name == null || name.isBlank()) {
                ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目名称，导致此附属无法加载！");
                return addon;
            }

            if (id == null || id.isBlank()) {
                ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目ID，导致此附属无法加载！");
                return addon;
            }

            if (info.contains("depends")) {
                depends = info.getStringList("depends");
                if (!RykenSlimefunCustomizer.addonManager.isLoaded(depends.toArray(new String[0]))) {
                    ExceptionHandler.handleError("在名称为 "+ name + " 的附属(附属id："+id+")中需要依赖项 "+ depends +"，由于部分依赖项在加载时出错或未安装，导致此附属无法加载！");
                    return addon;
                }
            }

            if (info.contains("pluginDepends")) {
                pluginDepends = info.getStringList("pluginDepends");
                for (String pluginDepend : pluginDepends) {
                    if (!Bukkit.getPluginManager().isPluginEnabled(pluginDepend)) {
                        ExceptionHandler.handleError("在名称为 "+ name + " 的附属(附属id："+id+")中需要依赖项 "+ depends +"，由于部分依赖项在加载时出错或未安装，导致此附属无法加载！");
                        return addon;
                    }
                }
            }

            addon = new ProjectAddon(name, version, id, pluginDepends, depends);
        } else {
            ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目信息，导致此附属无法加载！");
            return addon;
        }

        YamlConfiguration groups = YamlConfiguration.loadConfiguration(new File(file, GROUPS_FILE));
        ItemGroupReader reader = new ItemGroupReader(groups);
        addon.setItemGroups(reader.readAll(addon));
        //
        YamlConfiguration items = YamlConfiguration.loadConfiguration(new File(file, ITEMS_FILE));
        ItemReader itemReader = new ItemReader(items);
        addon.setItems(itemReader.readAll(addon));
        //
        YamlConfiguration geo_resources = YamlConfiguration.loadConfiguration(new File(file, GEO_RES_FILE));
        GeoResourceReader resourceReader = new GeoResourceReader(geo_resources);
        addon.setGeoResources(resourceReader.readAll(addon));
        //
        YamlConfiguration menus = YamlConfiguration.loadConfiguration(new File(file, MENUS_FILE));
        MenuReader menuReader = new MenuReader(menus);
        addon.setMenus(menuReader.readAll(addon));
        return addon;
    }
}
