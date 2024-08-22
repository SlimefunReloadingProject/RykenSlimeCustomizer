package org.lins.mmmjjkx.rykenslimefuncustomizer;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddonLoader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.RecipeTypeMap;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.Constants;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public final class ProjectAddonManager {
    public static File ADDONS_DIRECTORY;
    private final Map<String, ProjectAddon> projectAddons = new HashMap<>();

    @Getter
    private final Map<String, File> projectIds = new HashMap<>();

    public ProjectAddonManager() {
        ADDONS_DIRECTORY = new File(RykenSlimefunCustomizer.INSTANCE.getDataFolder(), "addons");
    }

    public void pushProjectAddon(ProjectAddon addon) {
        Validate.notNull(addon, "addon");
        if (!projectAddons.containsKey(addon.getAddonId())) {
            projectIds.put(addon.getAddonId(), addon.getFolder());
            projectAddons.put(addon.getAddonId(), addon);
        }
    }

    public void removeProjectAddon(ProjectAddon addon) {
        Validate.notNull(addon, "addon");

        projectIds.remove(addon.getAddonId());
        projectAddons.remove(addon.getAddonId());
    }

    public void setup(Plugin inst) {
        File addons = new File(inst.getDataFolder(), "addons");
        if (!addons.exists()) {
            addons.mkdirs();
            return;
        }

        File[] folders = addons.listFiles();
        if (folders == null) return;

        List<String> skip = new ArrayList<>();

        for (File folder : folders) {
            File info = new File(folder, Constants.INFO_FILE);
            if (!info.exists()) {
                ExceptionHandler.handleError("A folder called " + folder.getName()
                        + " contains invalid addon information，The addon will not be loaded！");
                skip.add(folder.getName());
                continue;
            }
            YamlConfiguration infoConfig = YamlConfiguration.loadConfiguration(info);
            String id = infoConfig.getString("id");
            if (id == null || id.isBlank()) {
                ExceptionHandler.handleError("A folder called " + folder.getName()
                        + " contains invalid addon information that does not have an ID，The addon will not be loaded！");
                skip.add(folder.getName());
                continue;
            }

            if (projectIds.containsKey(id)) {
                ProjectAddon addon = projectAddons.get(id);
                if (addon == null) {
                    ExceptionHandler.handleError("Cannot load addon with ID " + id
                            + " normally! Please check the addon's configuration file！");
                    continue;
                }

                if (addon.isMarkAsDepend()) {
                    continue;
                }

                ExceptionHandler.handleError("A folder called " + folder.getName() + "has the same ID with "
                        + addon.getFolder().getName() + "，The addon will not be loaded！");
                skip.add(folder.getName());
                continue;
            }

            projectIds.put(id, folder);
        }

        for (File folder : folders) {
            if (skip.contains(folder.getName())) continue;

            YamlConfiguration infoConfig = YamlConfiguration.loadConfiguration(new File(folder, Constants.INFO_FILE));
            String id = infoConfig.getString("id");
            if (projectAddons.containsKey(id)) {
                continue;
            }

            ProjectAddonLoader loader = new ProjectAddonLoader(folder, projectIds);
            ProjectAddon addon = loader.load();
            if (addon != null) {
                projectAddons.put(addon.getAddonId(), addon);
            }
        }
    }

    public void reload(Plugin plugin) {
        for (ProjectAddon addon : projectAddons.values()) {
            addon.unregister();
        }

        projectAddons.clear();
        projectIds.clear();

        RecipeTypeMap.clearRecipeTypes();

        setup(plugin);
    }

    public boolean isLoaded(String id) {
        return projectAddons.containsKey(id);
    }

    public boolean isLoaded(String... ids) {
        for (String id : ids) {
            if (!isLoaded(id)) {
                return false;
            }
        }
        return true;
    }

    public ProjectAddon get(String id) {
        return projectAddons.get(id);
    }

    public List<ProjectAddon> getAllValues() {
        return new ArrayList<>(projectAddons.values());
    }

    public File getAddonFolder(String id) {
        return projectIds.get(id);
    }
}
