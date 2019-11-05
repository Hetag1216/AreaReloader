package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class InfoCommand extends ARCommand {
	static String path = "Commands.Info.Description";
	
	public InfoCommand() {
		super("info", "/ar info <area>",  ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] {"info"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		if (args.size() == 0) {
			sender.sendMessage(getProperUsage());
		}
		String area = args.get(0);
		if (AreaReloader.areas.getConfig().contains("Areas." + area)) {
			sender.sendMessage(prefix);
			sender.sendMessage(ChatColor.DARK_GRAY + "«" + ChatColor.AQUA + area + ChatColor.DARK_GRAY + "»");
			sender.sendMessage(ChatColor.DARK_AQUA + "World" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + AreaMethods.getAreaInWorld(area));
			sender.sendMessage(ChatColor.DARK_AQUA + "Location X" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + AreaMethods.getXCoord(area));
			//sender.sendMessage(ChatColor.DARK_AQUA + "Location Y" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + AreaMethods.getYCoord(area));
			sender.sendMessage(ChatColor.DARK_AQUA + "Location Z" + ChatColor.DARK_GRAY + " » " + ChatColor.AQUA + AreaMethods.getZCoord(area));
			return;
		}
	}
}
