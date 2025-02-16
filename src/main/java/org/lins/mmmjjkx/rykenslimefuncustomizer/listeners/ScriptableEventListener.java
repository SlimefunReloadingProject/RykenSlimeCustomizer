package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import io.github.thebusybiscuit.slimefun4.api.events.AncientAltarCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.events.AndroidFarmEvent;
import io.github.thebusybiscuit.slimefun4.api.events.AndroidMineEvent;
import io.github.thebusybiscuit.slimefun4.api.events.AsyncAutoEnchanterProcessEvent;
import io.github.thebusybiscuit.slimefun4.api.events.AsyncMachineOperationFinishEvent;
import io.github.thebusybiscuit.slimefun4.api.events.AsyncProfileLoadEvent;
import io.github.thebusybiscuit.slimefun4.api.events.AutoDisenchantEvent;
import io.github.thebusybiscuit.slimefun4.api.events.AutoEnchantEvent;
import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ClimbingPickLaunchEvent;
import io.github.thebusybiscuit.slimefun4.api.events.CoolerFeedPlayerEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import io.github.thebusybiscuit.slimefun4.api.events.GEOResourceGenerationEvent;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockInteractEvent;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerLanguageChangeEvent;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerPreResearchEvent;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ReactorExplodeEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ResearchUnlockEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockBreakEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemRegistryFinalizedEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemSpawnEvent;
import io.github.thebusybiscuit.slimefun4.api.events.TalismanActivateEvent;
import io.github.thebusybiscuit.slimefun4.api.events.WaypointCreateEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.ArrowBodyCountChangeEvent;
import org.bukkit.event.entity.BatToggleSleepEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PigZombieAngerEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.StriderTemperatureChangeEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;

