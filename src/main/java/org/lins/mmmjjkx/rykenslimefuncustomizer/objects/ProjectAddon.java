package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.*;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.ArrayList;
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

    //groups.yml
    private List<CustomFatherItemGroup> fatherItemGroups = new ArrayList<>();
    private List<CustomItemGroup> itemGroups = new ArrayList<>();

    //menus.yml
    private List<CustomMenu> menus = new ArrayList<>();

    //geo.yml
    private List<CustomGeoResource> geoResources = new ArrayList<>();

    //items.yml
    private List<CustomPlaceableItem> placeableItems = new ArrayList<>();
    private List<CustomUnplaceableItem> items = new ArrayList<>();

    @Nullable
    public ItemGroup getItemGroup(String id) {
        ItemGroup ig = CommonUtils.getIf(itemGroups, i -> i.getKey().getKey().equalsIgnoreCase(id));
        if (ig == null) {
            ig = CommonUtils.getIf(fatherItemGroups, i -> i.getKey().getKey().equalsIgnoreCase(id));
        }
        return ig;
    }
}
