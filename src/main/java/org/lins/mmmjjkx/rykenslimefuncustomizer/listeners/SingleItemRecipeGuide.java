package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class SingleItemRecipeGuide implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        InventoryView view = e.getView();

        if (view.getTitle().equals(Slimefun.getLocalization().getMessage(p, "guide.title.main"))) {
            ItemStack item = e.getCurrentItem();
            if (item != null && isTaggedItem(item)) {
                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.getKeys().contains("rsc_recipe")) {

                }
            }
        }
    }

    private static boolean isTaggedItem(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasNBTData() && nbtItem.getKeys().contains("tag_by_rsc");
    }

    public static ItemStack tagItemRecipe(ItemStack item, int index) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("rsc_recipe", true);
        nbtItem.setInteger("rsc_recipe_index", index);
        return nbtItem.getItem();
    }

    private ChestMenu createGUI(Player p, List<ItemStack> input, List<ItemStack> output) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.main"));
        Optional<PlayerProfile> profile = PlayerProfile.find(p);
        menu.addItem(0, ChestMenuUtils.getBackButton(p, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单"),
                (pl, s, is, action) -> {
                    SurvivalSlimefunGuide guide = new SurvivalSlimefunGuide(false, false);
                    profile.ifPresent(pr -> {
                        if (action.isShiftClicked()) {
                            pr.getGuideHistory().goBack(guide);
                        } else {
                            guide.openMainMenu(pr, 1);
                        }
                    });
                    return false;
                });

    }
}
