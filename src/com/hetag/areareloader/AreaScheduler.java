package com.hetag.areareloader;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class AreaScheduler {
	public static List<AreaScheduler> areas = new ArrayList<>();
	public static FileConfiguration config = AreaReloader.areas.getConfig();

	public String area;
	public static long delay = 20000;
	private long reset;

	@SuppressWarnings("unlikely-arg-type")
	public AreaScheduler(String area, long delay) {
		if (areas.contains(area)) {
			return;
		}
		this.area = area;
		AreaScheduler.delay = delay;
		areas.add(this);
		AreaReloader.log.info("Automatically loading: " + area);
	}

	public static void progress() {
		for (AreaScheduler scheduler : areas) {
			if (System.currentTimeMillis() >= scheduler.getDelay() + scheduler.getLastReset()) {
				World world = Bukkit.getServer().getWorld(config.getString("Areas." + scheduler.getArea() + ".World"));
				int x = config.getInt("Areas." + scheduler.getArea() + ".X");
				int z = config.getInt("Areas." + scheduler.getArea() + ".Z");
				Location location = new Location(world, x, 0, z);

				new AreaLoader(scheduler.getArea(), AreaMethods.getAreaMaxX(scheduler.getArea()).intValue(), 0,
						AreaMethods.getAreaMaxZ(scheduler.getArea()).intValue(), location, null);
				Bukkit.getServer().broadcastMessage("Succesfully loading area: " + scheduler.getArea());
				return;
			}
		}
	}

	public static boolean progressAreas() {
		if (config.contains("Areas")) {
			for (String keys : config.getConfigurationSection("Areas").getKeys(false)) {
				if (config.contains("Areas." + keys + ".AutoReload.Enabled")
						&& config.getBoolean("Areas." + keys + ".AutoReload.Enabled")) {
					progress();

					long resetTime = config.getLong("Areas." + keys + ".AutoReload.Time");
					new AreaScheduler(keys, resetTime);
					Bukkit.getServer().broadcastMessage("Succesfully loading area: " + keys + "'key'");
					return true;
				}
			}
		}
		return false;
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

	public String getArea() {
		return this.area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		AreaScheduler.delay = delay;
	}

	public static void manage() {
		Runnable br = new Runnable() {
			public void run() {
				progressAreas();
			}
		};
		AreaReloader.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(AreaReloader.plugin, br, delay / 1000 * 20, delay / 1000 * 20);
	}

	public long getLastReset() {
		return this.reset;
	}

	public void setLastReset(long reset) {
		this.reset = Long.valueOf(reset);
	}

}
