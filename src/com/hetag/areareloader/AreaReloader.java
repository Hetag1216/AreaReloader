package com.hetag.areareloader;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hetag.areareloader.commands.Executor;
import com.hetag.areareloader.commands.TPSMonitorCommand;
import com.hetag.areareloader.configuration.Manager;
import com.hetag.areareloader.reflection.AreaProtocol;
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
	public static ArrayList<String> isDeleted = new ArrayList<>();
	private Queue queue;

	public void onEnable() {
		plugin = this;
		log = getLogger();

		log.info("-=-=-=-= AreaReloader " + plugin.getDescription().getVersion() + " =-=-=-=-");
		checkProtocol();
		PluginManager pm = Bukkit.getPluginManager();
		wep = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		if (wep == null) {
			log.warning("Worldedit hook was not found, the plugin cannot be enabled without this dependency.");
			pm.disablePlugin(this);
		} else {
			log.info("Plugin's dependency has been found!");
		}
		if (TPSMonitorCommand.enabled) {
			Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new TPS(), 0L, 1L);
		}
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
		log.info("Succesfully enabled AreaReloader!");
		log.info("-=-=-=-= -=- =-=-=-=-");
	}

	public void onDisable() {
		ShutDown();
		log.info("Succesfully disabled AreaReloader!");
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
		case "v1_14_R2":
			ap = new Protocol_1_14();
			break;
		case "v1_15_R1":
		case "v1_15_R2":
			ap = new Protocol_1_15();
			break;
		case "v1_16_R1":
		case "v1_16_R2":
			ap = new Protocol_1_16();
			break;
		case "v1_17_R1":
		case "v1_17_R2":
			ap = new Protocol_1_17();
			break;
		case "v1_18_R1":
		case "v1_18_R2":
			ap = new Protocol_1_18();
			break;
		}
		if (ap.equals(new Protocol_1_13())) {
			log.info("Using default protocol (1.13) for versions compatibility!");
		} else if (ap.equals(new Protocol_1_14())) {
			log.info("Using protocol for 1.14 versions compatibility!");
		} else if (ap.equals(new Protocol_1_15())) {
			log.info("Using protocol for 1.15 versions compatibility!");
		} else if (ap.equals(new Protocol_1_16())) {
			log.info("Using protocol for 1.16 versions compatibility!");
		} else if (ap.equals(new Protocol_1_17())) {
			log.info("Using protocol for 1.17 versions compatibility!");
		} else if (ap.equals(new Protocol_1_18())) {
			log.info("Using protocol for 1.18 versions compatibility!");
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
		if (wep != null) {
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
		
		AreaMethods.updateAreas();
		
		if (!getQueue().get().isEmpty()) {
			getQueue().get().clear();
		}
	}
}