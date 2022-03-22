package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

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
		input = input.toLowerCase();
		switch (input) {
		default:
			if (AreaReloader.getInstance().getQueue().isQueued(input)) {
				AreaReloader.getInstance().getQueue().remove(input, AreaReloader.getInstance().getQueue().getTaskByName(input)); 
				this.sendMessage(sender, this.onCancelArea().replace("%area%", input).replace("%id%", String.valueOf(AreaReloader.getInstance().getQueue().getTaskByName(input))), true);
				} else {
					this.sendMessage(sender, this.onCancelFail().replace("%area%", input), true);
					return;
				}
			break;
		case "all":
			//subsequentially remove all areas.
			for (String ID : AreaReloader.getInstance().getQueue().get().keySet()) {
				AreaReloader.getInstance().getQueue().remove(ID, AreaReloader.getInstance().getQueue().getTaskByName(ID)); 
			}
			this.sendMessage(sender, this.onCancelAll(), true);
			break;
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
