package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import com.google.common.annotations.Beta;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.io.File;
import java.io.IOException;

//bruh
@Beta
public class CustomAddonLanguage {
    private final ProjectAddon addon;
    private final YamlConfiguration languageConfig;

    public CustomAddonLanguage(ProjectAddon addon) {
        this.addon = addon;
        this.languageConfig = new YamlConfiguration();

        setupLanguage();
    }

    private void setupLanguage() {
        String language = RykenSlimefunCustomizer.INSTANCE.getConfig().getString("language");
        File languageFileFolder = new File(addon.getFolder(), "languages");
        File languageFile = new File(languageFileFolder, language + ".yml");
        File defaultLanguageFile = new File(languageFileFolder, addon.getDefaultLanguage() + ".yml");

        if (languageFile.exists()) {
            try {
                languageConfig.load(languageFile);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (defaultLanguageFile.exists()) {
                try {
                    languageConfig.load(defaultLanguageFile);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String get(String path) {
        return languageConfig.getString(path, "");
    }
}
