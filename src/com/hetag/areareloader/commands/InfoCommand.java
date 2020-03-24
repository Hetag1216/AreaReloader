package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.AreaScheduler;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class InfoCommand extends ARCommand {
	static String path = "Commands.Info.Description";
	
	public InfoCommand() {
		super("info", "/ar info <area>",  formatColors(Manager.getConfig().getString(path)), new String[] {"info"});
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
		String display = null;
		if (DisplayCommand.display.contains(area)) {
			display = ChatColor.AQUA + "true";
		} else {
			display = ChatColor.AQUA + "false";
		}
		if (AreaReloader.areas.getConfig().contains("Areas." + area)) {
			sender.sendMessage(prefix);
			sendMessage(sender, "&8« &b" + area + " &8»", false);
			sendMessage(sender, "&3World &8» &b" + AreaMethods.getAreaInWorld(area) , false);
			sendMessage(sender, "&3Location X &8» &b" + AreaMethods.getAreaX(area) + "&7 | &b" + AreaMethods.getAreaMaxX(area) , false);
			sendMessage(sender, "&3Location Z &8» &b" + AreaMethods.getAreaZ(area) + "&7 | &b" + AreaMethods.getAreaMaxZ(area), false);
			sendMessage(sender, "&3Is automatically reloading &8» &b" + AreaReloader.areas.getConfig().getBoolean("Areas." + area + ".AutoReload.Enabled"), false);
			sendMessage(sender, "&3Auto reloading time &8» &b" + AreaReloader.areas.getConfig().getLong("Areas." + area + ".AutoReload.Time"), false);
			sendMessage(sender, "&3Next auto reload in &8» &b" + AreaScheduler.getRemainingTime(area), false);
			sendMessage(sender, "&3Is being displayed &8» &b" + display, false);
			sendMessage(sender, "&3Is ignoring entities &8» &b" + AreaReloader.areas.getConfig().getBoolean("Areas." + area + ".IgnoresEntities"), false);
			sendMessage(sender, "&3Is ignoring air blocks when loading &8» &b" + AreaMethods.ignoreAirBlocks, false);
			return;
		} else {
			sendMessage(sender, LoadCommand.onInvalid().replaceAll("%area%", area), true);
		}
	}
}
