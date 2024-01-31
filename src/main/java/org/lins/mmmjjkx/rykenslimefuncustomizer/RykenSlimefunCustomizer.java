package org.lins.mmmjjkx.rykenslimefuncustomizer;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.commands.MainCommand;

public final class RykenSlimefunCustomizer extends JavaPlugin implements SlimefunAddon {
    public static RykenSlimefunCustomizer INSTANCE;
    public static ProjectAddonManager addonManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        addonManager = new ProjectAddonManager();

        saveDefaultConfig();
        saveConfig();

        addonManager.setup(this);

        getCommand("rykenslimecustomizer").setExecutor(new MainCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void reload() {
        INSTANCE.reloadConfig();
        addonManager.reload(INSTANCE);
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
