package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.*;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item.GeoResourceReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item.ItemReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item.MobDropsReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine.*;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.Constants;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectAddonLoader {
    private final Map<String, File> ids;
    private final File file;

    public ProjectAddonLoader(File dir, Map<String, File> ids) {
        Validate.notNull(dir, "File cannot be null!");
        Validate.isTrue(dir.isDirectory(), "File must be a directory!");

        this.file = dir;
        this.ids = ids;
    }

    @Nullable
    public ProjectAddon load() {
        ProjectAddon addon = null;
        YamlConfiguration info = doFileLoad(file, Constants.INFO_FILE);
        if (info.contains("name") && info.contains("version") && info.contains("id")) {
            String name = info.getString("name");
            String version = info.getString("version", "1.0");
            String id = info.getString("id", ""); //checked in ProjectAddonManager
            String description = info.getString("description", "");
            List<String> depends = new ArrayList<>();
            List<String> pluginDepends = new ArrayList<>();

            if (name == null || name.isBlank()) {
                ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目名称，导致此附属无法加载！");
                return addon;
            }

            if (info.contains("depends")) {
                depends = info.getStringList("depends");
                if (!RykenSlimefunCustomizer.addonManager.isLoaded(depends.toArray(new String[0]))) {
                    boolean loadResult = loadDependencies(depends);
                    if (!loadResult) {
                        ExceptionHandler.handleError("在名称为 " + name + " 的附属(附属id：" + id + ")中需要依赖项 " + depends + "，由于部分依赖项在加载时出错或未安装，导致此附属无法加载！");
                        return addon;
                    }
                }
            }

            if (info.contains("pluginDepends")) {
                pluginDepends = info.getStringList("pluginDepends");
                for (String pluginDepend : pluginDepends) {
                    if (!Bukkit.getPluginManager().isPluginEnabled(pluginDepend)) {
                        ExceptionHandler.handleError("在名称为 "+ name + " 的附属(附属id："+id+")中需要依赖项 "+ depends +"，由于部分依赖项在加载时出错或未安装，导致此附属无法加载！");
                        return addon;
                    }
                }
            }
            addon = new ProjectAddon(id, name, version, pluginDepends, depends, description, file);
        } else {
            ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目信息，导致此附属无法加载！");
            return addon;
        }

        YamlConfiguration groups = doFileLoad(file, Constants.GROUPS_FILE);
        ItemGroupReader reader = new ItemGroupReader(groups);
        addon.setItemGroups(reader.readAll(addon));
        //
        YamlConfiguration recipeTypes = doFileLoad(file, Constants.RECIPE_TYPES_FILE);
        RecipeTypesReader recipeTypesReader = new RecipeTypesReader(recipeTypes);
        addon.setRecipeTypes(recipeTypesReader.readAll(addon));

        RecipeTypeMap.pushRecipeType(addon.getRecipeTypes());
        //
        YamlConfiguration items = doFileLoad(file, Constants.ITEMS_FILE);
        ItemReader itemReader = new ItemReader(items);
        addon.setItems(itemReader.readAll(addon));
        //
        YamlConfiguration geo_resources = doFileLoad(file, Constants.GEO_RES_FILE);
        GeoResourceReader resourceReader = new GeoResourceReader(geo_resources);
        addon.setGeoResources(resourceReader.readAll(addon));
        //
        YamlConfiguration mob_drops = doFileLoad(file, Constants.MOB_DROPS_FILE);
        MobDropsReader mobDropsReader = new MobDropsReader(mob_drops);
        addon.setMobDrops(mobDropsReader.readAll(addon));
        //
        /////////////////
        YamlConfiguration menus = doFileLoad(file, Constants.MENUS_FILE);
        MenuReader menuReader = new MenuReader(menus);
        addon.setMenus(menuReader.readAll(addon));
        /////////////////
        YamlConfiguration machines = doFileLoad(file, Constants.MACHINES_FILE);
        MachineReader machineReader = new MachineReader(machines);
        addon.setMachines(machineReader.readAll(addon));
        //
        YamlConfiguration generators = doFileLoad(file, Constants.GENERATORS_FILE);
        GeneratorReader generatorReader = new GeneratorReader(generators);
        addon.setGenerators(generatorReader.readAll(addon));
        //
        YamlConfiguration solarGenerators = doFileLoad(file, Constants.SOLAR_GENERATORS_FILE);
        SolarGeneratorReader solarGeneratorReader = new SolarGeneratorReader(solarGenerators);
        addon.setSolarGenerators(solarGeneratorReader.readAll(addon));
        //
        YamlConfiguration materialGenerators = doFileLoad(file, Constants.MATERIAL_GENERATORS_FILE);
        MaterialGeneratorReader materialGeneratorReader = new MaterialGeneratorReader(materialGenerators);
        addon.setMaterialGenerators(materialGeneratorReader.readAll(addon));
        //
        YamlConfiguration recipeMachines = doFileLoad(file, Constants.RECIPE_MACHINES_FILE);
        RecipeMachineReader recipeMachineReader = new RecipeMachineReader(recipeMachines);
        addon.setRecipeMachines(recipeMachineReader.readAll(addon));
        //
        YamlConfiguration multiBlockMachines = doFileLoad(file, Constants.MULTI_BLOCK_MACHINES_FILE);
        MultiBlockMachineReader multiBlockMachineReader = new MultiBlockMachineReader(multiBlockMachines);
        addon.setMultiBlockMachines(multiBlockMachineReader.readAll(addon));
        //////////////////////////
        YamlConfiguration researches = doFileLoad(file, Constants.RESEARCHES_FILE);
        ResearchReader researchReader = new ResearchReader(researches);
        addon.setResearches(researchReader.readAll(addon));

        //late inits
        reader.loadLateInits(addon);
        itemReader.loadLateInits(addon);
        resourceReader.loadLateInits(addon);
        mobDropsReader.loadLateInits(addon);
        menuReader.loadLateInits(addon);
        machineReader.loadLateInits(addon);
        generatorReader.loadLateInits(addon);
        solarGeneratorReader.loadLateInits(addon);
        materialGeneratorReader.loadLateInits(addon);
        recipeMachineReader.loadLateInits(addon);
        multiBlockMachineReader.loadLateInits(addon);
        researchReader.loadLateInits(addon);

        return addon;
    }

    private boolean loadDependencies(List<String> depends) {
        for (String dependency : depends) {
            if (ids.containsKey(dependency)) {
                File dependencyFile = ids.get(dependency);
                ProjectAddonLoader loader = new ProjectAddonLoader(dependencyFile, ids);
                ProjectAddon addon = loader.load();
                if (addon != null) {
                    RykenSlimefunCustomizer.addonManager.pushProjectAddon(addon);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private YamlConfiguration doFileLoad(File dir, String file) {
        File dest = new File(dir, file);
        if (!dest.exists()) {
            return new YamlConfiguration();
        }
        return YamlConfiguration.loadConfiguration(dest);
    }
}
