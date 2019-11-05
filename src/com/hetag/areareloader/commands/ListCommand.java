package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class ListCommand extends ARCommand {
	static String path = "Commands.List.Description";

	public ListCommand() {
		super("list", "/ar list", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)),
				new String[] { "list" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		ConfigurationSection cs = AreaReloader.areas.getConfig().getConfigurationSection("Areas");
		if (cs != null) {
			sender.sendMessage(prefix + ChatColor.AQUA + "Areas:");
			for (String areaName : cs.getKeys(false)) {
				sender.sendMessage(ChatColor.DARK_AQUA + areaName + ChatColor.DARK_GRAY + " (" + "" + ChatColor.DARK_AQUA + "X: " + ChatColor.AQUA + AreaMethods.getXCoord(areaName) + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Z: " + ChatColor.AQUA + AreaMethods.getZCoord(areaName) + ChatColor.DARK_GRAY + ")");
			}
		} else {
			sender.sendMessage(prefix + notFound());
		}
	}

	private String notFound() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.List.NoAreasFound"));

	}
}
