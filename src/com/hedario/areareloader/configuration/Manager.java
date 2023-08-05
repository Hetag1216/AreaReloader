package com.hedario.areareloader.configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.hedario.areareloader.AreaMethods;
import com.hedario.areareloader.AreaReloader;
import com.hedario.areareloader.commands.Executor;

public class Manager {
	public static Config defaultConfig;
	public static Config areas;
	private static double version = 1.1;

	public Manager() {
		initConfigs();
		loadConfig(defaultConfig);
		loadConfig(areas);
		setDebug();
		checkVersion();
	}
	
	private static void checkVersion() {
		if (getConfig().getDouble("Config.Version") < version || getAreasConfig().getDouble("Config.Version") < version) {
			AreaReloader.getInstance().getLogger().warning("You're currently using an older configuration version, it's highly recommended to reset/update your configuration files!");
		}
	}
	
	public void initConfigs() {
		defaultConfig = new Config(new File("config.yml"));
		areas = new Config(new File("areas.yml"));
	}

	private void loadConfig(Config configurationFile) {
		FileConfiguration config = null;
		if (configurationFile == defaultConfig) {
			config = defaultConfig.getConfig();

		config.addDefault("Settings.Language.ChatPrefix", "&8[&3AreaReloader&8]&3 ");
		config.addDefault("Settings.Language.NoPermission", "You dont own sufficent permissions to run this command!");
		config.addDefault("Settings.Language.MustBePlayer", "You must be a player to use this command!");
		
		config.addDefault("Settings.Debug.Enabled", false);
		config.addDefault("Settings.Updater.Enabled", true);
		config.addDefault("Settings.Metrics.Enabled", true);
		config.addDefault("Settings.Announcer.Enabled", true);
		
		config.addDefault("Settings.AreaLoading.GlobalInterval", 500);
		config.addDefault("Settings.AreaLoading.FastMode", true);
		config.addDefault("Settings.AreaLoading.Percentage", 15);
		
		config.addDefault("Settings.AutoReload.Checker", true);
		config.addDefault("Settings.AutoReload.Notify.Admins", true);
		config.addDefault("Settings.AutoReload.Notify.Console", true);

		ArrayList<String> helpLines = new ArrayList<>();
		Executor.help = helpLines;
		helpLines.add("&8/&3ar &bhelp &7Display commands help.");
		config.addDefault("Commands.HelpLines", helpLines);
		config.addDefault("Commands.Help.Description", "&7Shows information about a command.");
		config.addDefault("Commands.Help.InvalidTopic", "&7Please specify a valid topic.");
		config.addDefault("Commands.Help.ProperUsage", "&3Proper usage: &b&o");

		config.addDefault("Commands.Create.Description", "&7Creates a new area.");
		config.addDefault("Commands.Create.Asynchronously", false);
		config.addDefault("Commands.Create.Preparing", "Preparing to create &b%area%&3...");
		config.addDefault("Commands.Create.Success", "&b%area%&3 has been succesfully created. Use the command &b/ar location &3to set a safe location for players inside the area whenever it loads!");
		config.addDefault("Commands.Create.Failure", "Failed to create new area &b%area%&3.");
		config.addDefault("Commands.Create.AlreadyExists", "An area with the name of &b%area%&3 already exists, please choose a different name!");
		config.addDefault("Commands.Create.InvalidValue", "You must select a value: &btrue&7/&bfalse");
		config.addDefault("Commands.Create.InvalidName", "&b%area% &3is not a valid name!");
		
		config.addDefault("Commands.Display.Description", "&7Displays corners around existing areas.");
		config.addDefault("Commands.Display.Display", "Displaying &b%area%&3.");
		config.addDefault("Commands.Display.RemoveDisplay", "Removed display for &b%area%&3.");
		config.addDefault("Commands.Display.UseParticles", false);
		config.addDefault("Commands.Display.ParticleEffect", "FLAME");
		config.addDefault("Commands.Display.Block.Material", "PRISMARINE");
		config.addDefault("Commands.Display.Block.RestrictVision", true);
		config.addDefault("Commands.Display.ParticleDelay", 3000);
		config.addDefault("Commands.Display.ForceRendering", false);
		
		config.addDefault("Commands.List.Description", "&7Lists all existing areas.");
		config.addDefault("Commands.List.NoAreasFound", "No areas were found.");

		config.addDefault("Commands.Delete.Description", "&7Deletes an existing area.");
		config.addDefault("Commands.Delete.Success", "&b%area%&3 was succesfully deleted.");
		config.addDefault("Commands.Delete.InvalidArea", "&b%area%&3 does not exist!");

		config.addDefault("Commands.Load.Description", "&7Loads an existing area.");
		config.addDefault("Commands.Load.Preparing", "Preparing to load &b%area%&3.");
		config.addDefault("Commands.Load.Process", "Loading of &b%area%&3 at &b%perc%&3%.");
		config.addDefault("Commands.Load.Success", "Area &b%area%&3 was succesfully loaded in &b%time%&b.");
		config.addDefault("Commands.Load.Fail", "&b%area%&3 has failed to load! Error has been traced and sent to console, enable debug for a more accurate description.");
		config.addDefault("Commands.Load.InvalidArea", "&b%area% &3does not exist.");
		config.addDefault("Commands.Load.AlreadyLoading", "&b%area%&3 is already queued and being loaded!");
		config.addDefault("Commands.Load.StillCreating", "&b%area%&3 is still being created.");

		config.addDefault("Commands.Version.Description", "&7Shows information about the plugin.");
		
		config.addDefault("Commands.Info.Description", "&7Shows information about the specified existing area.");
		
		config.addDefault("Commands.Reload.Description", "&7Reloads AreaReloader's configuration file.");
		config.addDefault("Commands.Reload.Success", "&bConfigurations and tasks were succesfully reloaded!");
		config.addDefault("Commands.Reload.Fail", "An &cerror &3occurred while reloading configurations, enable debug for a more accurate description.");

		config.addDefault("Commands.Hook.Description", "&7Shows an interface for the plugins dependencies.");
		
		config.addDefault("Commands.Cancel.Description", "&7Cancels the loading process of one or all areas.");
		config.addDefault("Commands.Cancel.Success", "&b%area% &7(&bTask: %id%&7) &3loading has been cancelled!");
		config.addDefault("Commands.Cancel.Fail", "&b%area% &3is not currently being loaded.");
		config.addDefault("Commands.Cancel.CancelAll", "All areas have been cancelled from loading!");
		config.addDefault("Commands.Cancel.NoAreasLoading", "Nothing happened, no areas were being loaded.");
		
		config.addDefault("Commands.Location.Description", "&7Customise and set a safe location for players to be teleported to when they're inside an area being loaded.");
		config.addDefault("Commands.Location.Set", "Safe location has been set correctly for &b%area%&3!");
		config.addDefault("Commands.Location.Teleport", "You've been teleported to the safe location for &b%area%&3!");
		config.addDefault("Commands.Location.InvalidArea", "&b%area% &3does not exist.");
		
		config.addDefault("Config.Version", version);
		defaultConfig.saveConfig();
		} else if (configurationFile == areas) {
			config = areas.getConfig();
			
			config.addDefault("Config.Version", version);
		}
	}

	public static FileConfiguration getConfig() {
		return defaultConfig.getConfig();
	}
	
	public static FileConfiguration getAreasConfig() {
		return areas.getConfig();
	}

	public static void reloadConfigurations() {
		defaultConfig.reloadConfig();
		areas.reloadConfig();
		checkVersion();
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
			printDebug("Writing debugs trace...");
			printDebug("");
			printDebug("" + e.getMessage());
			printDebug("-=-=-=-=-=-=-=-=-=-=- -=- -=-=-=-=-=-=-=-=-=-=-");
			if (sender != null)
				AreaMethods.sendMessage(sender, "An error has been generated and registered to the debugs file.", true);
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
