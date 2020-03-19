package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.AreaScheduler;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class InfoCommand extends ARCommand {
	static String path = "Commands.Info.Description";
	
	public InfoCommand() {
		super("info", "/ar info <area>",  formatColors(Manager.getConfig().getString(path)), new String[] {"info"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		if (args.size() == 0) {
			sendMessage(sender, getProperUsage(), false);
			return;
		}
		String area = args.get(0);
		String display = null;
		if (DisplayCommand.display.contains(area)) {
			display = ChatColor.AQUA + "true";
		} else {
			display = ChatColor.AQUA + "false";
		}
		if (AreaReloader.areas.getConfig().contains("Areas." + area)) {
			sender.sendMessage(prefix);
			sender.sendMessage(ChatColor.DARK_GRAY + "«" + ChatColor.AQUA + area + ChatColor.DARK_GRAY + "»");
			sender.sendMessage(ChatColor.DARK_AQUA + "World" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + AreaMethods.getAreaInWorld(area));
			sender.sendMessage(ChatColor.DARK_AQUA + "Location X" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + AreaMethods.getAreaX(area) + ChatColor.GRAY + " | " + ChatColor.AQUA + AreaMethods.getAreaMaxX(area));
			sender.sendMessage(ChatColor.DARK_AQUA + "Location Z" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + AreaMethods.getAreaZ(area) + ChatColor.GRAY + " | " + ChatColor.AQUA + AreaMethods.getAreaMaxZ(area));
			sender.sendMessage(ChatColor.DARK_AQUA + "Auto Reloading" + ChatColor.DARK_AQUA + " » " + ChatColor.AQUA + AreaReloader.areas.getConfig().getBoolean("Areas." + area + ".AutoReload.Enabled"));
			sender.sendMessage(ChatColor.DARK_AQUA + "Auto Reloading Time " + ChatColor.DARK_AQUA + " » " + ChatColor.AQUA + AreaReloader.areas.getConfig().getLong("Areas." + area + ".AutoReload.Time"));
			sender.sendMessage(ChatColor.DARK_AQUA + "Time Left " + ChatColor.DARK_AQUA + " » " + ChatColor.AQUA + AreaScheduler.getRemainingTime(area));
			sender.sendMessage(ChatColor.DARK_AQUA + "Is being displayed " + ChatColor.DARK_AQUA + " » " + display);
			return;
		} else {
			sendMessage(sender, LoadCommand.onInvalid().replaceAll("%area%", area), true);
		}
	}
}