public class ScriptableEventListener implements Listener {
    /**
     * Events:
     * Block events:
     * {@link BlockBreakEvent}
     * {@link BlockBurnEvent}
     * {@link BlockCanBuildEvent}
     * {@link BlockCookEvent}
     * {@link BlockDamageEvent}
     * {@link BlockDispenseArmorEvent}
     * {@link BlockDispenseEvent}
     * {@link BlockDropItemEvent}
     * {@link BlockExpEvent}
     * {@link BlockExplodeEvent}
     * {@link BlockFadeEvent}
     * {@link BlockFertilizeEvent}
     * {@link BlockFormEvent}
     * {@link BlockFromToEvent}
     * {@link BlockGrowEvent}
     * {@link BlockIgniteEvent}
     * {@link BlockMultiPlaceEvent}
     * {@link BlockPhysicsEvent}
     * {@link BlockPistonExtendEvent}
     * {@link BlockPistonRetractEvent}
     * {@link BlockPlaceEvent}
     * {@link BlockReceiveGameEvent}
     * {@link BlockRedstoneEvent}
     * {@link BlockShearEntityEvent}
     * {@link BlockSpreadEvent}
     * {@link CauldronLevelChangeEvent}
     * {@link EntityBlockFormEvent}
     * {@link FluidLevelChangeEvent}
     * {@link LeavesDecayEvent}
     * {@link MoistureChangeEvent}
     * {@link NotePlayEvent}
     * {@link SignChangeEvent}
     * {@link SpongeAbsorbEvent}
     * commands events:
     * {@link UnknownCommandEvent}
     * enchantment events:
     * {@link EnchantItemEvent}
     * {@link PrepareItemEnchantEvent}
     * entity events:
     * {@link AreaEffectCloudApplyEvent}
     * {@link ArrowBodyCountChangeEvent}
     * {@link BatToggleSleepEvent}
     * {@link CreatureSpawnEvent}
     * {@link CreeperPowerEvent}
     * {@link EnderDragonChangePhaseEvent}
     * {@link EntityAirChangeEvent}
     * {@link EntityBreakDoorEvent}
     * {@link EntityBreedEvent}
     * {@link EntityChangeBlockEvent}
     * {@link EntityCombustByBlockEvent}
     * {@link EntityCombustByEntityEvent}
     * {@link EntityCombustEvent}
     * {@link PortalCreateEvent}
     * {@link EntityDamageByBlockEvent}
     * {@link EntityDamageByEntityEvent}
     * {@link EntityDamageEvent}
     * {@link EntityDeathEvent}
     * {@link EntityDropItemEvent}
     * {@link EntityEnterBlockEvent}
     * {@link EntityEnterLoveModeEvent}
     * {@link EntityExhaustionEvent}
     * {@link EntityExplodeEvent}
     * {@link EntityInteractEvent}
     * {@link EntityPickupItemEvent}
     * {@link EntityPlaceEvent}
     * {@link EntityPortalEnterEvent}
     * {@link EntityPortalExitEvent}
     * {@link EntityPoseChangeEvent}
     * {@link EntityPotionEffectEvent}
     * {@link EntityRegainHealthEvent}
     * {@link EntityResurrectEvent}
     * {@link EntityShootBowEvent}
     * {@link EntitySpawnEvent}
     * {@link EntitySpellCastEvent}
     * {@link EntityTameEvent}
     * {@link EntityTargetEvent}
     * {@link EntityTargetLivingEntityEvent}
     * {@link EntityTeleportEvent}
     * {@link EntityToggleGlideEvent}
     * {@link EntityToggleSwimEvent}
     * {@link EntityTransformEvent}
     * {@link EntityUnleashEvent}
     * {@link ExpBottleEvent}
     * {@link ExplosionPrimeEvent}
     * {@link FireworkExplodeEvent}
     * {@link FoodLevelChangeEvent}
     * {@link HorseJumpEvent}
     * {@link ItemDespawnEvent}
     * {@link ItemMergeEvent}
     * {@link ItemSpawnEvent}
     * {@link LingeringPotionSplashEvent}
     * {@link PiglinBarterEvent}
     * {@link PigZapEvent}
     * {@link PigZombieAngerEvent}
     * {@link PlayerDeathEvent}
     * {@link PlayerLeashEntityEvent}
     * {@link PotionSplashEvent}
     * {@link ProjectileHitEvent}
     * {@link ProjectileLaunchEvent}
     * {@link SheepDyeWoolEvent}
     * {@link SheepRegrowWoolEvent}
     * {@link SlimeSplitEvent}
     * {@link SpawnerSpawnEvent}
     * {@link StriderTemperatureChangeEvent}
     * {@link VillagerAcquireTradeEvent}
     * {@link VillagerCareerChangeEvent}
     * {@link VillagerReplenishTradeEvent}
     * hanging events:
     * {@link HangingBreakByEntityEvent}
     * {@link HangingBreakEvent}
     * {@link HangingPlaceEvent}
     * inventory events:
     * {@link BrewEvent}
     * {@link BrewingStandFuelEvent}
     * {@link CraftItemEvent}
     * {@link FurnaceBurnEvent}
     * {@link FurnaceExtractEvent}
     * {@link FurnaceSmeltEvent}
     * {@link InventoryClickEvent}
     * {@link InventoryCloseEvent}
     * {@link InventoryCreativeEvent}
     * {@link InventoryDragEvent}
     * {@link InventoryMoveItemEvent}
     * {@link InventoryOpenEvent}
     * {@link InventoryPickupItemEvent}
     * {@link PrepareAnvilEvent}
     * {@link PrepareItemCraftEvent}
     * {@link PrepareSmithingEvent}
     * {@link SmithItemEvent}
     * {@link TradeSelectEvent}
     * player events:
     * {@link AsyncPlayerChatEvent}
     * {@link AsyncPlayerPreLoginEvent}
     * {@link PlayerAdvancementDoneEvent}
     * {@link PlayerAnimationEvent}
     * {@link PlayerArmorStandManipulateEvent}
     * {@link PlayerAttemptPickupItemEvent}
     * {@link PlayerBedEnterEvent}
     * {@link PlayerBedLeaveEvent}
     * {@link PlayerBucketEmptyEvent}
     * {@link PlayerBucketEntityEvent}
     * {@link PlayerBucketFillEvent}
     * {@link PlayerChangedMainHandEvent}
     * {@link PlayerChangedWorldEvent}
     * {@link PlayerCommandPreprocessEvent}
     * {@link PlayerCommandSendEvent}
     * {@link PlayerDropItemEvent}
     * {@link PlayerEditBookEvent}
     * {@link PlayerEggThrowEvent}
     * {@link PlayerExpChangeEvent}
     * {@link PlayerFishEvent}
     * {@link PlayerGameModeChangeEvent}
     * {@link PlayerHarvestBlockEvent}
     * {@link PlayerHideEntityEvent}
     * {@link PlayerInteractAtEntityEvent}
     * {@link PlayerInteractEntityEvent}
     * {@link PlayerInteractEvent}
     * {@link PlayerItemBreakEvent}
     * {@link PlayerItemConsumeEvent}
     * {@link PlayerItemDamageEvent}
     * {@link PlayerItemHeldEvent}
     * {@link PlayerItemMendEvent}
     * {@link PlayerJoinEvent}
     * {@link PlayerKickEvent}
     * {@link PlayerLevelChangeEvent}
     * {@link PlayerLocaleChangeEvent}
     * {@link PlayerLoginEvent}
     * {@link PlayerMoveEvent}
     * {@link PlayerPickupArrowEvent}
     * {@link PlayerPickupItemEvent}
     * {@link PlayerPortalEvent}
     * {@link PlayerQuitEvent}
     * {@link PlayerRecipeDiscoverEvent}
     * {@link PlayerRegisterChannelEvent}
     * {@link PlayerResourcePackStatusEvent}
     * {@link PlayerRespawnEvent}
     * {@link PlayerRiptideEvent}
     * {@link PlayerShearEntityEvent}
     * {@link PlayerShowEntityEvent}
     * {@link PlayerStatisticIncrementEvent}
     * {@link PlayerSwapHandItemsEvent}
     * {@link PlayerTakeLecternBookEvent}
     * {@link PlayerTeleportEvent}
     * {@link PlayerToggleFlightEvent}
     * {@link PlayerToggleSneakEvent}
     * {@link PlayerToggleSprintEvent}
     * {@link PlayerUnleashEntityEvent}
     * {@link PlayerUnregisterChannelEvent}
     * {@link PlayerVelocityEvent}
     * raid events:
     * {@link RaidFinishEvent}
     * {@link RaidSpawnWaveEvent}
     * {@link RaidStopEvent}
     * {@link RaidTriggerEvent}
     * server events:
     * {@link BroadcastMessageEvent}
     * {@link MapInitializeEvent}
     * {@link PluginDisableEvent}
     * {@link PluginEnableEvent}
     * {@link RemoteServerCommandEvent}
     * {@link ServerCommandEvent}
     * {@link ServerListPingEvent}
     * {@link ServerLoadEvent}
     * {@link ServiceRegisterEvent}
     * {@link ServiceUnregisterEvent}
     * {@link TabCompleteEvent}
     * vehicle events:
     * {@link VehicleBlockCollisionEvent}
     * {@link VehicleCreateEvent}
     * {@link VehicleDamageEvent}
     * {@link VehicleDestroyEvent}
     * {@link VehicleEnterEvent}
     * {@link VehicleEntityCollisionEvent}
     * {@link VehicleExitEvent}
     * {@link VehicleMoveEvent}
     * {@link VehicleUpdateEvent}
     * weather events:
     * {@link LightningStrikeEvent}
     * {@link ThunderChangeEvent}
     * {@link WeatherChangeEvent}
     * world events:
     * {@link ChunkLoadEvent}
     * {@link ChunkPopulateEvent}
     * {@link ChunkUnloadEvent}
     * {@link EntitiesLoadEvent}
     * {@link EntitiesUnloadEvent}
     * {@link GenericGameEvent}
     * {@link LootGenerateEvent}
     * {@link PortalCreateEvent}
     * {@link SpawnChangeEvent}
     * {@link StructureGrowEvent}
     * {@link TimeSkipEvent}
     * {@link WorldInitEvent}
     * {@link WorldLoadEvent}
     * {@link WorldSaveEvent}
     * {@link WorldUnloadEvent}
     * Slimefun events:
     * {@link AncientAltarCraftEvent}
     * {@link AndroidFarmEvent}
     * {@link AndroidMineEvent}
     * {@link AsyncAutoEnchanterProcessEvent}
     * {@link AsyncMachineOperationFinishEvent}
     * {@link AsyncProfileLoadEvent}
     * {@link AutoDisenchantEvent}
     * {@link AutoEnchantEvent}
     * {@link BlockPlacerPlaceEvent}
     * {@link ClimbingPickLaunchEvent}
     * {@link CoolerFeedPlayerEvent}
     * {@link ExplosiveToolBreakBlocksEvent}
     * {@link GEOResourceGenerationEvent}
     * {@link MultiBlockCraftEvent}
     * {@link MultiBlockInteractEvent}
     * {@link PlayerLanguageChangeEvent}
     * {@link PlayerPreResearchEvent}
     * {@link PlayerRightClickEvent}
     * {@link ReactorExplodeEvent}
     * {@link ResearchUnlockEvent}
     * {@link SlimefunBlockBreakEvent}
     * {@link SlimefunBlockPlaceEvent}
     * {@link SlimefunGuideOpenEvent}
     * {@link SlimefunItemRegistryFinalizedEvent}
     * {@link SlimefunItemSpawnEvent}
     * {@link TalismanActivateEvent}
     * {@link WaypointCreateEvent}
     */
    public ScriptableEventListener(JavaScriptEval e) {
        ctx = new ListenerContext(e);
    }

