package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.TPS;
import com.hetag.areareloader.configuration.Manager;

public class TPSMonitorCommand extends ARCommand {
	private long startTime, time;

	public TPSMonitorCommand() {
		super("tps", "/ar tps <time>", formatColors(Manager.getConfig().getString("Commands.TpsMonitor.Description")), new String[] { "tps" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		if (args.size() <= 0) {
			sender.sendMessage(this.getProperUsage());
			return;
		}
		startTime = System.currentTimeMillis();
		time = Long.valueOf(args.get(0));
		if (!isNumeric(args.get(0))) {
			sendMessage(sender, "&3" + time + "&b must be numeric.", true);
			return;
		}
		sendMessage(sender, "Initializing AreaReloader's TPS monitoring for &b" + time + "&3ms" , true);

	        new BukkitRunnable() {
	        	public void run() {
	        		sendMessage(sender, String.valueOf(TPS.getTPS()), true);
	        		if (System.currentTimeMillis() >= startTime + time) {
	        			sendMessage(sender, "Finished running TPS monitoring task.", true);
	        			cancel();
	        			return;
	        		}
	        	}
	        }.runTaskTimer(AreaReloader.getInstance(), 0L, 20L);
	}
}
