package com.hedario.areareloader.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.hedario.areareloader.AreaLoader;
import com.hedario.areareloader.AreaMethods;
import com.hedario.areareloader.AreaReloader;
import com.hedario.areareloader.AreaScheduler;
import com.hedario.areareloader.configuration.Manager;

public class InfoCommand extends ARCommand {
	public InfoCommand() {
		super("info", "/ar info <area>", Manager.getConfig().getString("Commands.Info.Description"), new String[] { "info" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 1, 1)) {
			return;
		}
		String area = args.get(0);
		String display = null;
		if (DisplayCommand.getDisplayedAreas().contains(area)) {
			display = "true";
		} else {
			display = "false";
		}
		if (!Manager.getAreasConfig().contains("Areas." + area)) {
			sendMessage(sender, LoadCommand.invalidArea().replaceAll("%area%", area), true);
		}
		sendMessage(sender, "&7-=-=-=-=-=-=-=-=-=-=- « &3" + area + " &7» -=-=-=-=-=-=-=-=-=-=-", false);
		sendMessage(sender, "&3World &7» &b" + AreaMethods.getAreaInWorld(area), false);
		sendMessage(sender, "&3First corner &7» &b" + AreaMethods.getAreaX(area) + "&7, &b" + AreaMethods.getAreaY(area) + "&7, &b" + AreaMethods.getAreaZ(area), false);
		sendMessage(sender, "&3Second corner &7» &b" + AreaMethods.getAreaMaxX(area) + "&7, &b" + AreaMethods.getAreaMaxY(area) + "&7, &b" + AreaMethods.getAreaMaxZ(area), false);
		sendMessage(sender, "&3Chunk size &7» &b" + AreaMethods.getAreaChunk(area), false);
		sendMessage(sender, "&3Loading Interval &7» &3" + (AreaMethods.isGlobalInterval(area) ? AreaMethods.formatTime(AreaMethods.getInterval(area)) + " &7(&bGLOBAL&7)" : AreaMethods.formatTime(AreaMethods.getInterval(area))), false);
		
		if (Manager.getAreasConfig().getBoolean("Areas." + area + ".SafeLocation.Enabled")) {
			World world = Bukkit.getWorld(Manager.getAreasConfig().getString("Areas." + area + ".SafeLocation.World"));
			double x = Manager.getAreasConfig().getDouble("Areas." + area + ".SafeLocation.X");
			double y = Manager.getAreasConfig().getDouble("Areas." + area + ".SafeLocation.Y");
			double z = Manager.getAreasConfig().getDouble("Areas." + area + ".SafeLocation.Z");
			sendMessage(sender, "&3Safe location &7» &b" + world.getName() + "&7, &b" + x + "&7, &b" + y + "&7, &b" + z, false);
			sendMessage(sender, "&3Safe location speed &7» &b" + Manager.getAreasConfig().getInt("Areas." + area + ".SafeLocation.Settings.Speed"), false);
			sendMessage(sender, "&3Safe location interval &7» &b" + Manager.getAreasConfig().getInt("Areas." + area + ".SafeLocation.Settings.Interval"), false);
		}
		sendMessage(sender, "&3Is being displayed &7» &b" + display, false);
		sendMessage(sender, "&3Has copied entities &7» &b" + Manager.getAreasConfig().getBoolean("Areas." + area + ".HasCopiedEntities"), false);
		sendMessage(sender, "&3Has copied biomes &7» &b" + Manager.getAreasConfig().getBoolean("Areas." + area + ".HasCopiedBiomes"), false);
		sendMessage(sender, "&3Is using fast mode &7» &b" + AreaMethods.fastMode, false);

		if (AreaReloader.getQueue().isQueued(area)) {
			if (AreaLoader.isInstance(area)) {
				sendMessage(sender,"&3Currently loaded percentage &7» &b" + String.format("%.2f", AreaLoader.get(area).getPerc()) + "&3%", false);
			}
		}
		sendMessage(sender, "&3Is automatically reloading &7» &b" + Manager.getAreasConfig().getBoolean("Areas." + area + ".AutoReload.Enabled"), false);
		if (Manager.getAreasConfig().getBoolean("Areas." + area + ".AutoReload.Enabled") == true) {
			sendMessage(sender, "&3Auto reloading time &7» &b" 	+ AreaMethods.formatTime(Manager.getAreasConfig().getLong("Areas." + area + ".AutoReload.Time")), false);
			sendMessage(sender, "&3Next auto reload in &7» &b" + AreaMethods.formatTime(AreaScheduler.getRemainingTime(area)), false);
		}
		return;
	}

	@Override
	protected List<String> getTabCompletion(final CommandSender sender, final List<String> args) {
		List<String> list = new ArrayList<String>();
		if (!sender.hasPermission("areareloader.command.info") || args.size() >= 1) {
			return new ArrayList<String>();
		}
		for (final String map : AreaMethods.getAreas()) {
			list.add(map);
		}
		return list;
	}
}
