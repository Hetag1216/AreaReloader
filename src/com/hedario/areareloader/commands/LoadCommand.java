package com.hedario.areareloader.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.hedario.areareloader.AreaLoader;
import com.hedario.areareloader.AreaMethods;
import com.hedario.areareloader.AreaReloader;
import com.hedario.areareloader.configuration.Manager;

public class LoadCommand extends ARCommand {
	public LoadCommand() {
		super("load", "/ar load <name>", Manager.getConfig().getString("Commands.Load.Description"), new String[] { "load" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 1, 1)) {
			return;
		}
		String area = args.get(0);
		if (Manager.areas.getConfig().contains("Areas." + args.get(0))) {
			if (!AreaReloader.getQueue().isQueued(area)) {
			Location location = new Location(AreaMethods.getWorld(area), AreaMethods.getAreaX(area), AreaMethods.getAreaY(area), AreaMethods.getAreaZ(area));
			new AreaLoader(area, AreaMethods.getAreaSizeX(area), AreaMethods.getAreaSizeZ(area), AreaMethods.getAreaChunk(area), location, sender);
			sendMessage(sender, prepare().replaceAll("%area%", area), true);
			} else {
				sendMessage(sender, alreadyLoading().replaceAll("%area%", area), true);
			}
		} else {
			sendMessage(sender, invalidArea().replaceAll("%area%", area), true);
		}
	}

	private String prepare() {
		return Manager.getConfig().getString("Commands.Load.Prepare");
	}

	public static String invalidArea() {
		return Manager.getConfig().getString("Commands.Load.InvalidArea");
	}
	
	private String alreadyLoading() {
		return Manager.getConfig().getString("Commands.Load.AlreadyLoading");
	}
	
	@Override
	protected List<String> getTabCompletion(final CommandSender sender, final List<String> args) {
		List<String> list = new ArrayList<String>();
		if (!sender.hasPermission("areareloader.command.load") || args.size() >= 1) {
			return new ArrayList<String>();
		}
		for (final String map : AreaMethods.getAreas()) {
			list.add(map);
		}
		return list;
	}
}