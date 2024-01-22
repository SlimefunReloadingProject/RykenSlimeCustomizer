package org.lins.mmmjjkx.rykenslimefuncustomizer;

import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectAddonManager {
    private final Map<String, ProjectAddon> projectAddons = new HashMap<>();

    public ProjectAddonManager(RykenSlimefunCustomizer instance) {

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
