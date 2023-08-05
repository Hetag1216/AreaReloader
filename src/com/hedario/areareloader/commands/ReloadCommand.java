package com.hedario.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areareloader.AreaMethods;
import com.hedario.areareloader.AreaReloader;
import com.hedario.areareloader.AreaScheduler;
import com.hedario.areareloader.configuration.Manager;

public class ReloadCommand extends ARCommand {
	public ReloadCommand() {
		super("reload", "/ar reload", Manager.getConfig().getString("Commands.Reload.Description"), new String[] { "reload" });
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
			Manager.reloadConfigurations();
			AreaScheduler.init();
			new Executor(AreaReloader.getInstance());
			if (AreaReloader.checker) {
				if (AreaScheduler.getAreas() != null) {
					AreaScheduler.updateDelay(AreaScheduler.getAreas(), AreaScheduler.getAreasResetTime());
				}
			}
			sendMessage(sender, success(), true);
		} catch (Exception e) {
			sendMessage(sender, fail(), true);
			Manager.printDebug(this.getName(), e, sender);
		}
	}

	private String success() {
		return Manager.getConfig().getString("Commands.Reload.Success");
	}

	private String fail() {
		return Manager.getConfig().getString("Commands.Reload.Fail");
	}
}
