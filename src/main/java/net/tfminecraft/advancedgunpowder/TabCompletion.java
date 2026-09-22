package net.tfminecraft.advancedgunpowder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabCompletion implements TabCompleter{
    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
    	
	        if(cmd.getName().equalsIgnoreCase("agp") && args.length >= 0 && args.length < 2 && !(args[0].equalsIgnoreCase("checkproficiency") || args[0].equalsIgnoreCase("reload"))){
	            if(sender instanceof Player){
	                List<String> completions = new ArrayList<>();
	                
	                completions.add("checkproficiency");
	                if(Permissions.isAdmin(sender)) {
	                	completions.add("reload");
	                }
	                
	                return completions;
	            }
	            }
    	return null;
    }
}
