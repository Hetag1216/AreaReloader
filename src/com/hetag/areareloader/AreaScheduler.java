package com.hetag.areareloader;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.hetag.areareloader.configuration.Manager;

public class AreaScheduler {
	public static List<AreaScheduler> areas = new ArrayList<>();
	public static FileConfiguration config = AreaReloader.areas.getConfig();

	public String area;
	public static boolean notifyOnReload, notifyConsoleOnReload, checker, useTPSChecker;
	private long reset;
	private long delay;
	public static double requiredTPS;
	public static long interval;

	public AreaScheduler(String area, long delay) {
		if (areas.contains(this)) {
			return;
		}
		if (AreaReloader.getInstance().getQueue().isQueued(area)) {
			updateDelay(area, delay);
			return;
		}
		this.delay = delay;
		this.reset = System.currentTimeMillis();
		areas.add(this);
		int count = 0;
		AreaReloader.getInstance().getQueue().queue().put(area, count++);
	}
	
	public static void init() {
		checker = Manager.getConfig().getBoolean("Settings.AutoReload.Checker");
		requiredTPS = Manager.getConfig().getDouble("Settings.AutoReload.RequiredTPS");		
		notifyOnReload = Manager.getConfig().getBoolean("Settings.AutoReload.Notify.Admins");
		notifyConsoleOnReload = Manager.getConfig().getBoolean("Settings.AutoReload.Notify.Console");
		useTPSChecker = Manager.getConfig().getBoolean("Settings.AutoReload.TPSChecker.Enabled");
		requiredTPS = Manager.getConfig().getDouble("Settings.AutoReload.TPSChecker.RequiredTPS");
		interval = Manager.getConfig().getLong("Settings.AutoReload.Interval");
		
		if (checker) {
			AreaReloader.log.info("Checker for areas to auto reload is enabled!");
			checkForAreas();
			manageReloading();
		} else {
			AreaReloader.log.info("Checker for areas to auto reload is disabled!");
		}
	}
	
	public static void checkForAreas() {
		if (config.contains("Areas")) {
			for (String keys : config.getConfigurationSection("Areas").getKeys(false)) {
				if (config.contains("Areas." + keys + ".AutoReload.Enabled") && config.getBoolean("Areas." + keys + ".AutoReload.Enabled") == true) {
					long resetTime = config.getLong("Areas." + keys + ".AutoReload.Time");
					new AreaScheduler(keys, resetTime);
				}
			}
		}
	}



	public static void updateDelay(String area, long delay) {
		for (AreaScheduler s : areas) {
			if (s.getArea().equalsIgnoreCase(area)) {
				s.setDelay(delay);
				s.setLastReset(System.currentTimeMillis());
				return;
			}
		}
		new AreaScheduler(area, delay);
	}

	public static long getRemainingTime(String area) {
		for (AreaScheduler scheduler : areas) {
			if (scheduler.getArea().equalsIgnoreCase(area)) {
				return scheduler.getLastReset() + scheduler.getDelay() - System.currentTimeMillis();
			}
		}
		return 0L;
	}
	
	public static String getAreas() {
		if (config.contains("Areas")) {
			for (String keys : config.getConfigurationSection("Areas").getKeys(false)) {
				if (config.contains("Areas." + keys + ".AutoReload.Enabled") && config.getBoolean("Areas." + keys + ".AutoReload.Enabled") == true) {
					return keys;
				}
			}
		}
		return null;
	}
	
	public static long getAreasResetTime() {
		if (config.contains("Areas")) {
			for (String keys : config.getConfigurationSection("Areas").getKeys(false)) {
				if (config.contains("Areas." + keys + ".AutoReload.Enabled") && config.getBoolean("Areas." + keys + ".AutoReload.Enabled") == true) {
					long resetTime = config.getLong("Areas." + keys + ".AutoReload.Time");
					return resetTime;
				}
			}
		}
		return 0;
	}
	public String getArea() {
		return this.area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public long getDelay() {
		return this.delay;
	}

	public void setDelay(long delay) {
		this.delay = Long.valueOf(delay);
	}

	public long getLastReset() {
		return this.reset;
	}

	public void setLastReset(long reset) {
		this.reset = reset;
	}

	public static void progress() {
		for (AreaScheduler scheduler : areas) {
			if (System.currentTimeMillis() >= scheduler.getDelay() + scheduler.getLastReset()) {
				World world = Bukkit.getServer().getWorld(config.getString("Areas." + scheduler.getArea() + ".World"));
				int x = AreaMethods.getAreaX(scheduler.getArea());
				int z = AreaMethods.getAreaZ(scheduler.getArea());
				int size = AreaMethods.getAreaChunk(scheduler.getArea());
				int maxX = AreaMethods.getAreaSizeX(scheduler.getArea());
				int maxZ = AreaMethods.getAreaSizeZ(scheduler.getArea());
				Location location = new Location(world, x, 0, z);
				if (!AreaReloader.isDeleted.contains(scheduler.getArea())) {
					new AreaLoader(scheduler.getArea(), maxX, maxZ, size, location, null);
				    if (notifyConsoleOnReload) {   
					AreaReloader.log.info("Automatically reloading area: " + scheduler.getArea());
				    }
					if (notifyOnReload) {
						for (Player ops : Bukkit.getServer().getOnlinePlayers()) {
							if (ops.isOp() || ops.hasPermission("areareloader.command.admin")) {
								ops.sendMessage(AreaLoader.prefix() + "Automatically reloading area: " + ChatColor.AQUA + scheduler.getArea() + ChatColor.DARK_AQUA + ".");
								ops.getWorld().playSound(ops.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 0.3F);
							}
						}
					}
					scheduler.setLastReset(System.currentTimeMillis());
				}
			}
		}
	}

	public static void manageReloading() {
		Runnable br = new Runnable() {
			public void run() {
				if (useTPSChecker) {
					if (TPS.getTPS() >= requiredTPS)
						progress();
				} else {
					progress();
				}
			}
		};
		AreaReloader.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(AreaReloader.plugin, br, 0, interval / 1000 * 20);
	}

}
