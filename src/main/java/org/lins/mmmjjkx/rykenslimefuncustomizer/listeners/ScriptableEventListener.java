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
import org.bukkit.Bukkit;
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
import org.bukkit.event.block.BlockPistonEvent;
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
import org.bukkit.event.entity.EntityCreatePortalEvent;
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
import org.bukkit.event.inventory.InventoryInteractEvent;
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
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
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
import org.bukkit.event.player.PlayerPreLoginEvent;
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
import org.bukkit.event.vehicle.VehicleCollisionEvent;
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
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.ScriptableListeners;

@SuppressWarnings("deprecation")
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
     * {@link BlockPistonEvent}
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
     * {@link EntityCreatePortalEvent}
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
     * {@link InventoryInteractEvent}
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
     * {@link PlayerChannelEvent}
     * {@link PlayerChatEvent}
     * {@link PlayerChatTabCompleteEvent}
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
     * {@link PlayerPreLoginEvent}
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
     * {@link VehicleCollisionEvent}
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
     * slimefun events:
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
    public ScriptableEventListener() {
        Bukkit.getPluginManager().registerEvents(this, RykenSlimefunCustomizer.INSTANCE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockCook(BlockCookEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockExp(BlockExpEvent e) {
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
    public void onBlockFertilize(BlockFertilizeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockMultiPlace(BlockMultiPlaceEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPiston(BlockPistonEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockReceiveGame(BlockReceiveGameEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockShear(BlockShearEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
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
    public void onSpongeAbsorb(SpongeAbsorbEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onUnknownCommand(UnknownCommandEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBrewingStandFuel(BrewingStandFuelEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBrewEvent(BrewEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSmithItem(SmithItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onTradeSelect(TradeSelectEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent e) {
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
    public void onPlayerChangedMainHand(PlayerChangedMainHandEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerChannel(PlayerChannelEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerEggThrow(PlayerEggThrowEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerHideEntity(PlayerHideEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerItemMend(PlayerItemMendEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerLocaleChange(PlayerLocaleChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerPreLogin(PlayerPreLoginEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
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
    public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onRaidSpawnWave(RaidSpawnWaveEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBroadcastMessage(BroadcastMessageEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onRemoteServerCommand(RemoteServerCommandEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onServiceUnregister(ServiceUnregisterEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onGenericGame(GenericGameEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSpawnChange(SpawnChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAncientAltarCraft(AncientAltarCraftEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAndroidFarm(AndroidFarmEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAndroidMine(AndroidMineEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAsyncAutoEnchanterProcess(AsyncAutoEnchanterProcessEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAsyncMachineOperationFinish(AsyncMachineOperationFinishEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAsyncProfileLoad(AsyncProfileLoadEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAutoDisenchant(AutoDisenchantEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onAutoEnchant(AutoEnchantEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onBlockPlacerPlace(BlockPlacerPlaceEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onClimbingPickLaunch(ClimbingPickLaunchEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onCoolerFeedPlayer(CoolerFeedPlayerEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onExplosiveToolBreakBlocks(ExplosiveToolBreakBlocksEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onGEOResourceGeneration(GEOResourceGenerationEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onMultiBlockCraft(MultiBlockCraftEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onMultiBlockInteract(MultiBlockInteractEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerLanguageChange(PlayerLanguageChangeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerPreResearch(PlayerPreResearchEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onPlayerRightClick(PlayerRightClickEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onReactorExplode(ReactorExplodeEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onResearchUnlock(ResearchUnlockEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSlimefunBlockBreak(SlimefunBlockBreakEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSlimefunBlockPlace(SlimefunBlockPlaceEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSlimefunGuideOpen(SlimefunGuideOpenEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSlimefunItemRegistryFinalized(SlimefunItemRegistryFinalizedEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onSlimefunItemSpawn(SlimefunItemSpawnEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onTalismanActivate(TalismanActivateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }

    @EventHandler
    public void onWaypointCreate(WaypointCreateEvent e) {
        ScriptableListeners.getScriptableListeners().forEach(o -> o.doEventEval(e));
    }
}
