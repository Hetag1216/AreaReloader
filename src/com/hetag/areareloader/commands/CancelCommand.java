package com.hetag.areareloader.commands;

import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

public class CancelCommand extends ARCommand {
	static String path = "Commands.Cancel.Description";

	public CancelCommand() {
		super("cancel", "/ar cancel <area, ALL>", formatColors(Manager.getConfig().getString(path)), new String[] { "cancel", "c" });

	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 1, 1)) {
			return;
		}
		String input = args.get(0);
		if (input.equalsIgnoreCase("all")) {
			for (Iterator<String> IDs = AreaReloader.getInstance().getQueue().get().keySet().iterator(); IDs.hasNext();) {
				AreaMethods.kill(IDs.next());
			}
			this.sendMessage(sender, this.onCancelAll(), true);
			return;
		} else {
			if (AreaReloader.getInstance().getQueue().isQueued(input)) {
				AreaMethods.kill(input);
				this.sendMessage(sender, this.onCancelArea().replace("%area%", input).replace("%id%", String.valueOf(AreaReloader.getInstance().getQueue().getTaskByName(input))), true);
				return;
			} else {
				this.sendMessage(sender, this.onCancelFail().replace("%area%", input), true);
				return;
			}
		}
	}

	private String onCancelArea() {
		return formatColors(Manager.getConfig().getString("Commands.Cancel.OnCancelArea"));
	}

	private String onCancelFail() {
		return formatColors(Manager.getConfig().getString("Commands.Cancel.OnCancelFail"));
	}

	private String onCancelAll() {
		return formatColors(Manager.getConfig().getString("Commands.Cancel.OnCancelAll"));
	}
}
