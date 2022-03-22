package com.hetag.areareloader;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hetag.areareloader.commands.Executor;
import com.hetag.areareloader.configuration.Manager;
import com.hetag.areareloader.reflection.AreaProtocol;
import com.hetag.areareloader.reflection.UpdateChecker;
import com.hetag.areareloader.reflection.V1_13.Protocol_1_13;
import com.hetag.areareloader.reflection.V1_14.Protocol_1_14;
import com.hetag.areareloader.reflection.V1_15.Protocol_1_15;
import com.hetag.areareloader.reflection.V1_16.Protocol_1_16;
import com.hetag.areareloader.reflection.V1_17.Protocol_1_17;
import com.hetag.areareloader.reflection.V1_18.Protocol_1_18;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AreaReloader extends JavaPlugin implements Listener {
	public static AreaReloader plugin;
	public static Logger log;
	public static WorldEditPlugin wep;
	public static AreaProtocol ap;
	public static boolean debug, checker;
	//public static ArrayList<String> isDeleted = new ArrayList<>();
	private Queue queue;
	private boolean fetch = false;

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
		checkProtocol();
		new Manager();
		AreaMethods.performSetup();
		// General setup
		queue = new Queue(this);
		debug = Manager.getConfig().getBoolean("Settings.Debug.Enabled");
		// AreaLoader setup
		AreaLoader.init();
		// AreaScheduler setup
		AreaScheduler.init();

		log.info("Configurations succesfully registered!");

		try {
			new Executor(this);
			log.info("Commands succesfully registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getServer().getPluginManager().registerEvents(new AreaListener(this), this);
		
		//time out the updater so that it doesn't slow the main thread if unable to reach the url.
		long time = System.currentTimeMillis();
		if (System.currentTimeMillis() <= time + 5000 && !fetch) {
			new UpdateChecker(this, 70655).getVersion(version -> {
				if (this.getDescription().getVersion().equals(version)) {
					getLogger().info("There is not a new update available.");
					fetch = true;
				} else {
					getLogger().info("There is a new update available.");
					fetch = true;
				}
			});
		} else {
			log.warning("Unable to check for an update.");
		}
		log.info("Succesfully enabled AreaReloader!");
		log.info("-=-=-=-= -=- =-=-=-=-");
	}

	public void onDisable() {
		ShutDown();
		log.info("Succesfully disabled AreaReloader!");
	}

	public void checkProtocol() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		final String formattedVersion = version.substring(version.lastIndexOf(".") + 1);
		if (formattedVersion.contains("1_14")) {
			ap = new Protocol_1_14();
			log.info("Using protocol for 1.14 versions compatibility!");
		} else if (formattedVersion.contains("1_15")) {
			ap = new Protocol_1_15();
			log.info("Using protocol for 1.15 versions compatibility!");
		} else if (formattedVersion.contains("1_16")) {
			ap = new Protocol_1_16();
			log.info("Using protocol for 1.16 versions compatibility!");
		} else if (formattedVersion.contains("1_17")) {
			ap = new Protocol_1_17();
			log.info("Using protocol for 1.17 versions compatibility!");
		} else if (formattedVersion.contains("1_18")) {
			ap = new Protocol_1_18();
			log.info("Using protocol for 1.18 versions compatibility!");
		} else {
			ap = new Protocol_1_13();
			log.info("Using default protocol (1.13) for versions compatibility!");
		}
	}
	
	/**
	 * Returns the protocol version to allow graceful fall back for different spigot's versions.
	 * @return ap
	 */
	public static AreaProtocol getProtocol() {
		return ap;
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
	public Queue getQueue() {
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
		String status = ChatColor.DARK_AQUA + "Status: ";
		if (wep != null && wep.isEnabled()) {
			return status + enabled;
		} else {
			return status + disabled;
		}
	}
	
	/**
	 * Shut down all active tasks.
	 */
	public void ShutDown() {
		if (!getInstance().getServer().getScheduler().getPendingTasks().isEmpty()) {
			getInstance().getServer().getScheduler().getPendingTasks().clear();
		}
		if (!getInstance().getServer().getScheduler().getActiveWorkers().isEmpty()) {
			getInstance().getServer().getScheduler().getActiveWorkers().clear();;
		}
		if (!getQueue().get().isEmpty()) {
			getQueue().get().clear();
		}
	}
}