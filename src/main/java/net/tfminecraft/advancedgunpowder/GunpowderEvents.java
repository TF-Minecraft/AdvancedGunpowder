package net.tfminecraft.advancedgunpowder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.util.RayTrace;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes.AttributeInstance;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.event.item.UntargetedWeaponUseEvent;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Musket;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.manager.ItemManager;
import net.Indyuce.mmoitems.stat.data.DoubleData;

@SuppressWarnings("deprecation")
public class GunpowderEvents implements Listener{
	static HashMap<Player, Boolean> isReloading = new HashMap<>();
	static HashMap<Player, ItemStack> equippedItem = new HashMap<>();
	static HashMap<Player, Boolean> isEquipping = new HashMap<>();
	@EventHandler
	public void equipEvent(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if(isEquipping.containsKey(p)) {
			if(isEquipping.get(p) == true) {
				isEquipping.put(p, false);
				return;
			}
		}
		if(equippedItem.containsKey(p)) {
			equippedItem.remove(p);
			return;
		}
		ItemStack i = p.getInventory().getItem(e.getNewSlot());
		if(i == null) return;
		NBTItem nbt = NBTItem.get(i);
		if(nbt.hasType() == false) return;
		if(!nbt.getType().equalsIgnoreCase(ConfigLoader.type)) return;
		for(HandWeapon w : ConfigLoader.loadedHandWeapons) {
        	String mmoitem = w.getMMOItem();
        	String type = mmoitem.split("\\.")[0];
        	String id = mmoitem.split("\\.")[1];
        	if(nbt.getType().equalsIgnoreCase(type) && nbt.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(id)) {
        		ItemMeta nbtTags = i.getItemMeta();
        		NamespacedKey isLoaded = new NamespacedKey(GunpowderMain.plugin, "loaded");
        		Integer loaded;
        		if(nbtTags.getPersistentDataContainer().get(isLoaded, PersistentDataType.INTEGER) == null) {
        			loaded = 0;
        		} else {
        			loaded = nbtTags.getPersistentDataContainer().get(isLoaded, PersistentDataType.INTEGER);
        		}
        		if(loaded == 0) return;
                PotionEffect slow = new PotionEffect(PotionEffectType.SLOWNESS, (int) Math.floor(w.getReloadTime()*20), 2, false, false);
        		p.addPotionEffect(slow);
        		isEquipping.put(p, true);
        		Double time = w.getEquipTime();
        		if(checkArmor(p)) {
        			time = time/4;
        		}
        		final Double fTime = time*10;
        		new BukkitRunnable()
        		{
        			Double t = (double) fTime;
        			public void run()
        			   {
        				if(t >= 0) {
        					p.sendTitle(" ", "§eEquipping... §7" + t/10 + "§es", 1, 20, 5);
        				} else {
        					p.sendTitle(" ", "§eEquipped!", 1, 20, 10);
                            p.removePotionEffect(PotionEffectType.SLOWNESS);
        					isEquipping.put(p, false);
        					equippedItem.put(p, i);
        					this.cancel();
        				}
        				if(!p.getInventory().getItemInMainHand().equals(i) || isEquipping.get(p) == false) {
                            p.removePotionEffect(PotionEffectType.SLOWNESS);
        					isEquipping.put(p, false);
        					this.cancel();
        				}
        				t--;
        			   }
        		}.runTaskTimer(GunpowderMain.plugin, 0L, 2L);
        	}
		}
	}
	@EventHandler
    public void fireMusket(UntargetedWeaponUseEvent e) {
		Weapon weapon = e.getWeapon();
		NBTItem nbt = weapon.getNBTItem();
		Player p = e.getPlayer();
		if(e.getWeapon() instanceof Musket) {
			e.setCancelled(true);
		}
        for(HandWeapon w : ConfigLoader.loadedHandWeapons) {
        	String mmoitem = w.getMMOItem();
        	String type = mmoitem.split("\\.")[0];
        	String id = mmoitem.split("\\.")[1];
        	if(nbt.getType().equalsIgnoreCase(type) && nbt.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(id)) {
        		ItemStack weaponItem = weapon.getItem();
        		ItemMeta nbtTags = weaponItem.getItemMeta();
        		NamespacedKey isLoaded = new NamespacedKey(GunpowderMain.plugin, "loaded");
        		Integer loaded;
        		if(nbtTags.getPersistentDataContainer().get(isLoaded, PersistentDataType.INTEGER) == null) {
        			loaded = 0;
        		} else {
        			loaded = nbtTags.getPersistentDataContainer().get(isLoaded, PersistentDataType.INTEGER);
        		}
        		if(loaded == 0) {
        			e.setCancelled(true);
        			if(isReloading.containsKey(p)) {
        				if(isReloading.get(p).equals(false)) {
        					reloadMusket(w, weaponItem, p);
        				} else {
        					return;
        				}
        			} else {
        				reloadMusket(w, weaponItem, p);
        			}
        		} else {
        			if(!equippedItem.containsKey(p)) {
            			p.sendMessage("§cWeapon not equipped yet!");
            			e.setCancelled(true);
            			return;
            		}
            		if(!equippedItem.get(p).equals(weaponItem)) {
            			p.sendMessage("§cWeapon not equipped yet!");
            			e.setCancelled(true);
            			return;
            		}
        			nbtTags.getPersistentDataContainer().set(isLoaded, PersistentDataType.INTEGER, nbtTags.getPersistentDataContainer().get(isLoaded, PersistentDataType.INTEGER)-1);
        			weaponItem.setItemMeta(nbtTags);
        			MMOItem m = new LiveMMOItem(nbt);
    				DoubleData dRange = (DoubleData) m.getData(ItemStats.RANGE);
    				DoubleData dRecoil = (DoubleData) m.getData(ItemStats.RECOIL);
    				Double range = dRange.getValue();
    		        Double recoil = dRecoil.getValue();
    		        AttributeInstance attribute = net.Indyuce.mmocore.api.player.PlayerData.get(p).getAttributes().getInstance(ConfigLoader.attribute);
    		        Integer level = attribute.getTotal();
    				NamespacedKey pKey = new NamespacedKey(GunpowderMain.plugin, p.getName());
    				ItemMeta pTags = weaponItem.getItemMeta();
    				if(pTags.getPersistentDataContainer().get(pKey, PersistentDataType.INTEGER) != null) {
    					Integer proficiency = pTags.getPersistentDataContainer().get(pKey, PersistentDataType.INTEGER);
    					recoil = recoil * (1-((level * ConfigLoader.recoilPerLevel+proficiency)/100));
    				} else {
    					recoil = recoil * (1-((level * ConfigLoader.recoilPerLevel)/100));
    				}
    		        recoil = (double)Math.round(recoil*10)/10;
    		        if(p.isInsideVehicle()) {
    		        	recoil = recoil*0.70;
    		        }
    		        Random r = new Random();
    		        final double a = Math.toRadians(p.getEyeLocation().getYaw() + 90 + 45);
    		        final Location loc = p.getLocation().add(Math.cos(a) * .5, 1.5, Math.sin(a) * .5);
    		        loc.setPitch((float) (loc.getPitch() + (r.nextDouble() - .5) * 2 * recoil));
    		        loc.setYaw((float) (loc.getYaw() + (r.nextDouble() - .5) * 2 * recoil));
    		        PlayerMetadata stats = PlayerData.get(p).getStats().newTemporary(EquipmentSlot.MAIN_HAND);
    		        final RayTrace trace = new RayTrace(loc, loc.getDirection(), range, entity -> UtilityMethods.canTarget(stats.getPlayer(), entity, InteractionType.OFFENSE_ACTION));
    		        if (trace.hasHit()) {
    		        	DoubleData dDamage = (DoubleData) m.getData(ItemStats.ATTACK_DAMAGE);
    		        	Location targetLoc = trace.getHit().getLocation();
    		    		Double distance = Math.sqrt(Math.pow(p.getLocation().getX()-targetLoc.getX(), 2)+Math.pow(p.getLocation().getY()-targetLoc.getY(), 2)+Math.pow(p.getLocation().getZ()-targetLoc.getZ(), 2));
    		    		distance = (double) Math.round(distance);
    		    		if(distance < 5) {
    		    			dDamage.setValue(dDamage.getValue() * (0.2*distance));
    		    		}
    		        	Double damage = dDamage.getValue();
    		        	Double pDamage = damage * (w.getPierceRating()/100);
    		        	Double rDamage = damage-pDamage;
    		        	stats.attack(trace.getHit(), rDamage, DamageType.PROJECTILE);
    		        	trace.getHit().damage(pDamage);
    		    		calculateProficiency(p, weaponItem, distance);
    		        }
    		        trace.draw(.5, Color.BLACK);
    		        List<String> sfx = new ArrayList<String>();
    		        if(w.getSfx().equalsIgnoreCase("heavy")) {
    		        	sfx = ConfigLoader.heavy_sfx;
    		        } else if(w.getSfx().equalsIgnoreCase("light")){
    		        	sfx = ConfigLoader.light_sfx;
    		        } else {
    		        	sfx.add("airships:custom.airships.winchester");
    		        }
    		        Collections.shuffle(sfx);
    		        lightEffect(p.getEyeLocation());
    		        p.getWorld().playSound(p.getLocation(), sfx.get(0), 10f, 1f);
        			if(nbtTags.getPersistentDataContainer().get(isLoaded, PersistentDataType.INTEGER) == 0) {
        				weaponItem.setType(w.getItem());
            			ItemMeta model = weaponItem.getItemMeta();
    	        		model.setCustomModelData(w.getModel());
    	        		weaponItem.setItemMeta(model);
        			}
        		}
        	}
        }
    }
	private void calculateProficiency(Player p, ItemStack weaponItem, Double distance) {
		Integer proficiency;
		NamespacedKey pKey = new NamespacedKey(GunpowderMain.plugin, p.getName());
		ItemMeta nbtTags = weaponItem.getItemMeta();
		if(nbtTags.getPersistentDataContainer().get(pKey, PersistentDataType.INTEGER) == null) {
			proficiency = 0;
		} else {
			proficiency = nbtTags.getPersistentDataContainer().get(pKey, PersistentDataType.INTEGER);
		}
		if(proficiency == 10) {
			return;
		}
		if(Math.floor(Math.random()*100)<=distance) {
			proficiency = proficiency+1;
			p.sendMessage("§eYour proficiency with this weapon has increased to §a"+proficiency+"/10");
			nbtTags.getPersistentDataContainer().set(pKey, PersistentDataType.INTEGER, proficiency);
			weaponItem.setItemMeta(nbtTags);
		}
	}
	public void checkProficiency(Player p) {
		ItemStack i = p.getInventory().getItemInMainHand();
		NBTItem nbt = NBTItem.get(i);
		if(nbt.hasType()) {
			for(HandWeapon w : ConfigLoader.loadedHandWeapons) {
	        	String mmoitem = w.getMMOItem();
	        	String type = mmoitem.split("\\.")[0];
	        	String id = mmoitem.split("\\.")[1];
	        	if(nbt.getType().equalsIgnoreCase(type) && nbt.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(id)) {
	        		ItemMeta nbtTags = i.getItemMeta();
	        		NamespacedKey pKey = new NamespacedKey(GunpowderMain.plugin, p.getName());
	        		Integer proficiency;
	        		if(nbtTags.getPersistentDataContainer().get(pKey, PersistentDataType.INTEGER) == null) {
	        			proficiency = 0;
	        		} else {
	        			proficiency = nbtTags.getPersistentDataContainer().get(pKey, PersistentDataType.INTEGER);
	        		}
	        		p.sendMessage("§eYour proficiency with this weapon is §a"+proficiency+"/10");
	        		return;
	        	}
			}
		}
		p.sendMessage("§cCannot check proficiency of this item, are you holding the correct item?");
	}
	public void lightEffect(Location loc) {
		if(!loc.getBlock().getType().equals(Material.AIR)) return;
		Block b = loc.getBlock();
		b.setType(Material.LIGHT);
		final Levelled level = (Levelled) b.getBlockData();
		level.setLevel(10);
		b.setBlockData(level, true);
		new BukkitRunnable()
		{
			Integer i = 0;
			public void run()
			   {
				if(i == 1) {
					level.setLevel(15);
					b.setBlockData(level, true);
				} else if(i == 2){
					level.setLevel(11);
					b.setBlockData(level, true);
				} else if(i == 3) {
					loc.getBlock().setType(Material.AIR);
					this.cancel();
				}
				i++;
			   }
		}.runTaskTimer(GunpowderMain.plugin, 0L, 1L);
	}

