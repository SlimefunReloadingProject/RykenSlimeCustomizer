package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.bytebuddy.ByteBuddy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.ProjectAddonManager;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.ScriptableEventListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomAddonConfig;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.RecipeTypeMap;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.ItemGroupReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.MenuReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.RecipeTypesReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.ResearchReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item.*;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine.*;
import org.lins.mmmjjkx.rykenslimefuncustomizer.update.GithubUpdater;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.Constants;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class ProjectAddonLoader {
    private final Map<String, File> ids;
    private final File file;

    public ProjectAddonLoader(File dir, Map<String, File> ids) {
        Validate.notNull(dir, "File cannot be null!");
        Validate.isTrue(dir.isDirectory(), "File must be a directory!");

        this.file = dir;
        this.ids = ids;
    }

    @Nullable public ProjectAddon load() {
        ProjectAddon addon;
        YamlConfiguration info = doFileLoad(file, Constants.INFO_FILE);

        ExceptionHandler.debugLog("开始读取文件夹 " + file.getName() + " 中的项目信息...");

        if (info.contains("name") && info.contains("version") && info.contains("id")) {
            String name = info.getString("name");
            String version = info.getString("version", "1.0");
            String id = info.getString("id", ""); // checked in ProjectAddonManager
            String description = info.getString("description", "");
            String downloadZipName = info.getString("downloadZipName", "");
            List<String> depends = new ArrayList<>();
            List<String> pluginDepends = new ArrayList<>();
            List<String> authors = info.getStringList("authors");
            String repo = info.getString("repo", "");

            if (!repo.isBlank()) {
                String[] split = repo.split("/");
                if (split.length == 2) {
                    if (RykenSlimefunCustomizer.allowUpdate(id)) {
                        boolean b = GithubUpdater.checkAndUpdate(version, split[0], split[1], id, file.getName());
                        if (b) {
                            YamlConfiguration info2 = doFileLoad(file, Constants.INFO_FILE);
                            if (!Objects.equals(info2.getString("version"), version)) {
                                return load(); // reload
                            }
                        }
                    }
                }
            }

            if (name == null || name.isBlank()) {
                ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目名称，导致此附属无法加载！");
                return null;
            }

            if (info.contains("depends")) {
                depends = info.getStringList("depends");
                if (!RykenSlimefunCustomizer.addonManager.isLoaded(depends.toArray(new String[0]))) {
                    boolean loadResult = loadDependencies(depends);
                    if (!loadResult) {
                        ExceptionHandler.handleError("在名称为 " + name + " 的附属(附属id：" + id + ")中需要依赖项 " + depends
                                + "，由于部分依赖项在加载时出错或未安装，导致此附属无法加载！");
                        return null;
                    }
                }
            }

            if (info.contains("pluginDepends")) {
                Set<String> unloadedPlugins = new HashSet<>();
                pluginDepends = info.getStringList("pluginDepends");
                for (String pluginDepend : pluginDepends) {
                    if (!Bukkit.getPluginManager().isPluginEnabled(pluginDepend)) {
                        unloadedPlugins.add(pluginDepend);
                    }
                }

                if (!unloadedPlugins.isEmpty()) {
                    StringBuilder message = new StringBuilder("在名称为 " + name + " 的附属(附属id：" + id + ")中需要插件依赖项 ");
                    for (String pluginDepend : pluginDepends) {
                        if (unloadedPlugins.contains(pluginDepend)) {
                            message.append("&c").append(pluginDepend).append("&r ");
                        } else {
                            message.append("&a").append(pluginDepend).append("&r ");
                        }
                    }
                    message.append("，由于部分依赖项在加载时出错或未安装，导致此附属无法加载！");
                    ExceptionHandler.handleError(message.toString());
                    return null;
                }
            }

            addon = new ProjectAddon(id, name, version, pluginDepends, depends, description, authors, file);

            if (!repo.isBlank()) {
                addon.setGithubRepo("https://github.com/" + repo);
            }

            if (!downloadZipName.isBlank()) {
                addon.setDownloadZipName(downloadZipName);
            }

            String scriptListener = info.getString("scriptListener", "");
            if (!scriptListener.isBlank()) {
                File file = new File(addon.getScriptsFolder(), scriptListener + ".js");
                if (file.exists()) {
                    JavaScriptEval eval = new JavaScriptEval(file, addon);

                    // First letter to uppercase
                    String listenerName = scriptListener.replaceFirst(
                            String.valueOf(scriptListener.charAt(0)),
                            String.valueOf(Character.toUpperCase(scriptListener.charAt(0))));

                    Class<? extends ScriptableEventListener> sel = new ByteBuddy().subclass(ScriptableEventListener.class)
                            .name("org.rykenslimefuncustomizer.addoncontents.listeners." + listenerName)
                            .make()
                            .load(getClass().getClassLoader())
                            .getLoaded();

                    try {
                        var listenerObj = (ScriptableEventListener) sel.getConstructors()[0].newInstance(eval);
                        Bukkit.getPluginManager().registerEvents(listenerObj, RykenSlimefunCustomizer.INSTANCE);

                        addon.setEventListener(listenerObj);
                        ExceptionHandler.info("成功注册附属 " + addon.getAddonId() + " 的监听脚本 " + file.getName() + "!");
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ExceptionHandler.handleWarning(
                            "无法找到附属 " + addon.getAddonId() + " 的对应监听脚本文件 " + file.getName() + "！");
                }
            }

            File customConfigFolder = new File(ProjectAddonManager.CONFIGS_DIRECTORY, id);
            YamlConfiguration customConfigYaml = doFileLoad(file, Constants.ADDON_CONFIG_FILE);
            if (!customConfigYaml.getKeys(false).isEmpty()) {
                File configFile = new File(file, Constants.ADDON_CONFIG_FILE);

                File customConfig = new File(customConfigFolder, "config.yml");
                if (!customConfigFolder.exists()) {
                    customConfigFolder.mkdirs();
                }

                if (!customConfig.exists()) {
                    try {
                        Files.copy(configFile.toPath(), customConfig.toPath());
                        customConfigYaml = doFileLoad(customConfigFolder, "config.yml");
                    } catch (IOException e) {
                        ExceptionHandler.handleError("无法复制配置文件 " + configFile.getName() + " 到 " + customConfigFolder.getName() + "，附属可能不按预期工作！", e);
                    }
                }

                File scriptHandler = new File(addon.getScriptsFolder(), "configHandler.js");
                ScriptEval eval = scriptHandler.exists() ? new JavaScriptEval(scriptHandler, addon) : null;
                CustomAddonConfig customConfigObj = new CustomAddonConfig(customConfig, customConfigYaml, eval);

                YamlConfiguration dest = YamlConfiguration.loadConfiguration(customConfig);
                CommonUtils.completeFile(customConfigYaml, dest);
                try {
                    dest.save(customConfig);
                } catch (IOException ignored) {
                }

                addon.setConfig(customConfigObj);
                customConfigObj.tryReload();
            }

            String idPattern = info.getString("idPattern");
            if (idPattern != null && !idPattern.isBlank()) {
                if (idPattern.contains("%0")) {
                    addon.setIdPattern(idPattern);
                } else {
                    ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的配置: idPattern，idPattern 必须包含 %0（原id）");
                }
            }
        } else {
            ExceptionHandler.handleError("在名称为 " + file.getName() + "的文件夹中有无效的项目信息，导致此附属无法加载！");
            return null;
        }

        ExceptionHandler.debugLog("读取完成，开始加载附属 " + addon.getAddonId() + " 中的内容...");

        YamlConfiguration groups = doFileLoad(file, Constants.GROUPS_FILE);
        ItemGroupReader groupReader = new ItemGroupReader(groups, addon);
        addon.setItemGroups(groupReader.readAll());

        YamlConfiguration recipeTypes = doFileLoad(file, Constants.RECIPE_TYPES_FILE);
        RecipeTypesReader recipeTypesReader = new RecipeTypesReader(recipeTypes, addon);
        addon.setRecipeTypes(recipeTypesReader.readAll());
        RecipeTypeMap.pushRecipeType(addon.getRecipeTypes());

        YamlConfiguration mob_drops = doFileLoad(file, Constants.MOB_DROPS_FILE);
        YamlConfiguration geo_resources = doFileLoad(file, Constants.GEO_RES_FILE);
        YamlConfiguration items = doFileLoad(file, Constants.ITEMS_FILE);
        YamlConfiguration armors = doFileLoad(file, Constants.ARMORS_FILE);
        YamlConfiguration capacitors = doFileLoad(file, Constants.CAPACITORS_FILE);
        YamlConfiguration foods = doFileLoad(file, Constants.FOODS_FILE);
        YamlConfiguration menus = doFileLoad(file, Constants.MENUS_FILE);
        YamlConfiguration machines = doFileLoad(file, Constants.MACHINES_FILE);
        YamlConfiguration generators = doFileLoad(file, Constants.GENERATORS_FILE);
        YamlConfiguration solarGenerators = doFileLoad(file, Constants.SOLAR_GENERATORS_FILE);
        YamlConfiguration materialGenerators = doFileLoad(file, Constants.MATERIAL_GENERATORS_FILE);
        YamlConfiguration recipeMachines = doFileLoad(file, Constants.RECIPE_MACHINES_FILE);
        YamlConfiguration simpleMachines = doFileLoad(file, Constants.SIMPLE_MACHINES_FILE);
        YamlConfiguration multiBlockMachines = doFileLoad(file, Constants.MULTI_BLOCK_MACHINES_FILE);
        YamlConfiguration supers = doFileLoad(file, Constants.SUPERS_FILE);
        YamlConfiguration templateMachines = doFileLoad(file, Constants.TEMPLATE_MACHINES_FILE);
        YamlConfiguration linkedRecipeMachines = doFileLoad(file, Constants.LINKED_RECIPE_MACHINES_FILE);
        YamlConfiguration workbenches = doFileLoad(file, Constants.WORKBENCHES_FILE);

        MobDropsReader mobDropsReader = new MobDropsReader(mob_drops, addon);
        GeoResourceReader resourceReader = new GeoResourceReader(geo_resources, addon);
        ItemReader itemReader = new ItemReader(items, addon);
        ArmorReader armorReader = new ArmorReader(armors, addon);
        CapacitorsReader capacitorsReader = new CapacitorsReader(capacitors, addon);
        FoodReader foodReader = new FoodReader(foods, addon);
        MenuReader menuReader = new MenuReader(menus, addon);
        MachineReader machineReader = new MachineReader(machines, addon);
        GeneratorReader generatorReader = new GeneratorReader(generators, addon);
        SolarGeneratorReader solarGeneratorReader = new SolarGeneratorReader(solarGenerators, addon);
        MaterialGeneratorReader materialGeneratorReader = new MaterialGeneratorReader(materialGenerators, addon);
        RecipeMachineReader recipeMachineReader = new RecipeMachineReader(recipeMachines, addon);
        SimpleMachineReader simpleMachineReader = new SimpleMachineReader(simpleMachines, addon);
        MultiBlockMachineReader multiBlockMachineReader = new MultiBlockMachineReader(multiBlockMachines, addon);
        SuperReader superReader = new SuperReader(supers, addon);
        TemplateMachineReader templateMachineReader = new TemplateMachineReader(templateMachines, addon);
        LinkedRecipeMachineReader linkedRecipeMachineReader = new LinkedRecipeMachineReader(linkedRecipeMachines, addon);
        WorkbenchReader workbenchReader = new WorkbenchReader(workbenches, addon);

        ExceptionHandler.debugLog("开始加载 " + file.getName() + " 中的物品内容...");

        mobDropsReader.preload();
        resourceReader.preload();
        itemReader.preload();
        armorReader.preload();
        capacitorsReader.preload();
        foodReader.preload();
        machineReader.preload();
        generatorReader.preload();
        solarGeneratorReader.preload();
        materialGeneratorReader.preload();
        recipeMachineReader.preload();
        simpleMachineReader.preload();
        multiBlockMachineReader.preload();
        superReader.preload();
        templateMachineReader.preload();
        linkedRecipeMachineReader.preload();
        workbenchReader.preload();

        ExceptionHandler.debugLog("开始注册 " + file.getName() + " 存放的内容...");

        addon.setMobDrops(mobDropsReader.readAll());
        addon.setGeoResources(resourceReader.readAll());
        addon.setItems(itemReader.readAll());
        addon.setArmors(armorReader.readAll());
        addon.setCapacitors(capacitorsReader.readAll());
        addon.setFoods(foodReader.readAll());
        addon.setMenus(menuReader.readAll());
        addon.setMachines(machineReader.readAll());
        addon.setGenerators(generatorReader.readAll());
        addon.setSolarGenerators(solarGeneratorReader.readAll());
        addon.setMaterialGenerators(materialGeneratorReader.readAll());
        addon.setRecipeMachines(recipeMachineReader.readAll());
        addon.setSimpleMachines(simpleMachineReader.readAll());
        addon.setMultiBlockMachines(multiBlockMachineReader.readAll());
        addon.setSupers(superReader.readAll());
        addon.setTemplateMachines(templateMachineReader.readAll());
        addon.setLinkedRecipeMachines(linkedRecipeMachineReader.readAll());
        addon.setWorkbenches(workbenchReader.readAll());

        ExceptionHandler.debugLog("开始加载要求延迟加载的内容...");

        // late inits
        addon.getMobDrops().addAll(mobDropsReader.loadLateInits());
        addon.getGeoResources().addAll(resourceReader.loadLateInits());
        addon.getItems().addAll(itemReader.loadLateInits());
        addon.getArmors().addAll(armorReader.loadLateInits());
        addon.getCapacitors().addAll(capacitorsReader.loadLateInits());
        addon.getFoods().addAll(foodReader.loadLateInits());
        addon.getMenus().addAll(menuReader.loadLateInits());
        addon.getMachines().addAll(machineReader.loadLateInits());
        addon.getGenerators().addAll(generatorReader.loadLateInits());
        addon.getSolarGenerators().addAll(solarGeneratorReader.loadLateInits());
        addon.getMaterialGenerators().addAll(materialGeneratorReader.loadLateInits());
        addon.getRecipeMachines().addAll(recipeMachineReader.loadLateInits());
        addon.getSimpleMachines().addAll(simpleMachineReader.loadLateInits());
        addon.getMultiBlockMachines().addAll(multiBlockMachineReader.loadLateInits());
        addon.getSupers().addAll(superReader.loadLateInits());
        addon.getTemplateMachines().addAll(templateMachineReader.loadLateInits());
        addon.getLinkedRecipeMachines().addAll(linkedRecipeMachineReader.loadLateInits());
        addon.getWorkbenches().addAll(workbenchReader.loadLateInits());

        YamlConfiguration researches = doFileLoad(file, Constants.RESEARCHES_FILE);
        ResearchReader researchReader = new ResearchReader(researches, addon);
        List<Research> researchesList = researchReader.readAll();
        researchesList.addAll(researchReader.loadLateInits());
        addon.setResearches(researchesList);

        ExceptionHandler.debugLog("加载附属 " + addon.getAddonId() + " 成功!");

        return addon;
    }

    private boolean loadDependencies(List<String> depends) {
        for (String dependency : depends) {
            if (RykenSlimefunCustomizer.addonManager.isLoaded(dependency)) {
                continue;
            }

            if (ids.containsKey(dependency)) {
                File dependencyFile = ids.get(dependency);
                ProjectAddonLoader loader = new ProjectAddonLoader(dependencyFile, ids);
                ProjectAddon addon = loader.load();
                if (addon != null) {
                    addon.setMarkAsDepend(true);
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
