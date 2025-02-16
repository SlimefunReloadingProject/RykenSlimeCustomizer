package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.ScriptableEventListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomAddonConfig;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomArmorPiece;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomFood;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts.CustomMobDrop;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.*;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.DropFromBlock;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.RecipeTypeMap;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(AccessLevel.PACKAGE)
public final class ProjectAddon {
    private boolean markAsDepend = false;

    // info.yml
    private @NotNull final String addonId;
    private @NotNull final String addonName;
    private @NotNull final String addonVersion;
    private @NotNull final List<String> pluginDepends;
    private @NotNull final List<String> depends;
    private @NotNull final String description;
    private @NotNull final List<String> authors;
    private @NotNull final File folder;
    //
    private @NotNull final Map<String, SlimefunItemStack> preloadItems = new HashMap<>();
    //
    private @Nullable String githubRepo;
    private @Nullable String downloadZipName;
    private @Nullable String idPattern;
    //
    private @Nullable CustomAddonConfig config;
    //
    private @Nullable ScriptableEventListener eventListener;
    //
    private List<JavaScriptEval> scriptEvals = new ArrayList<>();
    // groups.yml
    private List<ItemGroup> itemGroups = new ArrayList<>();
    // menus.yml
    private List<CustomMenu> menus = new ArrayList<>();
    // geo_resources.yml
    private List<GEOResource> geoResources = new ArrayList<>();
    // items.yml
    private List<SlimefunItem> items = new ArrayList<>();
    // machines.yml
    private List<AbstractEmptyMachine<?>> machines = new ArrayList<>();
    // researches.yml
    private List<Research> researches = new ArrayList<>();
    // generators.yml
    private List<CustomGenerator> generators = new ArrayList<>();
    // mat_generators.yml
    private List<CustomMaterialGenerator> materialGenerators = new ArrayList<>();
    // recipe_machines.yml
    private List<CustomRecipeMachine> recipeMachines = new ArrayList<>();
    // mb_machines.yml
    private List<CustomMultiBlockMachine> multiBlockMachines = new ArrayList<>();
    // solar_generators.yml
    private List<CustomSolarGenerator> solarGenerators = new ArrayList<>();
    // mob_drops.yml
    private List<CustomMobDrop> mobDrops = new ArrayList<>();
    // capacitors.yml
    private List<Capacitor> capacitors = new ArrayList<>();
    // recipe_types.yml
    private List<RecipeType> recipeTypes = new ArrayList<>();
    // simple_machines.yml
    private List<SlimefunItem> simpleMachines = new ArrayList<>();
    // foods.yml
    private List<CustomFood> foods = new ArrayList<>();
    // armors.yml
    private List<List<CustomArmorPiece>> armors = new ArrayList<>();
    // supers.yml
    private List<SlimefunItem> supers = new ArrayList<>();
    // template_machines.yml
    private List<CustomTemplateMachine> templateMachines = new ArrayList<>();
    // linked_recipe_machines.yml
    private List<CustomLinkedRecipeMachine> linkedRecipeMachines = new ArrayList<>();
    // workbenches.yml
    private List<CustomWorkbench> workbenches = new ArrayList<>();

    public File getScriptsFolder() {
        File scripts = new File(folder, "scripts");
        if (!scripts.exists()) {
            scripts.mkdirs();
        }
        return scripts;
    }

    public File getSavedItemsFolder() {
        File savedItems = new File(folder, "saveditems");
        if (!savedItems.exists()) {
            savedItems.mkdirs();
        }
        return savedItems;
    }

    public void unregister() {
        scriptEvals.forEach(JavaScriptEval::close);
        itemGroups.forEach(ig -> Slimefun.getRegistry().getAllItemGroups().remove(ig));
        menus.forEach(m -> Slimefun.getRegistry().getMenuPresets().remove(m.getID()));
        items.forEach(this::unregisterItem);
        mobDrops.forEach(md -> {
            unregisterItem(md);
            Slimefun.getRegistry().getMobDrops().get(md.getEntityType()).removeAll(md.getDrops());
        });
        capacitors.forEach(this::unregisterItem);
        foods.forEach(this::unregisterItem);
        machines.forEach(this::unregisterItem);
        solarGenerators.forEach(this::unregisterItem);
        generators.forEach(this::unregisterItem);
        geoResources.forEach(g -> {
            if (g instanceof CustomGeoResource cgr) {
                unregisterItem(cgr);
            }
            unregisterGeo(g);
        });
        materialGenerators.forEach(this::unregisterItem);
        recipeMachines.forEach(this::unregisterItem);
        multiBlockMachines.forEach(this::unregisterItem);
        simpleMachines.forEach(this::unregisterItem);
        armors.forEach(l -> l.forEach(this::unregisterItem));
        supers.forEach(this::unregisterItem);
        templateMachines.forEach(this::unregisterItem);
        linkedRecipeMachines.forEach(this::unregisterItem);
        workbenches.forEach(this::unregisterItem);

        recipeTypes.forEach(r -> RecipeTypeMap.removeRecipeTypes(r.getKey().getKey()));

        // scripts.clear();
        scriptEvals.clear();
        items.clear();
        machines.clear();
        itemGroups.clear();
        menus.clear();
        geoResources.clear();
        generators.clear();
        materialGenerators.clear();
        recipeMachines.clear();
        multiBlockMachines.clear();
        capacitors.clear();
        solarGenerators.clear();
        mobDrops.clear();
        recipeTypes.clear();
        simpleMachines.clear();
        foods.clear();
        armors.clear();
        supers.clear();
        templateMachines.clear();
        linkedRecipeMachines.clear();
        workbenches.clear();
        preloadItems.clear();

        DropFromBlock.unregisterAddonDrops(this);

        if (config != null) {
            if (config.onReloadHandler() != null) {
                config.onReloadHandler().close();
            }
        }
    }

    private void unregisterItem(SlimefunItem item) {
        if (item instanceof Radioactive) {
            Slimefun.getRegistry().getRadioactiveItems().remove(item);
        }

        Slimefun.getRegistry().getTickerBlocks().remove(item.getId());
        Slimefun.getRegistry().getEnabledSlimefunItems().remove(item);

        Slimefun.getRegistry().getSlimefunItemIds().remove(item.getId());
        Slimefun.getRegistry().getAllSlimefunItems().remove(item);
    }

    private void unregisterGeo(GEOResource resource) {
        Slimefun.getRegistry().getGEOResources().remove(resource.getKey());
    }

    public String getId(@Nullable String configuredId, @Nullable String id_alias) {
        String id = configuredId;
        if (id_alias != null) {
            id = id_alias;
        }
        if (id != null) {
            if (idPattern != null) {
                // 当前使用的 id 可能是正常引用的 id，也可能是 idPattern 格式化后的 id
                // 如果找不到已初始化的 item，则尝试用 idPattern 格式化 id
                SlimefunItem item = SlimefunItem.getById(id);
                if (item == null) {
                    id = idPattern.replaceAll("%0", id);
                }
            }

            return id.toUpperCase();
        } else {
            ExceptionHandler.handleError("无法获取id");
            ExceptionHandler.handleError("configuredId: " + configuredId == null ? "null" : configuredId);
            ExceptionHandler.handleError("id_alias: " + id_alias == null ? "null" : id_alias);
            ExceptionHandler.handleError("idPattern: " + idPattern == null ? "null" : idPattern);
            String randomId = "RSC_UNKNOWN_ID_" + ((int) (Math.random() * 1_000_000));
            ExceptionHandler.handleError("分配随机id");
            ExceptionHandler.handleError("randomId: " + randomId);
            return randomId;
        }
    }
}
