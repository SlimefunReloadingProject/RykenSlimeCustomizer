package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.List;

public class ItemSection extends RKYamlSection<ItemStack> {
    public ItemSection(ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack read() {
        ConfigurationSection section = this.section;
        String name = section.getString("name");
        List<String> lore = section.getStringList("lore");
        Material material = Material.getMaterial(section.getString("material", ""));
        int model = section.getInt("modelData");
        boolean glow = section.getBoolean("glow");

        if (material == null) material = Material.STONE;

        CustomItemStack cim = new CustomItemStack(material, name, lore);
        cim.setCustomModel(model);

        return glow ? CommonUtils.doGlow(cim) : cim;
    }

    @Override
    public void save(ItemStack itemStack) {

    }
}
