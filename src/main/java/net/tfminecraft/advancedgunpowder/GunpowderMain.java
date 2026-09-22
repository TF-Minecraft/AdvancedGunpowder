package net.tfminecraft.advancedgunpowder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GunpowderMain extends JavaPlugin{
	FileConfiguration config = getConfig();
	
	public static GunpowderMain plugin;
	ConfigLoader loader = new ConfigLoader();
	GunpowderEvents events = new GunpowderEvents();
	CommandManager commands = new CommandManager();
	
	@Override
	public void onEnable(){
		plugin = this;
		
		loader.loadConfig(config);
		
		getServer().getPluginManager().registerEvents(events, plugin);
		getCommand(commands.cmd1).setExecutor(commands);
		getCommand(commands.cmd1).setTabCompleter(new TabCompletion());
	}
	public void reloadConfigCommand() {
		loader.loadConfig(config);
	}
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public void reloadConfigPCommand(Player p) {
		p.sendMessage(ChatColor.GREEN + "[AdvancedGunpowder]" + ChatColor.YELLOW + " Reloading plugin...");
		this.reloadConfig();
		config = getConfig();
		loader.loadConfig(config);
		p.sendMessage(ChatColor.GREEN + "[AdvancedGunpowder]" + ChatColor.YELLOW + " Reloading complete!");
	}
}
