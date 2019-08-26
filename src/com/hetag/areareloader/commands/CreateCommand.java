package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;
import com.sk89q.worldedit.WorldEditException;

import net.md_5.bungee.api.ChatColor;

public class CreateCommand extends ARCommand {
	static String path = "Commands.Create.Description";

	public CreateCommand() {
		super("create", "/ar create <name>",
				ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)),
				new String[] { "create" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !isPlayer(sender)) {
			return;
		}
		if (args.size() == 0) {
			sender.sendMessage(getProperUsage());
		}
		if (args.size() == 1) {
			if (AreaReloader.areas.getConfig().contains("Areas." + args.get(0))) {
				sender.sendMessage(prefix + onCreateExists().replaceAll("%area%", args.get(0)));
				return;
			}
			try {
				if (AreaMethods.createNewArea((Player) sender, args.get(0), 16)) {
					sender.sendMessage(prefix + onCreateSuccess().replaceAll("%area%", args.get(0)));
				} else {
					sender.sendMessage(prefix + onCreateFail().replaceAll("%area%", args.get(0)));
				}
			} catch (WorldEditException e) {
				e.printStackTrace();
			}
		}
		if (args.size() > 1) {
			sender.sendMessage(getProperUsage());
		}
	}

	private String onCreateExists() {
		return ChatColor.translateAlternateColorCodes('&',
				Manager.getConfig().getString("Commands.Create.AlreadyExists"));
	}

	private String onCreateSuccess() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.Create.OnSuccess"));
	}

	private String onCreateFail() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.Create.OnFailure"));
	}

}
