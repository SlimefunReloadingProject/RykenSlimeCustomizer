package org.lins.mmmjjkx.rykenslimefuncustomizer;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.commands.MainCommand;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class RykenSlimefunCustomizer extends JavaPlugin implements SlimefunAddon {
    private static final String[] resourcePaths = {
            "info.yml",
            "menus.yml",
            "groups.yml",
            "machines.yml",
            "researches.yml",
            "items.yml",
            "generators.yml",
            "mat_generators.yml",
            "mb_machines.yml",
            "recipe_machines.yml",
            "geo_resources.yml",
            "scripts/example_item_2.js",
            "scripts/example_machine.js",
            "scripts/example_machine_energy.js"
    };

    public static RykenSlimefunCustomizer INSTANCE;
    public static ProjectAddonManager addonManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        addonManager = new ProjectAddonManager();

        saveDefaultConfig();
        saveConfig();

        if (getConfig().getBoolean("saveExample")) {
            saveExample();
        }

        addonManager.setup(this);

        Objects.requireNonNull(getCommand("rykenslimecustomizer")).setExecutor(new MainCommand());

        getLogger().info("RykenSlimeCustomizer已启用!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("RykenSlimeCustomizer已卸载!");
    }

    public static void reload() {
        INSTANCE.reloadConfig();
        addonManager.reload(INSTANCE);
        
        if (INSTANCE.getConfig().getBoolean("saveExample")) {
            saveExample();
        }
    }

    @NotNull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/SlimefunReloadingProject/RykenSlimeCustomizer/issues";
    }

    public static void saveExample() {
        String head = "addons/example/";

        for (String resourcePath : resourcePaths) {
            String filePath = new File(INSTANCE.getDataFolder(),head + resourcePath).getAbsolutePath();
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                INSTANCE.saveResource(head + resourcePath, false);
            }
        }
    }
}
