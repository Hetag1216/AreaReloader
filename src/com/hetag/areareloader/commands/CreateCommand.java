package com.hetag.areareloader.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.configuration.Manager;
import com.sk89q.worldedit.WorldEditException;

public class CreateCommand extends ARCommand {
	static String path = "Commands.Create.Description";

	public CreateCommand() {
		super("create", "/ar create <name> <copyEntities: true|false>", formatColors(Manager.getConfig().getString(path)), new String[] { "create" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !isPlayer(sender) || !this.correctLength(sender, args.size(), 2, 2)) {
			return;
		}
		if (args.size() == 2) {
			String area = args.get(0);
			boolean skipEntities = false;
			if (Manager.areas.getConfig().contains("Areas." + args.get(0))) {
				sendMessage(sender, onCreateExists().replaceAll("%area%", area), true);
				return;
			}
			String skipEnts = args.get(1);
			if (skipEnts.equalsIgnoreCase("true")) {
				skipEntities = true;
			} else if (skipEnts.equalsIgnoreCase("false")) {
				skipEntities = false;
			} else {
				sendMessage(sender, onEntity(), true);
				return;
			}
			try {
				if (AreaMethods.createNewArea((Player) sender, args.get(0), 16, skipEntities)) {
					sendMessage(sender, onCreateSuccess().replaceAll("%area%", area), true);
				} else {
					sendMessage(sender, onCreateFail().replaceAll("%area%", area), true);
				}
			} catch (WorldEditException e) {
				e.printStackTrace();
			}
		}
		if (args.size() > 2 || args.size() < 2) {
			sender.sendMessage(getProperUsage());
		}
	}

	private String onCreateExists() {
		return formatColors(Manager.getConfig().getString("Commands.Create.AlreadyExists"));
	}

	private String onCreateSuccess() {
		return formatColors(Manager.getConfig().getString("Commands.Create.OnSuccess"));
	}

	private String onCreateFail() {
		return formatColors(Manager.getConfig().getString("Commands.Create.OnFailure"));
	}
	
	private String onEntity() {
		return Manager.getConfig().getString("Commands.Create.EntitiesValue");
	}

}
