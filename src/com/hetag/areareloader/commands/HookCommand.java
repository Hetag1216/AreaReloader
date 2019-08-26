package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class HookCommand extends ARCommand {
	static String path = "Commands.Hook.Description";

	public HookCommand() {
		super("hook", "/ar hook", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)),
				new String[] { "hook", "hooks" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "-=-=-=-= Hooks =-=-=-=-");
		sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "WorldEdit");
		sender.sendMessage(AreaReloader.plugin.getStatus());
		if (AreaReloader.plugin.wep != null) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Version: " + ChatColor.AQUA
					+ AreaReloader.plugin.wep.getDescription().getVersion());
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "-=-=-=-= -=- =-=-=-=-");
	}
}
