package org.lins.mmmjjkx.rykenslimefuncustomizer;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptCreator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.commands.MainCommand;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.ScriptCreators;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class RykenSlimefunCustomizer extends JavaPlugin implements SlimefunAddon {
    private static boolean runtime = false;

    public static RykenSlimefunCustomizer INSTANCE;
    public static ProjectAddonManager addonManager;

    @Override
    public void onLoad() {
        ScriptCreators.pushScriptCreator(new JavaScriptCreator());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");

        INSTANCE = this;
        addonManager = new ProjectAddonManager();

        saveDefaultConfig();
        saveConfig();

        if (getConfig().getBoolean("saveExample")) {
            saveExample();
        }

        Objects.requireNonNull(getCommand("rykenslimecustomizer")).setExecutor(new MainCommand());

        addonManager.setup(this);

        ExceptionHandler.info("RykenSlimeCustomizer加载成功！");

        getServer().getScheduler().runTaskLater(this, () -> runtime = true, 1);
    }

    public static boolean updatePreReleaseVersions() {
        return INSTANCE.getConfig().getBoolean("update.pre-releases", false);
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

    public static boolean allowUpdate(String prjId) {
        if (runtime) return false;

        return INSTANCE.getConfig().getBoolean("update.auto") &&
                !INSTANCE.getConfig().getStringList("update.blocks").contains(prjId);
    }

    public static void saveExample() {
        String head = "addons/example/info.yml";

        String filePath = new File(INSTANCE.getDataFolder(),head).getAbsolutePath();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            INSTANCE.saveResource(head, true);
        }
    }
}
