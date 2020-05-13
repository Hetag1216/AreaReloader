package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaLoader;
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
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		try {
			AreaMethods.performSetup();
			Manager.defaultConfig.reloadConfig();
			AreaReloader.areas.reloadConfig();
			AreaReloader.getInstance().getServer().getScheduler().getPendingTasks().clear();
			AreaLoader.manage();
			if (AreaReloader.checker) {
				AreaScheduler.checkForAreas();
				AreaScheduler.manageReloading();
				AreaScheduler.updateDelay(AreaScheduler.getAreas(), AreaScheduler.getAreasResetTime());
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
