package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.ScriptableListeners;

public class ScriptableEventListener implements Listener {
    public ScriptableEventListener() {
        Bukkit.getPluginManager().registerEvents(this, RykenSlimefunCustomizer.INSTANCE);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntity(EntityExplodeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }
}
