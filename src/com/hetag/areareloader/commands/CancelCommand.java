package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaLoader;
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
		try {
			if (input.equalsIgnoreCase("all")) {
				if (!AreaReloader.getInstance().getQueue().get().isEmpty()) {
					AreaReloader.getInstance().getServer().getScheduler().cancelTasks(AreaReloader.getInstance());
					AreaLoader.areas.clear();
					AreaReloader.getInstance().getQueue().get().clear();
				}
				this.sendMessage(sender, this.onCancelAll(), true);
				return;
			} else {
				if (AreaReloader.getInstance().getQueue().isQueued(input)) {
					this.sendMessage(sender, this.onCancelArea().replace("%area%", input).replace("%id%", String.valueOf(AreaReloader.getInstance().getQueue().getTaskByName(input))), true);
					AreaMethods.kill(input);
					return;
				} else {
					this.sendMessage(sender, this.onCancelFail().replace("%area%", input), true);
					return;
				}
			}
		} catch (Exception e) {
			Manager.printDebug(this.getName(), e, sender);
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
