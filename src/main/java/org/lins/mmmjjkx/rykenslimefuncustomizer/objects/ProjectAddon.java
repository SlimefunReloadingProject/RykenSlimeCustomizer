package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter(AccessLevel.PACKAGE)
public class ProjectAddon {
    //info.yml
    private @NotNull final String addonId;
    private @NotNull final String addonName;
    private @NotNull final String addonVersion;
    private @NotNull final List<String> pluginDepends;
    private @NotNull final List<String> depends;

    private @NotNull final File scriptsFolder;

    //groups.yml
    private List<ItemGroup> itemGroups = new ArrayList<>();

    //menus.yml
    private List<CustomMenu> menus = new ArrayList<>();

    //geo_resources.yml
    private List<CustomGeoResource> geoResources = new ArrayList<>();

    //items.yml
    private List<CustomItem> items = new ArrayList<>();

    //machines.yml
    private List<AbstractEmptyMachine> machines = new ArrayList<>();

    //researches.yml
    private List<Research> researches = new ArrayList<>();
}
