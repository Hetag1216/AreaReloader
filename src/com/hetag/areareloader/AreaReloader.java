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
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AreaReloader extends JavaPlugin implements Listener {
	public static AreaReloader plugin;
	public static Logger log;
	public WorldEditPlugin wep;
	public static Config areas;
	public static boolean debug;

	public void onEnable() {
		plugin = this;
		log = getLogger();
		debug = Manager.getConfig().getBoolean("Settings.Debug.Enabled");

		log.info("-=-=-=-= AreaReloader " + plugin.getDescription().getVersion() + " =-=-=-=-");
		PluginManager pm = Bukkit.getPluginManager();
		wep = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		if (this.wep == null) {
			log.warning("Worldedit hook was not found, the plugin cannot be enabled without this dependency.");
			pm.disablePlugin(this);
		}
		try {
			AreaMethods.performSetup();
			new Manager(this);
			areas = new Config(new File("areas.yml"));
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
		AreaLoader.manage();
		getServer().getPluginManager().registerEvents(new AreaListener(this), this);
		log.info("Succesfully enabled AreaReloader!");
		log.info("-=-=-=-= -=- =-=-=-=-");
	}

	public void onDisable() {
		log.info("Succesfully disabled AreaReloader!");
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