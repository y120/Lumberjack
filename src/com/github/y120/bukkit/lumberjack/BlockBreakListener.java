package com.github.y120.bukkit.lumberjack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
	
	private Lumberjack plugin;
	public BlockBreakListener(Lumberjack instance) {
		plugin = instance;
	}
	
	static int dx[] = {0,0,-1,1}, dz[] = {-1,1,0,0};
	static Material[] ok = {
		Material.WOOD_AXE, 
		Material.STONE_AXE, 
		Material.IRON_AXE,
		Material.GOLD_AXE, 
		Material.DIAMOND_AXE
	};
	
	private void chop(World w, int x, int y, int z) {
		if (!w.getBlockAt(x, y, z).breakNaturally()) {
			plugin.getLogger().warning("Could not break wood block at (" + x + ", " + y + ", " + z + ")!");
			return;
		}
		for (int i = 0; i < dx.length; i++)
			if (w.getBlockAt(x + dx[i],  y, z + dz[i]).getType() == Material.LOG)
				chop(w, x + dx[i], y, z + dz[i]);
		if (w.getBlockAt(x,  y + 1, z).getType() == Material.LOG)
			chop(w, x, y + 1, z);
		if (w.getBlockAt(x,  y - 1, z).getType() == Material.LOG)
			chop(w, x, y - 1, z);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {		
		Player player = e.getPlayer();
		if (!player.hasPermission("lumberjack.timber"))
			return;
		
		Block block = e.getBlock();
		if (block.getType() != Material.LOG)
			return; // must be wood
		
		boolean okay = false;
		for (int i = 0; !okay && i < ok.length; i++)
			okay |= player.getItemInHand().getType() == ok[i];
		if (!okay)
			return; // must destroy with axe

		Location loc = block.getLocation();
		World w = loc.getWorld();
		
		if (w.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()).getType() == Material.LOG)
			return; // bottom block only
		
		for (int i = 0; i < dx.length; i++)
			if (w.getBlockAt(loc.getBlockX() + dx[i], loc.getBlockY(), loc.getBlockZ() + dz[i])
					.getType() == Material.LOG) return; // cannot be surrounded by wood
		
		okay = false;
		for (int dy = 1; !okay && dy < 9; dy++)
			for (int i = 0; !okay && i < dx.length; i++)
				okay |= w.getBlockAt(loc.getBlockX() + dx[i], loc.getBlockY() + dy, loc.getBlockZ() + dz[i])
						.getType() == Material.LEAVES; // must have leaves somewhere nearby
		if (!okay)
			return;
		
		// checks out, let's chop the tree!
		chop(w, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
}
