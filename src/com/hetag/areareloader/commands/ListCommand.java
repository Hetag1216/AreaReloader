package com.hetag.areareloader.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class ListCommand extends ARCommand {
	static String path = "Commands.List.Description";

	public ListCommand() {
		super("list", "/ar list", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] { "list" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, 0, 0, 1)) {
			return;
		}
		if (args.size() == 0) {
			List<String> strings = new ArrayList<>();
			ConfigurationSection cs = AreaReloader.areas.getConfig().getConfigurationSection("Areas");
			if (cs != null && cs.getKeys(false).size() > 0) {
				for (String area : cs.getKeys(false)) {
					strings.add(area);
				}
			} else {
				sendMessage(sender, notFound(), true);
				return;
			}
			Collections.sort(strings);
			Collections.reverse(strings);
			for (String formatted : getPage(strings, ChatColor.BOLD + "- " + ChatColor.AQUA + "Existing Areas" + ChatColor.BOLD + " -" , 1, true)) {
				sendMessage(sender, "&b" + formatted, false);
			}
			return;
		} else if (args.size() == 1) {
			String arg = args.get(0).toLowerCase();
			if (isNumeric(arg)) {
				List<String> strings = new ArrayList<>();
				ConfigurationSection cs = AreaReloader.areas.getConfig().getConfigurationSection("Areas");
				if (cs != null && cs.getKeys(false).size() > 0) {
					for (String area : cs.getKeys(false)) {
						strings.add(area);
					}
				} else {
					sendMessage(sender, notFound(), true);
					return;
				}
				for (String formatted : getPage(strings, ChatColor.BOLD + "- " + ChatColor.AQUA + "Existing Areas" + ChatColor.BOLD + " -" , Integer.valueOf(arg), true)) {
					sendMessage(sender, "&b" + formatted, false);
				}
			}
		}
		if (args.size() > 1) {
			sendMessage(sender, this.getProperUsage(), false);
		}
	}

	private String notFound() {
		return Manager.getConfig().getString("Commands.List.NoAreasFound");
	}
}
