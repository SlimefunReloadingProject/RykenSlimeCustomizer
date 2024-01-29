package org.lins.mmmjjkx.rykenslimefuncustomizer;

import org.bukkit.plugin.Plugin;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddonLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectAddonManager {
    private final Map<String, ProjectAddon> projectAddons = new HashMap<>();

    public ProjectAddonManager(RykenSlimefunCustomizer instance) {
        setup(instance);
    }

    private void setup(Plugin inst) {
        File addons = new File(inst.getDataFolder(), "addons");
        if (!addons.exists()) {
            addons.mkdirs();
            return;
        }

        File[] folders = addons.listFiles();
        if (folders == null) return;

        for (File folder : folders) {
            ProjectAddonLoader loader = new ProjectAddonLoader(folder);
            ProjectAddon addon = loader.load();
            if (addon != null) {
                projectAddons.put(addon.getAddonId(), addon);
            }
        }
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
}
