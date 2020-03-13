package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class VersionCommand extends ARCommand {
	static String path = "Commands.Version.Description";

	public VersionCommand() {
		super("version", "/ar version", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] { "version", "v" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		sendMessage(sender, "", true);
		sendMessage(sender, "&3Version: &b" + AreaReloader.getInstance().getDescription().getVersion(), false);
		sendMessage(sender, "&3Author: &b" + AreaReloader.getInstance().getDescription().getAuthors().toString().replace("[", "").toString().replace("]", ""), false);
		sendMessage(sender, "&3Compatible Minecraft Version(s): &b 1.13 - 1.14 - 1.15", false);
		sendMessage(sender, "&3Using Protocol: &b" + AreaReloader.getProtocol().toString(), false);
	}
}
