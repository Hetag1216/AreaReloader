package com.hedario.areareloader;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hedario.areareloader.commands.Executor;
import com.hedario.areareloader.configuration.Manager;
import com.hedario.areareloader.reflection.Metrics;
import com.hedario.areareloader.reflection.UpdateChecker;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AreaReloader extends JavaPlugin implements Listener {

	public static WorldEditPlugin wep;
	public static AreaReloader plugin;
	public static Logger log;
	public static boolean debug, checker;
	private static Queue queue;
	private boolean updater, useMetrics, announcer;

	public void onEnable() {
		PluginManager pm = Bukkit.getPluginManager();
		if ((WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit") == null) {
			getLogger().warning("Worldedit hook was not found, the plugin cannot be enabled without this dependency.");
			pm.disablePlugin(this);
		} else {
			getLogger().info("Plugin's dependency has been found!");
			wep = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		}
		
		plugin = this;
		log = getLogger();

		log.info("-=-=-=-= AreaReloader " + plugin.getDescription().getVersion() + " =-=-=-=-");
		
		
		try {
			new Manager();
			log.info("Configurations succesfully registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AreaMethods.performSetup();
		// Instantiate queue
		queue = new Queue(this);
		
		// Instantiate settings
		debug = Manager.getConfig().getBoolean("Settings.Debug.Enabled");
		updater = Manager.getConfig().getBoolean("Settings.Updater.Enabled");
		announcer = Manager.getConfig().getBoolean("Settings.Announcer.Enabled");
		
		// AreaLoader setup
		AreaLoader.init();
		
		// AreaScheduler setup
		AreaScheduler.init();
		
		// Instantiate events
		getServer().getPluginManager().registerEvents(new AreaListener(this), this);
		new AreaListener(this);

		try {
			new Executor(this);
			log.info("Commands succesfully registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (updater) {
			checkForUpdates();
		}
		useMetrics = Manager.getConfig().getBoolean("Settings.Metrics.Enabled");
		if (useMetrics) {
	        int pluginId = 14758;
	        new Metrics(this, pluginId);
	        log.info("Metrics has been enabled, thank you!");
		} else {
			log.info("Metrics will be disabled.");
		}
		log.info("Succesfully enabled AreaReloader!");
		log.info("-=-=-=-= -=- =-=-=-=-");
		
		if (announcer) {
			Runnable announcer = new Runnable() {
				@Override
				public void run() {
					for (Player players : getServer().getOnlinePlayers()) {
						if (players.hasPermission("areareloader.*") || players.isOp()) {
							players.sendMessage(AreaMethods.getPrefix() + "AreaReloader is brought to you freely, if you wish to support the project, please consider making a donation!");
						}
					}
				}
			};
			getInstance().getServer().getScheduler().runTaskTimerAsynchronously(plugin, announcer, 200L, 36000L);
		}
	}

	public void onDisable() {
		ShutDown();
		log.info("Succesfully disabled AreaReloader!");
	}
	
	/**
	 * Gets the plugin's instance.
	 * @return plugin
	 */
	public static AreaReloader getInstance() {
		return plugin;
	}

	/**
	 * Returns class utility instance.
	 * @return queue.class
	 */
	public static Queue getQueue() {
		return queue;
	}
	
	public static WorldEditPlugin getWEInstance() {
		return wep;
	}
	
	/**
	 * Gets the status of AreaReloader's hooks.
	 * @return status
	 */
	public String getStatus() {
		String enabled = ChatColor.GREEN + "Enabled";
		String disabled = ChatColor.RED + "Disabled";
		String status = ChatColor.DARK_AQUA + "Status " + ChatColor.GRAY + "» ";
		if (wep != null && wep.isEnabled()) {
			return status + enabled;
		} else {
			return status + disabled;
		}
	}
	/**
	 * Checks for plugin's update from the official spigot page.
	 */
	private void checkForUpdates() {
		new UpdateChecker(this, 70655).getVersion(version -> {
			log.info("-=-=-=-= AreaReloader Updater =-=-=-=-");
			if (this.getDescription().getVersion().equals(version)) {
				log.info("You're running the latest version of the plugin!");
			} else {
				log.info("AreaReloader " + version + " is now available!");
				log.info("You're running AreaReloader " + this.getDescription().getVersion());
				log.info("DOWNLOAD IT AT: https://www.spigotmc.org/resources/areareloader.70655/");
			}
			log.info("-=-=-=-= -=- =-=-=-=-");
		});
	}
	
	/**
	 * Shut down all active tasks.
	 */
	private void ShutDown() {
		if (!getInstance().getServer().getScheduler().getPendingTasks().isEmpty()) {
			getInstance().getServer().getScheduler().getPendingTasks().clear();
		}
		if (!getInstance().getServer().getScheduler().getActiveWorkers().isEmpty()) {
			getInstance().getServer().getScheduler().cancelTasks(getInstance());
			getInstance().getServer().getScheduler().getActiveWorkers().clear();
		}
		if (!getQueue().get().isEmpty()) {
			getQueue().get().clear();
		}
		if (!AreaLoader.areas.isEmpty()) {
			AreaLoader.areas.clear();
		}
	}
}