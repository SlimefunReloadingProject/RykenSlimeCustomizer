package org.lins.mmmjjkx.rykenslimefuncustomizer;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.lins.mmmjjkx.rykenslimefuncustomizer.commands.MenuPreview;

import java.io.File;

public final class RykenSlimefunCustomizer extends JavaPlugin implements SlimefunAddon {
    public static RykenSlimefunCustomizer INSTANCE;
    public static ProjectAddonManager addonManager;

    @TestOnly
    public RykenSlimefunCustomizer(JavaPluginLoader loader, PluginDescriptionFile description, File file, File file2) {
        super(loader, description, file, file2);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        addonManager = new ProjectAddonManager(this);

        saveDefaultConfig();
        saveConfig();

        getCommand("menupreview").setExecutor(new MenuPreview());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
}
