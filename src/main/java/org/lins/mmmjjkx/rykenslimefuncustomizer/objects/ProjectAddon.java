package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomPlaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomUnplaceableItem;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter(AccessLevel.PACKAGE)
public class ProjectAddon {
    //info.yml
    private @NotNull final String addonId;
    private @NotNull final String addonName;
    private @NotNull final String addonVersion;
    private @NotNull final List<String> depends;

    //menus.yml
    private List<CustomMenu> menus;

    //geo.yml
    private List<CustomGeoResource> geoResources;

    //items.yml
    private List<CustomPlaceableItem> placeableItems;
    private List<CustomUnplaceableItem> items;
}
