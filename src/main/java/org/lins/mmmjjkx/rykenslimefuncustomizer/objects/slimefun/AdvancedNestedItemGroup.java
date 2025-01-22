package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun;

import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class AdvancedNestedItemGroup extends NestedItemGroup {
    private final List<SubItemGroup> subGroups;

    public AdvancedNestedItemGroup(NamespacedKey key, ItemStack item, int tier) {
        super(key, item, tier);

        ExceptionHandler.debugLog("创建物品组: " + key);

        subGroups = new ArrayList<>();
    }

    @Override
    public void open(Player p, PlayerProfile profile, SlimefunGuideMode mode) {
        setup(p, profile, mode, 1);
    }

    public void addSubGroup(@Nonnull SubItemGroup group) {
        super.addSubGroup(group);

        this.subGroups.add(group);
    }

    public void removeSubGroup(@Nonnull SubItemGroup group) {
        super.removeSubGroup(group);

        this.subGroups.remove(group);
    }

    @SuppressWarnings("deprecation")
    private void setup(Player p, PlayerProfile profile, SlimefunGuideMode mode, int page) {
        GuideHistory history = profile.getGuideHistory();
        if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
            history.add(this, page);
        }

        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.main"));
        SurvivalSlimefunGuide guide =
                (SurvivalSlimefunGuide) Slimefun.getRegistry().getSlimefunGuide(mode);
        menu.setEmptySlotsClickable(false);
        SoundEffect var10001 = SoundEffect.GUIDE_BUTTON_CLICK_SOUND;
        Objects.requireNonNull(var10001);
        menu.addMenuOpeningHandler(var10001::playFor);
        guide.createHeader(p, profile, menu);
        menu.addItem(
                1,
                new CustomItemStack(ChestMenuUtils.getBackButton(
                        p, "", ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide"))));
        menu.addMenuClickHandler(1, (pl, s, is, action) -> {
            SlimefunGuide.openMainMenu(profile, mode, history.getMainMenuPage());
            return false;
        });

        int index = 9;
        int target = 36 * (page - 1) - 1;

        while (target < this.subGroups.size() - 1 && index < 45) {
            ++target;
            SubItemGroup itemGroup = this.subGroups.get(target);
            if (itemGroup.isVisibleInNested(p)) {
                menu.addItem(index, itemGroup.getItem(p));
                menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                    if (itemGroup instanceof ItemGroupButton button) {
                        button.run(p, slot, item, action, mode);
                        return false;
                    }
                    SlimefunGuide.openItemGroup(profile, itemGroup, mode, 1);
                    return false;
                });
                ++index;
            }
        }

        int pages = target == this.subGroups.size() - 1 ? page : (this.subGroups.size() - 1) / 36 + 1;
        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;
            if (next > 0) {
                setup(p, profile, mode, next);
            }

            return false;
        });
        menu.addItem(52, ChestMenuUtils.getNextButton(p, page, pages));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            int next = page + 1;
            if (next <= pages) {
                setup(p, profile, mode, next);
            }

            return false;
        });
        menu.open(p);
    }
}
