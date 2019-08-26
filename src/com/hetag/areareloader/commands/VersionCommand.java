package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class VersionCommand extends ARCommand {
	static String path = "Commands.Version.Description";

	public VersionCommand() {
		super("version", "/ar version",
				ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)),
				new String[] { "version", "v" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		sender.sendMessage(prefix);
		sender.sendMessage(
				ChatColor.DARK_AQUA + "Version: " + ChatColor.AQUA + AreaReloader.plugin.getDescription().getVersion());
		sender.sendMessage(
				ChatColor.DARK_AQUA + "Author: " + ChatColor.AQUA + AreaReloader.plugin.getDescription().getAuthors());
		sender.sendMessage(ChatColor.DARK_AQUA + "Compatible Minecraft Version(s): " + ChatColor.AQUA + "1.13.2");
	}
}
