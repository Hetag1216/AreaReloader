package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaLoader;
import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class LoadCommand extends ARCommand {
	static String path = "Commands.Load.Description";

	public LoadCommand() {
		super("load", "/ar load <name>", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] { "load" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		if (args.size() <= 0) {
			sender.sendMessage(this.getProperUsage());
			return;
		}
		String area = args.get(0);
		if (AreaReloader.areas.getConfig().contains("Areas." + args.get(0))) {
			World world = Bukkit.getWorld(AreaReloader.areas.getConfig().getString("Areas." + args.get(0) + ".World"));
			int x = AreaReloader.areas.getConfig().getInt("Areas." + args.get(0) + ".X");
			int z = AreaReloader.areas.getConfig().getInt("Areas." + args.get(0) + ".Z");
			Location location = new Location(world, x, 0.0D, z);
			new AreaLoader(area, AreaMethods.getAreaMaxX(area).intValue(), AreaMethods.getAreaMaxZ(area).intValue(), AreaMethods.getAreaChunk(area).intValue(), location, sender);
			sender.sendMessage(prefix + onPrepare().replaceAll("%area%", area));
		} else {
			sender.sendMessage(prefix + onInvalid().replaceAll("%area%", area));
		}
	}

	private String onPrepare() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.Load.onPrepare"));
	}

	public static String onInvalid() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.Load.onInvalidArea"));
	}
}