    private final ListenerContext ctx;

    @EventHandler
    public void onEvent(BlockBreakEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockBurnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockCanBuildEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockCookEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockDamageEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockDispenseArmorEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockDispenseEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockDropItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockExpEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockExplodeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockFadeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockFertilizeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockFormEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockFromToEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockGrowEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockIgniteEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockMultiPlaceEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockPhysicsEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockPistonExtendEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockPistonRetractEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockReceiveGameEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockRedstoneEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockShearEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BlockSpreadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(CauldronLevelChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityBlockFormEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(FluidLevelChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(LeavesDecayEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(MoistureChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(NotePlayEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SignChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SpongeAbsorbEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(UnknownCommandEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EnchantItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PrepareItemEnchantEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(AreaEffectCloudApplyEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ArrowBodyCountChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BatToggleSleepEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(CreatureSpawnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(CreeperPowerEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EnderDragonChangePhaseEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityAirChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityBreakDoorEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityBreedEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityChangeBlockEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityCombustByBlockEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityCombustByEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityCombustEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityDamageByBlockEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityDamageByEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityDamageEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityDeathEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityDropItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityEnterBlockEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityEnterLoveModeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityExhaustionEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityExplodeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityInteractEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityPickupItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityPlaceEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityPortalEnterEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityPortalExitEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityPoseChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityPotionEffectEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityRegainHealthEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityResurrectEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityShootBowEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntitySpawnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntitySpellCastEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityTameEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityTargetEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityTargetLivingEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityTeleportEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityToggleGlideEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityToggleSwimEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityTransformEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntityUnleashEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ExpBottleEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ExplosionPrimeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(FireworkExplodeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(FoodLevelChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(HorseJumpEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ItemDespawnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ItemMergeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ItemSpawnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(LingeringPotionSplashEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PigZapEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PigZombieAngerEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PiglinBarterEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerDeathEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerLeashEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PotionSplashEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ProjectileHitEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ProjectileLaunchEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SheepDyeWoolEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SheepRegrowWoolEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SlimeSplitEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SpawnerSpawnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(StriderTemperatureChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VillagerAcquireTradeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VillagerCareerChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VillagerReplenishTradeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(HangingBreakByEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(HangingBreakEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(HangingPlaceEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BrewEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BrewingStandFuelEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(CraftItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(FurnaceBurnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(FurnaceExtractEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(FurnaceSmeltEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(InventoryClickEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(InventoryCreativeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(InventoryDragEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(InventoryMoveItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(InventoryOpenEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(InventoryPickupItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PrepareAnvilEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PrepareItemCraftEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PrepareSmithingEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SmithItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(TradeSelectEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(AsyncPlayerChatEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(AsyncPlayerPreLoginEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerAdvancementDoneEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerAnimationEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerArmorStandManipulateEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerAttemptPickupItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerBedEnterEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerBedLeaveEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerBucketEmptyEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerBucketEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerBucketFillEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerChangedMainHandEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerChangedWorldEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerCommandPreprocessEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerCommandSendEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerDropItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerEditBookEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerEggThrowEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerExpChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerFishEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerGameModeChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerHarvestBlockEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerHideEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerInteractAtEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerInteractEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerItemBreakEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerItemConsumeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerItemDamageEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerItemHeldEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerItemMendEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerKickEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerLevelChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerLocaleChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerLoginEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerMoveEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerPickupArrowEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerPickupItemEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerPortalEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerRecipeDiscoverEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerRegisterChannelEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerResourcePackStatusEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerRespawnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerRiptideEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerShearEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerShowEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerStatisticIncrementEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerSwapHandItemsEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerTakeLecternBookEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerTeleportEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerToggleFlightEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerToggleSneakEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerToggleSprintEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerUnleashEntityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerUnregisterChannelEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PlayerVelocityEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(RaidFinishEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(RaidSpawnWaveEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(RaidStopEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(RaidTriggerEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(BroadcastMessageEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(MapInitializeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PluginDisableEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PluginEnableEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(RemoteServerCommandEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ServerCommandEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ServerListPingEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ServerLoadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ServiceRegisterEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ServiceUnregisterEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(TabCompleteEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleBlockCollisionEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleCreateEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleDamageEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleDestroyEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleEnterEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleEntityCollisionEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleExitEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleMoveEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(VehicleUpdateEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(LightningStrikeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ThunderChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(WeatherChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ChunkLoadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ChunkPopulateEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(ChunkUnloadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntitiesLoadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(EntitiesUnloadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(GenericGameEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(LootGenerateEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(PortalCreateEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(SpawnChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(StructureGrowEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(TimeSkipEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(WorldInitEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(WorldLoadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(WorldSaveEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onEvent(WorldUnloadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAncientAltarCraft(AncientAltarCraftEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAndroidFarm(AndroidFarmEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAndroidMine(AndroidMineEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAsyncAutoEnchanterProcess(AsyncAutoEnchanterProcessEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAsyncMachineOperationFinish(AsyncMachineOperationFinishEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAsyncProfileLoad(AsyncProfileLoadEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAutoDisenchant(AutoDisenchantEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onAutoEnchant(AutoEnchantEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onBlockPlacerPlace(BlockPlacerPlaceEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onClimbingPickLaunch(ClimbingPickLaunchEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onCoolerFeedPlayer(CoolerFeedPlayerEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onExplosiveToolBreakBlocks(ExplosiveToolBreakBlocksEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onGEOResourceGeneration(GEOResourceGenerationEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onMultiBlockCraft(MultiBlockCraftEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onMultiBlockInteract(MultiBlockInteractEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onPlayerLanguageChange(PlayerLanguageChangeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onPlayerPreResearch(PlayerPreResearchEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerRightClickEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onReactorExplode(ReactorExplodeEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onResearchUnlock(ResearchUnlockEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onSlimefunBlockBreak(SlimefunBlockBreakEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onSlimefunBlockPlace(SlimefunBlockPlaceEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onSlimefunGuideOpen(SlimefunGuideOpenEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onSlimefunItemRegistryFinalized(SlimefunItemRegistryFinalizedEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onSlimefunItemSpawn(SlimefunItemSpawnEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onTalismanActivate(TalismanActivateEvent e) {
        ctx.invoke(e);
    }

    @EventHandler
    public void onWaypointCreate(WaypointCreateEvent e) {
        ctx.invoke(e);
    }

    class ListenerContext {
        private final JavaScriptEval eval;

        ListenerContext(JavaScriptEval eval) {
            this.eval = eval;
        }

        public <T extends Event> void invoke(T event) {
            String className = event.getClass().getSimpleName();
            String methodName = "on" + className.replace("Event", "");
            eval.evalFunction(methodName, event);
        }
    }
}
