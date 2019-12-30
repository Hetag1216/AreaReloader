package com.hetag.areareloader;

import java.io.File;
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
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AreaReloader extends JavaPlugin implements Listener {
	public static AreaReloader plugin;
	public static Logger log;
	public WorldEditPlugin wep;
	private AreaProtocol ap;
	public static Config areas;
	public static boolean debug;
	public static long interval;
	public static boolean checker;

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
		
		try {
			AreaMethods.performSetup();
			new Manager();
			areas = new Config(new File("areas.yml"));
			debug = Manager.getConfig().getBoolean("Settings.Debug.Enabled");
			interval = Manager.getConfig().getLong("Settings.AreaLoading.Interval");
			checker = Manager.getConfig().getBoolean("Settings.AutoReload.Checker");
			log.info("Configurations succesfully registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			new Executor(this);
			log.info("Commands succesfully registered!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AreaLoader.manageLoading();
		
		/**
		 * Whether or not to check and enable/disable the auto reloading function.
		 * @return checker
		 */
		if (checker) {
			try {
				AreaScheduler.checkForAreas();
				AreaScheduler.manageReloading();
				log.info("Correctly checking for areas auto reloading!");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		getServer().getPluginManager().registerEvents(new AreaListener(this), this);
		log.info("Succesfully enabled AreaReloader!");
		log.info("-=-=-=-= -=- =-=-=-=-");
	}

	public void onDisable() {
		log.info("Succesfully disabled AreaReloader!");
	}

	public void checkProtocol() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		String formmatedVersion = version.substring(version.lastIndexOf(".") + 1);

		switch (formmatedVersion) {
		case "v1_13_R2":
		case "v1_13_R1":
			ap = new Protocol_1_13();
			break;
		case "v1_14_R1":
		default:
			ap = new Protocol_1_14();
			break;
		}
		if (ap.equals(new Protocol_1_13())) {
			log.info("Using protocol for 1.13 versions compatibility!");
		} else if (ap.equals(new Protocol_1_14())) {
			log.info("Using protocol for 1.14 versions compatibility!");
		}
	}

	public AreaProtocol getProtocol() {
		return ap;
	}

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