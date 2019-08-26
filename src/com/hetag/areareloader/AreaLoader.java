package com.hetag.areareloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hetag.areareloader.configuration.Manager;
import com.sk89q.worldedit.WorldEditException;

public class AreaLoader {
	public static List<AreaLoader> areas = new ArrayList<AreaLoader>();
	private static long delay;
	private String area;
	private int maxX;
	private int maxZ;
	private int x;
	private int z;
	private int size;
	private int chunks;
	private int maxChunks;
	private Location location;
	private boolean completed = false;
	private CommandSender sender;

	@SuppressWarnings("unlikely-arg-type")
	public AreaLoader(String arena, int x, int z, int size, Location location, CommandSender sender) {
		if (areas.contains(arena)) {
			return;
		}
		delay = Manager.getConfig().getLong("Settings.AreaLoading.Interval");
		this.area = arena;
		this.setMaxX(x);
		this.maxZ = z;
		this.size = size;
		this.chunks = 0;
		x++;
		z++;
		this.maxChunks = (x * z);
		this.location = location;
		if (sender != null) {
			this.sender = sender;
		}
		areas.add(this);
	}

	private void progress() throws FileNotFoundException, WorldEditException, IOException {
		this.chunks += 1;
		if (!AreaMethods.loadSchematicArea((Player) sender, this.area,
				AreaMethods.getFileName(this.area, this.x, this.z), this.location.getWorld(),
				this.location.clone().add(this.x * this.size, 0.0D, this.z * this.size))) {
			AreaReloader.log
					.warning("Failed to reset section '" + AreaMethods.getFileName(this.area, this.x, this.z) + "'!");
		} else {
			this.z += 1;
		}
		if (this.chunks == this.maxChunks) {
			this.z -= 1;
			complete();
			return;
		}
		if (this.z > this.maxZ) {
			this.z = 0;
			this.x += 1;
		}
	}

	private void complete() {
		this.completed = true;
	}

	private static void progressAll() {
		List<Integer> completed = new ArrayList<Integer>();
		for (AreaLoader al : areas) {
			if (al.completed) {
				// AreaReloader.log.info("Finished loading arena '" + al.area + "'. '" + al.x +
				// "_" + al.z + "' areas out of a total '" + al.maxX + "_" + al.maxZ + "'
				// loaded.");
				if ((al.sender != null) && ((al.sender instanceof Player)) && (((Player) al.sender).isOnline())) {
					al.sender.sendMessage(prefix() + onLoadSuccess().replaceAll("%area%", al.area));
				}
				completed.add(Integer.valueOf(areas.indexOf(al)));
			} else {
				try {
					al.progress();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (WorldEditException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				double percentage = al.chunks * 100.0D / al.maxChunks;
				if ((Math.round(percentage) % 25L == 0L) && (Math.round(percentage) % 100L != 0L) && (al.sender != null)
						&& ((al.sender instanceof Player)) && (((Player) al.sender).isOnline())) {
					al.sender.sendMessage(prefix() + "Loading area '" + ChatColor.AQUA + al.area + ChatColor.DARK_AQUA
							+ "' " + ChatColor.AQUA + percentage + "%" + ChatColor.DARK_AQUA + ".");
				}
			}
		}
		for (Iterator<Integer> iterator = completed.iterator(); iterator.hasNext();) {
			int id = ((Integer) iterator.next()).intValue();
			areas.remove(id);
		}
	}

	public static void manage() {
		Runnable br = new Runnable() {
			public void run() {
				AreaLoader.progressAll();
			}
		};
		AreaReloader.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(AreaReloader.plugin, br, 0L,
				delay / 1000 * 20);
	}

	private static String prefix() {
		return ChatColor.translateAlternateColorCodes('&',
				AreaReloader.plugin.getConfig().getString("Settings.Language.ChatPrefix"));
	}

	private static String onLoadSuccess() {
		return ChatColor.translateAlternateColorCodes('&',
				AreaReloader.plugin.getConfig().getString("Commands.Load.onLoadSuccess"));
	}

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX
	 *            the maxX to set
	 */
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}
}