	public void reloadMusket(HandWeapon w, ItemStack item, Player p) {
		if(checkAmmo(w.getShots(), p, w.getAmmo()) == false) {
			p.sendMessage("§cNo Ammo");
			if(isReloading.containsKey(p)) {
				isReloading.remove(p);
			}
			return;
		}
		ItemMeta model = item.getItemMeta();
		model.setCustomModelData(w.getLoadingModel());
		item.setItemMeta(model);
        PotionEffect slow = new PotionEffect(PotionEffectType.SLOWNESS, (int) Math.floor(w.getReloadTime())*20, 2, false, false);
		p.addPotionEffect(slow);
		isReloading.put(p, true);
		Double time = w.getReloadTime();
		if(checkArmor(p)) {
			time = time/4;
		}
		AttributeInstance attribute = net.Indyuce.mmocore.api.player.PlayerData.get(p).getAttributes().getInstance(ConfigLoader.attribute);
        Integer level = attribute.getTotal();
        time = time * (1-((level * ConfigLoader.reloadPerLevel)/100));
        time = (double)Math.round(time*10)/10;
        if(p.isInsideVehicle()) {
        	time = time*2;
        }
		final Double fTime = time;
		new BukkitRunnable()
		{
			Double i = fTime*10;
			@SuppressWarnings("deprecation")
			public void run()
			   {
				if(i >= 0) {
					p.sendTitle(" ", "§eReloading... §7" + i/10 + "§es", 1, 20, 5);
				} else {
					p.sendTitle(" ", "§eReloaded!", 1, 20, 10);
					ItemMeta nbtTags = item.getItemMeta();
	        		NamespacedKey isLoaded = new NamespacedKey(GunpowderMain.plugin, "loaded");
	        		nbtTags.getPersistentDataContainer().set(isLoaded, PersistentDataType.INTEGER, w.getShots());
	        		item.setItemMeta(nbtTags);
	        		item.setType(w.getReloadedItem());
	        		ItemMeta model = item.getItemMeta();
	        		model.setCustomModelData(w.getReloadedModel());
	        		item.setItemMeta(model);
	        		if(p.getInventory().getItemInMainHand().getType().equals(Material.CROSSBOW)) {
	        			CrossbowMeta cbm = (CrossbowMeta) item.getItemMeta();
	        			cbm.addChargedProjectile(new ItemStack(Material.ARROW, 1));
	        			item.setItemMeta(cbm);
	        		}
                    p.removePotionEffect(PotionEffectType.SLOWNESS);
					equippedItem.put(p, item);
	        		isReloading.put(p, false);
					this.cancel();
				}
				if(!p.getInventory().getItemInMainHand().equals(item)) {
                    p.removePotionEffect(PotionEffectType.SLOWNESS);
					isReloading.put(p, false);
					ItemMeta model = item.getItemMeta();
	        		model.setCustomModelData(w.getModel());
	        		item.setItemMeta(model);
	        		String type = w.getAmmo().split("\\.")[0];
	            	String id = w.getAmmo().split("\\.")[1];
	            	ItemManager itemManager = MMOItems.plugin.getItems();
	        		MMOItem ammo = itemManager.getMMOItem(MMOItems.plugin.getTypes().get(type.toUpperCase()), id.toUpperCase());
	        		ItemStack ammoItem = ammo.newBuilder().build();
	        		ammoItem.setAmount(w.getShots());
	        		p.getInventory().addItem(ammoItem);
					this.cancel();
				}
				i--;
			   }
		}.runTaskTimer(GunpowderMain.plugin, 0L, 2L);
	}
	@EventHandler
	public void preventArrow(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			Player p = e.getPlayer();
			if(!p.getInventory().getItemInMainHand().getType().equals(Material.CROSSBOW)) return;
			NBTItem nbt = NBTItem.get(p.getInventory().getItemInMainHand());
			if(!nbt.hasType()) return;
			if(nbt.getType().equalsIgnoreCase(ConfigLoader.type)) {
				e.setCancelled(true);
			}
		}
	}
	public Boolean checkArmor(Player p) {
		ItemStack helmet = p.getInventory().getHelmet();
		ItemStack chest = p.getInventory().getChestplate();
		ItemStack legs = p.getInventory().getLeggings();
		ItemStack boots = p.getInventory().getBoots();
		Boolean hasArmour = false;
		if(helmet != null && NBTItem.get(helmet).hasType() == true && ConfigLoader.helmets.contains(NBTItem.get(helmet).getType().toLowerCase() + "." + NBTItem.get(helmet).getString("MMOITEMS_ITEM_ID").toLowerCase())) {
			hasArmour = true;
		} else {
			return false;
		}
		if(chest != null && NBTItem.get(chest).hasType() == true && ConfigLoader.chestplates.contains(NBTItem.get(chest).getType().toLowerCase() + "." + NBTItem.get(chest).getString("MMOITEMS_ITEM_ID").toLowerCase())) {
			hasArmour = true;
		} else {
			return false;
		}
		if(legs != null && NBTItem.get(legs).hasType() == true && ConfigLoader.leggings.contains(NBTItem.get(legs).getType().toLowerCase() + "." + NBTItem.get(legs).getString("MMOITEMS_ITEM_ID").toLowerCase())) {
			hasArmour = true;
		} else {
			return false;
		}
		if(boots != null && NBTItem.get(boots).hasType() == true && ConfigLoader.boots.contains(NBTItem.get(boots).getType().toLowerCase() + "." + NBTItem.get(boots).getString("MMOITEMS_ITEM_ID").toLowerCase())) {
			hasArmour = true;
		} else {
			return false;
		}
		if(hasArmour == true) return true;
		return false;
	}
	public Boolean checkAmmo(Integer amount, Player p, String mmoitem) {
		String type = mmoitem.split("\\.")[0];
    	String id = mmoitem.split("\\.")[1];
		Inventory i = p.getInventory();
		Integer counter = 0;
		Boolean hasAmmo = false;
		while(counter <= i.getSize()) {
			ItemStack slot = i.getItem(counter);
			NBTItem nbt = NBTItem.get(slot);
			if(nbt.hasType()) {
				if(nbt.getType().equalsIgnoreCase(type) && nbt.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(id) && slot.getAmount() >= amount) {
					hasAmmo = true;
					slot.setAmount(slot.getAmount()-amount);
					break;
				}
			}
			counter++;
		}
		if(hasAmmo) {
			return true;
		} else {
			return false;
		}
	}
}
