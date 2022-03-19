package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.configuration.Manager;

import net.md_5.bungee.api.ChatColor;

public class VersionCommand extends ARCommand {
	static String path = "Commands.Version.Description";

	public VersionCommand() {
		super("version", "/ar version", ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString(path)), new String[] { "version", "v" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		sendMessage(sender, "", true);
		sendMessage(sender, "&3Version: &b" + AreaReloader.getInstance().getDescription().getVersion(), false);
		sendMessage(sender, "&3Author: &b" + AreaReloader.getInstance().getDescription().getAuthors().toString().replace("[", "").toString().replace("]", ""), false);
		sendMessage(sender, "&3Compatible Minecraft Version(s): &b 1.13 - 1.14 - 1.15 - 1.16 - 1.17 - 1.18", false);
		sendMessage(sender, "&3Using Protocol: &b" + AreaReloader.getProtocol().getClass().toString(), false);
		sendMessage(sender, "", false);
		sendMessage(sender, "&8« &bUseful links &8»", false);
		sendMessage(sender, "&aSpigot page &8» &bhttps://www.spigotmc.org/resources/areareloader-reload-your-areas-1-13-x-1-14-x-1-15-x-1-16-x.70655/", false);
		sendMessage(sender, "&aGithub &8» &bhttps://github.com/Hetag1216/AreaReloader", false);
		sendMessage(sender, "&aCheck out my other resources at &8» &bhttps://www.spigotmc.org/members/_hetag1216_.243334/", false);
		sendMessage(sender, "&aDiscord &8» &bhttps://discord.gg/yqs9UJs", false);
	}
}
