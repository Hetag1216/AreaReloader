package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.configuration.Manager;

public class DeleteCommand extends ARCommand {
	static String path = "Commands.Delete.Description";

	public DeleteCommand() {
		super("delete", "/ar delete <name>", formatColors(Manager.getConfig().getString(path)), new String[] { "delete" });
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
		if (Manager.areas.getConfig().contains("Areas." + area)) {
			sendMessage(sender, onDelete().replaceAll("%area%", area), true);
			if (DisplayCommand.getDisplayedAreas().contains(area)) {
				DisplayCommand.getDisplayedAreas().remove(area);
			}
			AreaMethods.deleteArea(area);
			return;
		}
		sendMessage(sender, onInvalidArea().replaceAll("%area%", area), true);

	}

	private String onDelete() {
		return formatColors(Manager.getConfig().getString("Commands.Delete.OnDelete"));
	}

	private String onInvalidArea() {
		return formatColors(Manager.getConfig().getString("Commands.Delete.InvalidArea"));
	}

}
