package com.hetag.areareloader.configuration;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

import com.hetag.areareloader.commands.Executor;

public class Manager {
	public static Config defaultConfig;

	public Manager() {
		defaultConfig = new Config(new File("config.yml"));
		loadConfig(ConfigType.DEFAULT);
	}

	private void loadConfig(ConfigType type) {
		if (type == ConfigType.DEFAULT) {
		FileConfiguration config = defaultConfig.getConfig();

		config.addDefault("Settings.Language.ChatPrefix", "&8[&bAreaReloader&8]&3 ");
		config.addDefault("Settings.Language.NoPermission", "You don't own sufficent permissions to run this command!");
		config.addDefault("Settings.Language.MustBePlayer", "You must be a player to use this command!");
		config.addDefault("Settings.Debug.Enabled", false);
		config.addDefault("Settings.Debug.Prefix", "&8[&bAR&7-&bDebug&8]&b ");
		config.addDefault("Settings.AreaLoading.Interval", "200");

		ArrayList<String> helpLines = new ArrayList<>();
		Executor.help = helpLines;
		config.addDefault("Commands.HelpLines", helpLines);
		helpLines.add("&8/&3ar &ahelp &7Display commands help.");
		config.addDefault("Commands.Help.Required", "&7Required");
		config.addDefault("Commands.Help.Optional", "&7Optional");
		config.addDefault("Commands.Help.InvalidTopic", "&7Please specify a valid topic.");
		config.addDefault("Commands.Help.ProperUsage", "&3Proper usage: &b&o");
		config.addDefault("Commands.Help.Description", "Shows information about a command.");

		config.addDefault("Commands.Create.Description", "&7Creates a new area.");
		config.addDefault("Commands.Create.OnSuccess", "Succesfully created new area named: &b%area%&3.");
		config.addDefault("Commands.Create.OnFailure", "Failed to create new area named: &b%area%&3.");
		config.addDefault("Commands.Create.AlreadyExists", "An area with the name &b%area%&3 already exists, please choose a different name!");

		config.addDefault("Commands.List.Description", "&7Lists all existing areas.");
		config.addDefault("Commands.List.NoAreasFound", "No areas were found.");

		config.addDefault("Commands.Delete.Description", "&7Deletes an existing area.");
		config.addDefault("Commands.Delete.OnDelete", "&b%area%&3 was succesfully deleted.");
		config.addDefault("Commands.Delete.InvalidArea", "&b%area&3 does not exist!");

		config.addDefault("Commands.Load.Description", "&7Loads an existing area.");
		config.addDefault("Commands.Load.onPrepare", "Preparing area loading for area: &b%area%&3.");
		config.addDefault("Commands.Load.onLoadSuccess", "Area '&b%area%&3' was succesfully loaded!");
		config.addDefault("Commands.Load.onInvalidArea", "&b%area% &3does not exist.");

		config.addDefault("Commands.Version.Description", "&7Shows information about the plugin.");
		
		config.addDefault("Commands.Info.Description", "&7Shows information about the specified existing area.");
		
		config.addDefault("Commands.Reload.Description", "&7Reloads AreaReloader's configuration file.");

		config.addDefault("Commands.Hook.Description", "&7Shows an interface for the plugin's dependencies.");
		defaultConfig.saveConfig();
		}
	}

	  public static FileConfiguration getConfig() {
	    return defaultConfig.getConfig();
	  }
}
