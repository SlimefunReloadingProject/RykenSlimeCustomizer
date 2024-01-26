package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectAddonLoader {
    private final File file;
    private final String INFO_FILE = "info.yml";
    private final String MENUS_FILE = "menus.yml";
    private final String ITEMS_FILE = "items.yml";


    public ProjectAddonLoader(File files) {
        Validate.notNull(files, "File cannot be null!");
        Validate.isTrue(files.isDirectory(), "File must be a directory!");

        this.file = files;
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

        YamlConfiguration menus = YamlConfiguration.loadConfiguration(new File(file, MENUS_FILE));


        return addon;
    }
}
