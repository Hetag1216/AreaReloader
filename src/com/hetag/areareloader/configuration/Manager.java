package com.hetag.areareloader.configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.hetag.areareloader.AreaMethods;
import com.hetag.areareloader.AreaReloader;
import com.hetag.areareloader.commands.Executor;

public class Manager {
	public static Config defaultConfig;
	public static Config areas;

	public Manager() {
		initConfigs();
		loadConfig(defaultConfig);
		loadConfig(areas);
		setDebug();
	}
	
	public void initConfigs() {
		defaultConfig = new Config(new File("config.yml"));
		areas = new Config(new File("areas.yml"));
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
		
		config.addDefault("Settings.Updater.Enabled", true);
		
		config.addDefault("Settings.Metrics.Enabled", true);
		
		config.addDefault("Settings.AreaLoading.Interval", 500);
		config.addDefault("Settings.AreaLoading.IgnoreAirBlocks", false);
		config.addDefault("Settings.AreaLoading.FastMode", true);
		config.addDefault("Settings.AreaLoading.Percentage", 15);
		
		config.addDefault("Settings.AutoReload.Checker", true);
		config.addDefault("Settings.AutoReload.Interval", 200);
		config.addDefault("Settings.AutoReload.Notify.Admins", true);
		config.addDefault("Settings.AutoReload.Notify.Console", true);

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
		config.addDefault("Commands.Display.ParticleEffect", "FLAME");
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
		config.addDefault("Commands.Load.OnLoadSuccess", "Area '&b%area%&3' was succesfully loaded! Changed &b%count%&3 blocks in &b%time%&3.");
		config.addDefault("Commands.Load.OnInvalidArea", "&b%area% &3does not exist.");
		config.addDefault("Commands.Load.OnAlreadyLoading", "Failed to reload area: '&b%area%&3' as it is already being loaded.");

		config.addDefault("Commands.Version.Description", "&7Shows information about the plugin.");
		
		config.addDefault("Commands.Info.Description", "&7Shows information about the specified existing area.");
		
		config.addDefault("Commands.Reload.Description", "&7Reloads AreaReloader's configuration file.");
		config.addDefault("Commands.Reload.OnReload", "&aConfigurations and tasks were succesfully reloaded!");
		config.addDefault("Commands.Reload.OnFail", "&cAn error occurred while reloading configurations, printing stack trace.");

		config.addDefault("Commands.Hook.Description", "&7Shows an interface for the plugin's dependencies.");
		
		config.addDefault("Commands.Cancel.Description", "&7Cancels the loading of one or all areas.");
		config.addDefault("Commands.Cancel.OnCancelArea", "&b%area% &7(&bTask: %id%&7) &3loading has been cancelled!");
		config.addDefault("Commands.Cancel.OnCancelFail", "&b%area% &3is not currently being loaded.");
		config.addDefault("Commands.Cancel.OnCancelAll", "All areas have been cancelled from loading!");
		
		config.addDefault("Config.Version", 1.0);
		defaultConfig.saveConfig();
		} else if (configurationFile == areas) {
			config = areas.getConfig();
			
			config.addDefault("Config.Version", 1.0);
		}
	}

	public static FileConfiguration getConfig() {
		return defaultConfig.getConfig();
	}

	public static void reloadAllInstances() {
		defaultConfig.reloadConfig();
		areas.reloadConfig();
	}

	public static void setDebug() {
		resetDebug();
		try {
			File folder = AreaReloader.getInstance().getDataFolder();
			if (!folder.exists()) {
				folder.mkdir();
			}

			File debug = new File(AreaReloader.getInstance().getDataFolder(), "debug.txt");
			if (!debug.exists()) {
				debug.createNewFile();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints the given string to debug.
	 * @param string
	 */
	public static void printDebug(String string) {
		if (AreaReloader.debug) {
			try {
				final FileWriter fw = new FileWriter(new File(AreaReloader.getInstance().getDataFolder(), "debug.txt"), true);
				final PrintWriter pw = new PrintWriter(fw);
				pw.println(LocalTime.now() + " " + string);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Prints the given params to debug
	 * <p>
	 * Mainly used for Commands debugging
	 * @param cmd
	 * @param e
	 * @param sender
	 */
	public static void printDebug(String cmd, Exception e, CommandSender sender) {
		if (AreaReloader.debug) {
			printDebug("-=-=-=-=-=-=-=-=-=-=- Area Commands -=-=-=-=-=-=-=-=-=-=-");
			printDebug("An issue when running the command " + cmd + " has occurred!");
			printDebug("Writing debug's trace...");
			printDebug("");
			printDebug("" + e.getMessage());
			printDebug("-=-=-=-=-=-=-=-=-=-=- -=- -=-=-=-=-=-=-=-=-=-=-");
			if (sender != null)
				AreaMethods.sendDebugMessage(sender, "An error has been generated and registered to the debug's file.");
			else
				AreaReloader.log.warning("An error has been generated and registered to the debug's file.");
		}
	}
	
	public static void resetDebug() {
		File file = new File(AreaReloader.getInstance().getDataFolder(), "debug.txt");
		if (file.exists()) {
			file.delete();
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	
}
