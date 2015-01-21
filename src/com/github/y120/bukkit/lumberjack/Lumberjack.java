package com.github.y120.bukkit.lumberjack;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Lumberjack extends JavaPlugin {
	
	private static boolean enabled = true;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("lj") || cmd.getName().equalsIgnoreCase("lumberjack")) {
			if (args.length == 0)
				if (!(sender instanceof Player) || sender.hasPermission("lumberjack.check")) {
					sender.sendMessage("Felling trees is currently " + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.RESET + ".");
					return true;
				} else
					return false;
			else if (args.length == 1 && args[0].equalsIgnoreCase("toggle"))
				if (!(sender instanceof Player) || sender.hasPermission("lumberjack.toggle")) {
					enabled = !enabled;
					if (enabled)
						getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
					else
						HandlerList.unregisterAll(this);
					sender.sendMessage("Felling trees is now " + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.RESET + ".");
					return true;
				} else
					return false;
		}
		return false;
	}
}
