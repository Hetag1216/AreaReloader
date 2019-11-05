package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class ConfigReloadCommand extends ARCommand {
	static String path = "Commands.Reload.Description";

	public ConfigReloadCommand() {
		super("reload", "/ar reload",  ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] {"reload"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		AreaMethods.reloadConfig();
		sender.sendMessage(prefix + ChatColor.GREEN + "Succesfully reloaded the config!");
		
	}

}
