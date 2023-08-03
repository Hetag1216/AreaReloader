package com.hedario.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areareloader.AreaReloader;
import com.hedario.areareloader.configuration.Manager;

public class VersionCommand extends ARCommand {
	public VersionCommand() {
		super("version", "/ar version", Manager.getConfig().getString("Commands.Version.Description"), new String[] { "version", "v" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		sendMessage(sender, "&8&m-----&r "+ this.prefix + "&8&m-----", false);
		sendMessage(sender, "&3Version &7» &b" + AreaReloader.plugin.getDescription().getVersion(), false);
		sendMessage(sender, "&3API version &7» &b" + AreaReloader.plugin.getDescription().getAPIVersion(), false);
		sendMessage(sender, "&3Author &7» &b" + AreaReloader.plugin.getDescription().getAuthors().toString().replace("[", "").toString().replace("]", ""), false);
		sendMessage(sender, "&3Compatible Minecraft Version(s) &7» &b1.16.x, 1.17.x, 1.18.x, 1.19.x, 1.20.x", false);
		sendMessage(sender, "&3AreaRelaoders dependency &7» &b" + AreaReloader.plugin.getDescription().getDepend().toString().replace("[", "").toString().replace("]", ""), false);
		sendMessage(sender, "&3AreaReloaders Java requirements &7» &b Java 16+", false);
		sendMessage(sender, "&3System Java version &7» &b" + System.getProperty("java.version"), false);
		sendMessage(sender, "", false);
		sendMessage(sender, "&3Page &7» &bwww.spigotmc.org/resources/areareloader.70655/", false);
		sendMessage(sender, "&3Github &7» &bgithub.com/Hetag1216/AreaReloader", false);
		sendMessage(sender, "&3Discord &7» &bdiscord.gg/yqs9UJs", false);
		sendMessage(sender, "&3My plugins &7» &bwww.spigotmc.org/members/_hetag1216_.243334/", false);
		sendMessage(sender, "&3Donation &7» &bwww.paypal.me/Hetag1216", false);
		sendMessage(sender, "&3Discord &7» &bdiscord.gg/yqs9UJs", false);
	}
}
