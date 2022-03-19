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

public class LoadCommand extends ARCommand {
	static String path = "Commands.Load.Description";

	public LoadCommand() {
		super("load", "/ar load <name>", formatColors(Manager.getConfig().getString(path)), new String[] { "load" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		if (args.size() <= 0) {
			sender.sendMessage(this.getProperUsage());
			return;
		}
		String area = args.get(0);
		if (Manager.areas.getConfig().contains("Areas." + args.get(0))) {
			if (!AreaReloader.getInstance().getQueue().isQueued(area)) {
			World world = Bukkit.getWorld(Manager.areas.getConfig().getString("Areas." + args.get(0) + ".World"));
			int x = Manager.areas.getConfig().getInt("Areas." + args.get(0) + ".X");
			int z = Manager.areas.getConfig().getInt("Areas." + args.get(0) + ".Z");
			Location location = new Location(world, x, AreaMethods.getAreaY(area), z);
			new AreaLoader(area, AreaMethods.getAreaSizeX(area).intValue(), AreaMethods.getAreaSizeZ(area).intValue(), AreaMethods.getAreaChunk(area).intValue(), location, sender);
			//AreaLoader.manage();
			sendMessage(sender, onPrepare().replaceAll("%area%", area), true);
			} else {
				sendMessage(sender, onAlreadyLoading().replaceAll("%area%", area), true);
			}
		} else {
			sendMessage(sender, onInvalid().replaceAll("%area%", area), true);
		}
	}

	private String onPrepare() {
		return formatColors(Manager.getConfig().getString("Commands.Load.OnPrepare"));
	}

	public static String onInvalid() {
		return formatColors(Manager.getConfig().getString("Commands.Load.OnInvalidArea"));
	}
	
	private String onAlreadyLoading() {
		return formatColors(Manager.getConfig().getString("Commands.Load.OnAlreadyLoading"));
	}
}