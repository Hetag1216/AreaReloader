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
	public static long delay;
	public static boolean notifyOnReload;
	private long reset;

	@SuppressWarnings("unlikely-arg-type")
	public AreaScheduler(String area, long delay) {
		if (areas.contains(area)) {
			return;
		}
		this.area = area;
		notifyOnReload = Manager.getConfig().getBoolean("Settings.AutoReload.NotifyWhenReloading");
		AreaScheduler.delay = delay;
		areas.add(this);
		AreaReloader.log.info("Automatically reloading area: " + area);
	}

	@SuppressWarnings("static-access")
	public static void progress() {
		for (AreaScheduler scheduler : areas) {
			if (System.currentTimeMillis() >= scheduler.getDelay() + scheduler.getLastReset()) {
				World world = Bukkit.getServer().getWorld(config.getString("Areas." + scheduler.getArea() + ".World"));
				int x = config.getInt("Areas." + scheduler.getArea() + ".X");
				int z = config.getInt("Areas." + scheduler.getArea() + ".Z");
				Location location = new Location(world, x, 0, z);

				new AreaLoader(scheduler.getArea(), AreaMethods.getAreaMaxX(scheduler.getArea()).intValue(), 0, AreaMethods.getAreaMaxZ(scheduler.getArea()).intValue(), location, null);
				if (notifyOnReload) {
					for (Player ops : Bukkit.getServer().getOnlinePlayers()) {
						if (ops.isOp()) {
							ops.sendMessage(AreaLoader.prefix() + "Automatically reloading area: " + ChatColor.AQUA + scheduler.getArea() + ChatColor.DARK_AQUA + ".");
							ops.getWorld().playSound(ops.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5F, 0.3F);
						}
					}

				}
				scheduler.setLastReset(System.currentTimeMillis());
				return;
			}
		}
	}

	public static void progressAreas() {
		if (config.contains("Areas")) {
			for (String keys : config.getConfigurationSection("Areas").getKeys(false)) {
				if (config.contains("Areas." + keys + ".AutoReload.Enabled") && config.getBoolean("Areas." + keys + ".AutoReload.Enabled")) {

					long resetTime = config.getLong("Areas." + keys + ".AutoReload.Time");
					new AreaScheduler(keys, resetTime);
					progress();
				}
			}
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public static void updateDelay(String area, long delay) {
		for (AreaScheduler s : areas) {
			if (s.getArea().equalsIgnoreCase(area)) {
				s.setDelay(delay);
				s.setLastReset(System.currentTimeMillis());
				areas.remove(area);
				return;
			}
		}
		new AreaScheduler(area, delay);
	}

	@SuppressWarnings("static-access")
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

	public static long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		AreaScheduler.delay = delay;
	}

	public static void manageReloading() {
		Runnable br = new Runnable() {
			public void run() {
				progressAreas();
			}
		};
		AreaReloader.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(AreaReloader.plugin, br, 0L, getDelay());
	}

	public long getLastReset() {
		return this.reset;
	}

	public void setLastReset(long reset) {
		this.reset = Long.valueOf(reset);
	}

}
