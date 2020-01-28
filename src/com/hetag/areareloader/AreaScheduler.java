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
	public static boolean notifyOnReload;
	private long reset;
	private long delay;

	@SuppressWarnings("unlikely-arg-type")
	public AreaScheduler(String area, long delay) {
		if (areas.contains(area)) {
			return;
		}
		this.area = area;
		this.delay = delay;
		this.reset = System.currentTimeMillis();
		notifyOnReload = Manager.getConfig().getBoolean("Settings.AutoReload.NotifyWhenReloading");
		areas.add(this);
	}

	public static void checkForAreas() {
		if (config.contains("Areas")) {
			for (String keys : config.getConfigurationSection("Areas").getKeys(false)) {
				if (config.contains("Areas." + keys + ".AutoReload.Enabled")
						&& config.getBoolean("Areas." + keys + ".AutoReload.Enabled") == true) {
					long resetTime = config.getLong("Areas." + keys + ".AutoReload.Time");
					new AreaScheduler(keys, resetTime);
				}
			}
		}
	}

	public static void updateDelay(String arena, long delay) {
		for (AreaScheduler s : areas) {
			if (s.getArea().equalsIgnoreCase(arena)) {
				s.setDelay(delay);
				s.setLastReset(System.currentTimeMillis());
				return;
			}
		}
		new AreaScheduler(arena, delay);
	}

	public static long getRemainingTime(String area) {
		for (AreaScheduler scheduler : areas) {
			if (scheduler.getArea().equalsIgnoreCase(area)) {
				return scheduler.getLastReset() + scheduler.getDelay() - System.currentTimeMillis();
			}
		}
		return 0L;
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
				        
							AreaReloader.log.info("Automatically reloading area: " + scheduler.getArea());
					if (notifyOnReload) {
						for (Player ops : Bukkit.getServer().getOnlinePlayers()) {
							if (ops.isOp()) {
								ops.sendMessage(AreaLoader.prefix() + "Automatically reloading area: " + ChatColor.AQUA
										+ scheduler.getArea() + ChatColor.DARK_AQUA + ".");
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
				progress();
			}
		};
		AreaReloader.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(AreaReloader.plugin, br, 0,
				AreaReloader.interval / 1000 * 20);
	}

}
