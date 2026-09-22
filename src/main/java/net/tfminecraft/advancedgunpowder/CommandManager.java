package net.tfminecraft.advancedgunpowder;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager implements Listener, CommandExecutor{
	public String cmd1 = "agp";
	
	GunpowderEvents events = new GunpowderEvents();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase(cmd1)) {
			if(args[0].equalsIgnoreCase("checkproficiency")) {
				if(sender instanceof Player) {
					events.checkProficiency((Player) sender);
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("reload")) {
				if(Permissions.isAdmin(sender) == false) {
					sender.sendMessage("§cYou do not have access to this command!");
					return false;
				}
				if(sender instanceof Player) {
					Player p = (Player) sender;
					JavaPlugin.getPlugin(GunpowderMain.class).reloadConfigPCommand(p);
				} else {
					JavaPlugin.getPlugin(GunpowderMain.class).reloadConfigCommand();
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("setproficiency") && args.length == 3) {
				if(Permissions.isAdmin(sender) == false) {
					sender.sendMessage("§cYou do not have access to this command!");
					return false;
				}
				if(sender instanceof Player) {
					Player p = (Player) sender;
					Player target = Bukkit.getPlayerExact(args[2]);
					Integer amount = Integer.parseInt(args[1]);
					ItemUtil u = new ItemUtil();
					u.setProficiency(p, target, p.getInventory().getItemInMainHand(), amount);
				} 
				return true;
			}
		}
		return false;
	}
}
