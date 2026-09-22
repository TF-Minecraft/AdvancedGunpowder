package net.tfminecraft.advancedgunpowder;

import org.bukkit.Material;

public class HandWeapon {
	public String id;
	public String mmoitem;
	public String ammo;
	public String sfx;
	public Double reloadTime;
	public Double equipTime;
	public Integer loadingModel;
	public Integer model;
	public Integer reloadedModel;
	public Integer shots;
	public Double pierceRating;
	public Material item;
	public Material reloadedItem;
	
	public Double getPierceRating() {
		return pierceRating;
	}
	public void setPierceRating(Double pierceRating) {
		this.pierceRating = pierceRating;
	}
	public String getSfx() {
		return sfx;
	}
	public void setSfx(String sfx) {
		this.sfx = sfx;
	}
	public Double getEquipTime() {
		return equipTime;
	}
	public void setEquipTime(Double equipTime) {
		this.equipTime = equipTime;
	}
	public Integer getShots() {
		return shots;
	}
	public void setShots(Integer shots) {
		this.shots = shots;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMMOItem() {
		return mmoitem;
	}
	public void setMMOItem(String mmoitem) {
		this.mmoitem = mmoitem;
	}
	public String getAmmo() {
		return ammo;
	}
	public void setAmmo(String ammo) {
		this.ammo = ammo;
	}
	public Double getReloadTime() {
		return reloadTime;
	}
	public void setReloadTime(Double reloadTime) {
		this.reloadTime = reloadTime;
	}
	public Integer getLoadingModel() {
		return loadingModel;
	}
	public void setLoadingModel(Integer loadingModel) {
		this.loadingModel = loadingModel;
	}
	public Integer getModel() {
		return model;
	}
	public void setModel(Integer model) {
		this.model = model;
	}
	public Integer getReloadedModel() {
		return reloadedModel;
	}
	public void setReloadedModel(Integer reloadedModel) {
		this.reloadedModel = reloadedModel;
	}
	public Material getItem() {
		return item;
	}
	public void setItem(Material item) {
		this.item = item;
	}
	public Material getReloadedItem() {
		return reloadedItem;
	}
	public void setReloadedItem(Material reloadedItem) {
		this.reloadedItem = reloadedItem;
	}
	
}
