package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.ScriptableListeners;
import org.spigotmc.event.entity.EntityMountEvent;

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
    public void onEntityExplode(EntityExplodeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerAchievementDone(PlayerAdvancementDoneEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemMend(PlayerItemMendEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onNotePlay(NotePlayEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onCauldronLevelChange(CauldronLevelChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBrewingStandFuel(BrewingStandFuelEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onArrowBodyCountChange(ArrowBodyCountChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onCreeperPower(CreeperPowerEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityAirChange(EntityAirChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onExpBottle(ExpBottleEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }
    
    @EventHandler
    public void onEntityExhaustion(EntityExhaustionEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityUnleash(EntityUnleashEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityCreatePortal(EntityCreatePortalEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityBreakDoor(EntityBreakDoorEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntityEnterLoveMode(EntityEnterLoveModeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBatToggleSleep(BatToggleSleepEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }
}
