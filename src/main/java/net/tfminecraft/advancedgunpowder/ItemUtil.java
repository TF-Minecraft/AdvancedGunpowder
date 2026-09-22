package net.tfminecraft.advancedgunpowder;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtil {
	public void setProficiency(Player sender, Player p, ItemStack item, int amount) {
		NamespacedKey pKey = new NamespacedKey(GunpowderMain.plugin, p.getName());
		ItemMeta nbtTags = item.getItemMeta();
		sender.sendMessage("§eProficiency with this weapon has been set to §a"+amount+"/10 "+"§efor "+p.getName());
		nbtTags.getPersistentDataContainer().set(pKey, PersistentDataType.INTEGER, amount);
		item.setItemMeta(nbtTags);
	}
}
