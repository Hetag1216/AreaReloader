package com.hetag.areareloader.commands;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.AreaScheduler;
import com.hetag.areareloader.configuration.Manager;

public class ConfigReloadCommand extends ARCommand {
	static String path = "Commands.Reload.Description";

	public ConfigReloadCommand() {
		super("reload", "/ar reload", formatColors(Manager.getConfig().getString(path)), new String[] { "reload" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		try {
			Manager.resetDebug();
			DisplayCommand.removeAllDisplays();
			AreaMethods.performSetup();
			Manager.reloadAllInstances();
			if (!AreaReloader.getInstance().getServer().getScheduler().getPendingTasks().isEmpty()) {
			AreaReloader.getInstance().getServer().getScheduler().getPendingTasks().clear();
			}
			if (!AreaReloader.getInstance().getQueue().get().isEmpty()) {
				AreaReloader.getInstance().getQueue().get().clear();
			}
			if (AreaReloader.checker) {
				AreaScheduler.checkForAreas();
				AreaScheduler.manageTimings();
				if (AreaScheduler.getAreas() != null) {
				AreaScheduler.updateDelay(AreaScheduler.getAreas(), AreaScheduler.getAreasResetTime());
				}
			}
			sendMessage(sender, onReload(), true);
		} catch (Exception e) {
			sendMessage(sender, onFail(), true);
			e.printStackTrace();
		}

	}

	public String onReload() {
		return Manager.getConfig().getString("Commands.Reload.OnReload");
	}

	public String onFail() {
		return Manager.getConfig().getString("Commands.Reload.OnFail");
	}
}
