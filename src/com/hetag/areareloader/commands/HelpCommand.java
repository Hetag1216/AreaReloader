package com.hetag.areareloader.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.hetag.areareloader.configuration.Manager;

public class HelpCommand extends ARCommand {
	private String required;
	private String optional;
	private String invalidTopic;
	private static String path = "Commands.Help.";
	public static FileConfiguration config = Manager.getConfig();

	public HelpCommand() {
		super("help", "/ar help <Page/Topic>", config.getString(path + "Description"), new String[] { "help", "h" });

		this.required = ChatColor.translateAlternateColorCodes('&', config.getString(path + "Required"));
		this.optional = ChatColor.translateAlternateColorCodes('&', config.getString(path + "Optional"));
		this.invalidTopic = ChatColor.translateAlternateColorCodes('&', config.getString(path + "InvalidTopic"));
	}

	public void execute(CommandSender sender, List<String> args) {
		if ((!hasPermission(sender)) || (!correctLength(sender, args.size(), 0, 1))) {
			return;
		}
		if (args.size() == 0) {
			List<String> strings = new ArrayList<>();
			for (ARCommand command : instances.values()) {
				if ((!command.getName().equalsIgnoreCase("help")) && (sender.hasPermission("areareloader.command." + command.getName()))) {
					strings.add(command.getProperUse());
				}
			}
			Collections.sort(strings);
			Collections.reverse(strings);
			Collections.reverse(strings);
			for (String s : getPage(strings, ChatColor.GRAY + "Commands: <" + this.required + "> [" + this.optional + "]", 1, false)) {
				sender.sendMessage(ChatColor.GREEN + s);
			}
			return;
		}
		String arg = ((String) args.get(0)).toLowerCase();
		if (isNumeric(arg)) {
			List<String> strings = new ArrayList<>();
			for (ARCommand command : instances.values()) {
				strings.add(command.getProperUse());
			}
			for (String s : getPage(strings,
				ChatColor.GRAY + "Commands: <" + this.required + "> [" + this.optional + "]", Integer.valueOf(arg).intValue(), true)) {
				sender.sendMessage(ChatColor.GREEN + s);
			}
		} else if (instances.keySet().contains(arg)) {
			((ARCommand) instances.get(arg)).help(sender, true);
		} else {
			sender.sendMessage(ChatColor.RED + this.invalidTopic);
		}
	}
}
