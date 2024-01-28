package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

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

    @Nullable
    public ItemGroup getItemGroup(String id) {
        return CommonUtils.getIf(itemGroups, i -> i.getKey().getKey().equalsIgnoreCase(id));
    }
}
