package com.hetag.areareloader.commands;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
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
	public BukkitRunnable br;
	static String path = "Commands.Display.Description";

	public DisplayCommand() {
		super("display", "/ar display <area>", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] { "display" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1) && isPlayer(sender)) {
			return;
		}
		String area = args.get(0);
		Player player = (Player) sender;
		if (AreaReloader.areas.getConfig().contains("Areas." + args.get(0))) {
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
					Location corner1 = new Location(player.getWorld(), AreaMethods.getAreaX(area), 1, AreaMethods.getAreaZ(area));
					Location corner2 = new Location(player.getWorld(), AreaMethods.getAreaMaxX(area), player.getWorld().getMaxHeight(), AreaMethods.getAreaMaxZ(area));
					for (Location finalLoc : getHollowCube(corner1, corner2, 0.25)) {
						ParticleEffect.FLAME.display(finalLoc, 1, 0.05F, 0.05F, 0.05F, 0.05F);
						ParticleEffect.FLAME.display(finalLoc, 1);
					}
				}
			};
			br.runTaskTimerAsynchronously(AreaReloader.plugin, 1, 12);
		}
	}
	
	public static String onDisplay() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.Display.onDisplay"));
	}
	
	public static String onDisplayRemove() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.Display.onDisplayRemove"));
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
