package com.hetag.areareloader.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;
import com.hetag.areareloader.effects.ParticleEffect;

import net.md_5.bungee.api.ChatColor;

public class DisplayCommand extends ARCommand {

	public static ArrayList<String> display = new ArrayList<String>();
	public long pDelay;
	public BukkitRunnable br;
	static String path = "Commands.Display.Description";

	public DisplayCommand() {
		super("display", "/ar display <area>", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] { "display" });
		pDelay = Manager.getConfig().getLong("Commands.Display.ParticleDelay");
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1) && isPlayer(sender)) {
			return;
		}
		
		if (args.size() == 0) {
			sendMessage(sender, getProperUsage(), false);
			return;
		}
		String area = args.get(0);
		if (AreaReloader.areas.getConfig().contains("Areas." + area)) {
			if (display.contains(area)) {
				display.remove(area);
				sender.sendMessage(prefix + onDisplayRemove().replaceAll("%area%", area));
				br.cancel();
			} else {
				if (!display.isEmpty()) {
					display.clear();
					br.cancel();
				}
				display.add(area);
				sender.sendMessage(prefix + onDisplay().replaceAll("%area%", area));
			}
		} else {
			sender.sendMessage(prefix + LoadCommand.onInvalid().replaceAll("%area%", area));
		}
		
		if (display.contains(area)) {
			br = new BukkitRunnable() {
				public void run() {
					Location corner1 = new Location(Bukkit.getWorld(AreaMethods.getAreaInWorld(area)), AreaMethods.getAreaX(area), AreaMethods.getAreaY(area), AreaMethods.getAreaZ(area));
					Location corner2 = new Location(Bukkit.getWorld(AreaMethods.getAreaInWorld(area)), AreaMethods.getAreaMaxX(area), AreaMethods.getAreaMaxY(area), AreaMethods.getAreaMaxZ(area));
					for (Location finalLoc : getHollowCube(corner1, corner2, 0.25)) {
						if (!useParticles()) {
							Player player = null;
							if (restrictVision()) {
								player = (Player) sender;
							} else {
								for (Player online : Bukkit.getServer().getOnlinePlayers()) {
									player = online;
								}
							}
							blockChange(player, finalLoc.getBlock());
						} else {
							ParticleEffect.FLAME.display(finalLoc, 1, 0.05F, 0.05F, 0.05F, 0.05F);
							ParticleEffect.FLAME.display(finalLoc, 1);
						}
					}
				}
			};
			br.runTaskTimerAsynchronously(AreaReloader.plugin, 0, pDelay * 20 / 1000);
		}
	}
	
	private void blockChange(Player player, Block block) {
		player.sendBlockChange(block.getLocation(), Material.matchMaterial(match()).createBlockData());
	}
	
	public static String onDisplay() {
		return formatColors(Manager.getConfig().getString("Commands.Display.OnDisplay"));
	}
	
	public static String onDisplayRemove() {
		return formatColors(Manager.getConfig().getString("Commands.Display.OnDisplayRemove"));
	}
	
	public boolean useParticles() {
		return Manager.getConfig().getBoolean("Commands.Display.UseParticles");
	}
	
	public boolean restrictVision() {
		return Manager.getConfig().getBoolean("Commands.Display.Block.RestrictVision");
	}
	
	public String match() {
		return Manager.getConfig().getString("Commands.Display.Block.Material");
	}

	public List<Location> getHollowCube(Location corner1, Location corner2, double point) {
		List<Location> result = new ArrayList<Location>();
		World world = corner1.getWorld();
		double minX = Math.min(corner1.getX(), corner2.getX());
		double minY = Math.min(corner1.getY(), corner2.getY());
		double minZ = Math.min(corner1.getZ(), corner2.getZ());
		double maxX = Math.max(corner1.getX(), corner2.getX());
		double maxY = Math.max(corner1.getY(), corner2.getY());
		double maxZ = Math.max(corner1.getZ(), corner2.getZ());

		for (double x = minX; x <= maxX; x += point) {
			for (double y = minY; y <= maxY; y += point) {
				for (double z = minZ; z <= maxZ; z += point) {
					int components = 0;
					if (x == minX || x == maxX)
						components++;
					if (y == minY || y == maxY)
						components++;
					if (z == minZ || z == maxZ)
						components++;
					if (components >= 2) {
						result.add(new Location(world, x, y, z));
					}
				}
			}
		}
		return result;
	}
}