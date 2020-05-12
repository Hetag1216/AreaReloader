package com.hetag.areareloader;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hetag.areareloader.commands.Executor;
import com.hetag.areareloader.configuration.Config;
import com.hetag.areareloader.configuration.Manager;
import com.hetag.areareloader.reflection.AreaProtocol;
import com.hetag.areareloader.reflection.V1_13.Protocol_1_13;
import com.hetag.areareloader.reflection.V1_14.Protocol_1_14;
import com.hetag.areareloader.reflection.V1_15.Protocol_1_15;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AreaReloader extends JavaPlugin implements Listener {
	public static AreaReloader plugin;
	public static Logger log;
	public WorldEditPlugin wep;
	public static AreaProtocol ap;
	public static Config areas;
	public static boolean debug, checker, useQueue;
	public static long interval;
	public static ArrayList<String> isDeleted = new ArrayList<>();
	public Queue queue;
	//public static HashMap<String, Integer> QUEUE;

	public void onEnable() {
		plugin = this;
		log = getLogger();

		log.info("-=-=-=-= AreaReloader " + plugin.getDescription().getVersion() + " =-=-=-=-");
		checkProtocol();
		PluginManager pm = Bukkit.getPluginManager();
		wep = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		if (this.wep == null) {
			log.warning("Worldedit hook was not found, the plugin cannot be enabled without this dependency.");
			pm.disablePlugin(this);
		}
		
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new TPS(), 0L, 1L);
		try {
			new Manager();
			AreaMethods.performSetup();
			areas = new Config(new File("areas.yml"));
			debug = Manager.getConfig().getBoolean("Settings.Debug.Enabled");
			interval = Manager.getConfig().getLong("Settings.AreaLoading.Interval");
			useQueue = Manager.getConfig().getBoolean("Settings.Queue.Enabled");
			checker = Manager.getConfig().getBoolean("Settings.AutoReload.Checker");
			log.info("Configurations succesfully registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (useQueue) {
			//QUEUE = new HashMap<String, Integer>();
			queue = new Queue(this);
		}

		try {
			new Executor(this);
			log.info("Commands succesfully registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AreaLoader.manage();
		
		/**
		 * Whether or not to check and enable/disable the auto reloading function.
		 * @return checker
		 */
		if (checker) {
			try {
				AreaScheduler.checkForAreas();
				AreaScheduler.manageReloading();
				log.info("Checker for areas to auto reload is enabled!");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.info("Checker for areas to auto reload is disabled!");
		}
		
		getServer().getPluginManager().registerEvents(new AreaListener(this), this);
		log.info("Succesfully enabled AreaReloader!");
		log.info("-=-=-=-= -=- =-=-=-=-");
	}

	public void onDisable() {
		log.info("Succesfully disabled AreaReloader!");
		AreaMethods.updateAreas();
		if (!getQueue().queue().isEmpty()) {
			getQueue().queue().clear();;
		}
	}

	public void checkProtocol() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		String formmatedVersion = version.substring(version.lastIndexOf(".") + 1);

		switch (formmatedVersion) {
		default:
			ap = new Protocol_1_13();
			break;
		case "v1_13_R2":
		case "v1_13_R1":
			ap = new Protocol_1_13();
			break;
		case "v1_14_R1":
			ap = new Protocol_1_14();
			break;
		case "v1_15_R1":
			ap = new Protocol_1_15();
			break;
		}
		if (ap.equals(new Protocol_1_13())) {
			log.info("Using protocol for 1.13 versions compatibility!");
		} else if (ap.equals(new Protocol_1_14())) {
			log.info("Using protocol for 1.14 versions compatibility!");
		} else if (ap.equals(new Protocol_1_15())) {
			log.info("Using protocol for 1.15 versions compatibility!");
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
	 * Stores all areas queued for reloading.
	 * <p>
	 * This does not store instances but the areas themselves.
	 * <p>
	 * Every time the server is reloaded, restarted or the /ar command is ran, the
	 * queue gets cleared.
	 * <p>
	 * If {@link #useQueue} returns true
	 * @return QUEUE
	 */
	/*public HashMap<String, Integer> getQueue() {
		if (QUEUE != null) return QUEUE;
		return null;
	}*/
	
	public Queue getQueue() {
		return queue;
	}
	
	/**
	 * Gets the status of AreaReloader's hooks.
	 * @return status
	 */
	public String getStatus() {
		String enabled = ChatColor.GREEN + "Enabled";
		String disabled = ChatColor.RED + "Disabled";
		String status = ChatColor.DARK_AQUA + "Status: ";
		if (wep != null) {
			return status + enabled;
		} else {
			return status + disabled;
		}
	}
	
}