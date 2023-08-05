package com.hedario.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areareloader.AreaReloader;
import com.hedario.areareloader.configuration.Manager;

public class HookCommand extends ARCommand {
	public HookCommand() {
		super("hook", "/ar hook", Manager.getConfig().getString("Commands.Hook.Description"), new String[] { "hook", "hooks" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		sendMessage(sender, "&3-=-=-=-= &7« &3 Hooks &7» &3=-=-=-=-", false);
		sendMessage(sender, "&7- &bWorldEdit &7(&bWE&7)", false);
		sendMessage(sender, AreaReloader.getInstance().getStatus(), false);
		if (AreaReloader.getWEInstance() != null) {
			sendMessage(sender, "&3Version &7» &b" + AreaReloader.getWEInstance().getDescription().getVersion(), false);
		}
		sendMessage(sender, "&3-=-=-=-= -=- =-=-=-=-", false);
	}
}
