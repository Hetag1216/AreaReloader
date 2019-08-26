package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

public class DeleteCommand extends ARCommand {
	static String path = "Commands.Delete.Description";

	public DeleteCommand() {
		super("delete", "/ar delete <name>",
				ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)),
				new String[] { "delete" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		String area = args.get(0);
		if (AreaReloader.areas.getConfig().contains("Areas." + args.get(0))) {
			sender.sendMessage(prefix + onDelete().replaceAll("%area%", args.get(0)));
			AreaMethods.deleteArea(area);
			return;
		}
		sender.sendMessage(prefix + onInvalidArea().replaceAll("%area%", args.get(0)));

	}

	private String onDelete() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Commands.Delete.OnDelete"));
	}

	private String onInvalidArea() {
		return ChatColor.translateAlternateColorCodes('&',
				Manager.getConfig().getString("Commands.Delete.InvalidArea"));

	}

}
