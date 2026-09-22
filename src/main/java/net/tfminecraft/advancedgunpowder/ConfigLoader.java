package net.tfminecraft.advancedgunpowder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;


public class ConfigLoader {
	public static List<HandWeapon> loadedHandWeapons = new ArrayList<HandWeapon>();
	public static List<String> helmets = new ArrayList<String>();
	public static List<String> chestplates = new ArrayList<String>();
	public static List<String> leggings = new ArrayList<String>();
	public static List<String> boots = new ArrayList<String>();
	
	public static List<String> heavy_sfx = new ArrayList<String>();
	public static List<String> light_sfx = new ArrayList<String>();
	
	public static Double recoilPerLevel;
	public static Double reloadPerLevel;

	public static String attribute;
	public static String type;
	
	public void loadConfig(FileConfiguration config) {
		loadedHandWeapons = new ArrayList<HandWeapon>();
		heavy_sfx = config.getStringList("heavy_sfx");
		light_sfx = config.getStringList("light_sfx");
		recoilPerLevel = config.getDouble("recoil_per_lvl");
		reloadPerLevel = config.getDouble("reload_per_lvl");
		Set<String> set = config.getConfigurationSection("handweapons").getKeys(false);

		List<String> list = new ArrayList<String>(set);
		
		for(String key : list) {
			loadedHandWeapons.add(getHandWeaponFromConfig(config, key));
		}

		attribute = config.getString("attribute", "charisma");
		type = config.getString("item-type", "muskets");
	}
	
	public HandWeapon getHandWeaponFromConfig(FileConfiguration config, String key) {
		helmets = config.getStringList("helmets");
		chestplates = config.getStringList("chestplates");
		leggings = config.getStringList("leggings");
		boots = config.getStringList("boots");
		HandWeapon w = new HandWeapon();
		w.setId(key);
		w.setMMOItem(config.getConfigurationSection("handweapons."+key).getString("mmoitem"));
		w.setSfx(config.getConfigurationSection("handweapons."+key).getString("sfx"));
		w.setAmmo(config.getConfigurationSection("handweapons."+key).getString("ammo"));
		w.setReloadTime(config.getConfigurationSection("handweapons."+key).getDouble("reload_time"));
		w.setEquipTime(config.getConfigurationSection("handweapons."+key).getDouble("equip_time"));
		w.setModel(config.getConfigurationSection("handweapons."+key).getInt("model"));
		w.setLoadingModel(config.getConfigurationSection("handweapons."+key).getInt("reload_model"));
		w.setReloadedModel(config.getConfigurationSection("handweapons."+key).getInt("reloaded_model"));
		w.setShots(config.getConfigurationSection("handweapons."+key).getInt("shots"));
		w.setPierceRating(config.getConfigurationSection("handweapons."+key).getDouble("pierce_rating"));
		w.setItem(Material.valueOf(config.getConfigurationSection("handweapons."+key).getString("item").toUpperCase()));
		w.setReloadedItem(Material.valueOf(config.getConfigurationSection("handweapons."+key).getString("reloaded_item").toUpperCase()));
		return w;
	}
}
