package com.hetag.areareloader.configuration;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

import com.hetag.areareloader.commands.Executor;

public class Manager {
	public static Config defaultConfig;
	public static Config areas;
	
	public void initConfigs() {
		defaultConfig = new Config(new File("config.yml"));
		areas = new Config(new File("areas.yml"));
	}
	
	public Manager() {
		initConfigs();
		loadConfig(defaultConfig);
		loadConfig(areas);
	}

	private void loadConfig(Config configurationFile) {
		FileConfiguration config = null;
		if (configurationFile == defaultConfig) {
			config = defaultConfig.getConfig();

		config.addDefault("Settings.Language.ChatPrefix", "&8[&bAreaReloader&8]&3 ");
		config.addDefault("Settings.Language.NoPermission", "You don't own sufficent permissions to run this command!");
		config.addDefault("Settings.Language.MustBePlayer", "You must be a player to use this command!");
		
		config.addDefault("Settings.Debug.Enabled", false);
		config.addDefault("Settings.Debug.Prefix", "&8[&bAR&7-&bDebug&8]&b ");
		
		config.addDefault("Settings.AreaLoading.Interval", 1500);
		config.addDefault("Settings.AreaLoading.IgnoreAirBlocks", false);
		config.addDefault("Settings.AreaLoading.FastMode", true);
		config.addDefault("Settings.AreaLoading.TPSChecker.Enabled", false);
		config.addDefault("Settings.AreaLoading.TPSChecker.RequiredTPS", 18);
		config.addDefault("Settings.AreaLoading.Percentage", 15);
		
		config.addDefault("Settings.AutoReload.Checker", true);
		config.addDefault("Settings.AutoReload.Interval", 200);
		config.addDefault("Settings.AutoReload.Notify.Admins", true);
		config.addDefault("Settings.AutoReload.Notify.Console", true);
		config.addDefault("Settings.AutoReload.TPSChecker.Enabled", false);
		config.addDefault("Settings.AutoReload.TPSChecker.RequiredTPS", 18);

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
		config.addDefault("Commands.Create.EntitiesValue", "You must select a value: &btrue&7/&bfalse");
		
		config.addDefault("Commands.Display.Description", "&7Displays corners around existing areas.");
		config.addDefault("Commands.Display.OnDisplay", "Displaying area: &b%area%&3.");
		config.addDefault("Commands.Display.OnDisplayRemove", "Removed display for area: &b%area%&3.");
		config.addDefault("Commands.Display.UseParticles", false);
		config.addDefault("Commands.Display.Block.Material", "PRISMARINE");
		config.addDefault("Commands.Display.Block.RestrictVision", true);
		config.addDefault("Commands.Display.ParticleDelay", 3000);
		
		config.addDefault("Commands.List.Description", "&7Lists all existing areas.");
		config.addDefault("Commands.List.NoAreasFound", "No areas were found.");

		config.addDefault("Commands.Delete.Description", "&7Deletes an existing area.");
		config.addDefault("Commands.Delete.OnDelete", "&b%area%&3 was succesfully deleted.");
		config.addDefault("Commands.Delete.InvalidArea", "&b%area%&3 does not exist!");

		config.addDefault("Commands.Load.Description", "&7Loads an existing area.");
		config.addDefault("Commands.Load.OnPrepare", "Preparing area loading for area: &b%area%&3.");
		config.addDefault("Commands.Load.OnLoadSuccess", "Area '&b%area%&3' was succesfully loaded! Changed &b%count%&3 blocks in &b%time%&3ms.");
		config.addDefault("Commands.Load.OnInvalidArea", "&b%area% &3does not exist.");
		config.addDefault("Commands.Load.OnAlreadyLoading", "Failed to reload area: '&b%area%&3' as it is already being loaded.");
		
		config.addDefault("Commands.TpsMonitor.Description", "Starts a task which displays AreaReloader's method of counting TPS (ticks per seconds).");
		config.addDefault("Commands.TpsMonitor.Enabled", false);

		config.addDefault("Commands.Version.Description", "&7Shows information about the plugin.");
		
		config.addDefault("Commands.Info.Description", "&7Shows information about the specified existing area.");
		
		config.addDefault("Commands.Reload.Description", "&7Reloads AreaReloader's configuration file.");
		config.addDefault("Commands.Reload.OnReload", "&aConfigurations and tasks were succesfully reloaded!");
		config.addDefault("Commands.Reload.OnFail", "&cAn error occurred while reloading configurations, printing stack trace.");

		config.addDefault("Commands.Hook.Description", "&7Shows an interface for the plugin's dependencies.");
		defaultConfig.saveConfig();
		}
	}

	  public static FileConfiguration getConfig() {
	    return defaultConfig.getConfig();
	  }
	  
	  public static void reloadAllInstances() {
		  defaultConfig.reloadConfig();
		  areas.reloadConfig();
	  }
}
