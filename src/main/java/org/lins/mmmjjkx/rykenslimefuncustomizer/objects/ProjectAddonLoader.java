package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.MenusSection;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
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

            addon = new ProjectAddon(name, version, id);

            if ((name == null || name.isBlank()) && (id == null || id.isBlank())) {
                ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目信息，导致此附属无法加载！");
                return addon;
            }
        } else {
            ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目信息，导致此附属无法加载！");
            return addon;
        }

        YamlConfiguration menus = YamlConfiguration.loadConfiguration(new File(file, MENUS_FILE));

        List<CustomMenu> menuList = new MenusSection(menus).read();//TODO implement

        return addon;
    }
}
