package com.hetag.areareloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
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
	public int chunks;
	private int maxChunks;
	private Location location;
	private boolean completed = false;
	private CommandSender sender;
	private static long fakeTime;
	public static double requiredTPS, percentage;

	public AreaLoader(String area, int x, int z, int size, Location location, CommandSender sender) {
		if (sender != null) {
			this.sender = sender;
		}
		if (AreaReloader.getInstance().getQueue().isQueued(area)) {
			if (AreaReloader.debug) {
				if (getSender() != null)
				AreaMethods.sendDebugMessage(getSender(), "'" + ChatColor.AQUA + area + ChatColor.DARK_AQUA + "' is already being loaded.");
			}
			if (getSender() != null)
			getSender().sendMessage(prefix() + "Area '" + ChatColor.AQUA + area + ChatColor.DARK_AQUA + "' is already being loaded.");
			return;
		}
		delay = Manager.getConfig().getLong("Settings.AreaLoading.Interval");
		requiredTPS = Manager.getConfig().getDouble("Settings.AreaLoading.RequiredTPS");
		percentage = Manager.getConfig().getDouble("Settings.AreaLoading.Percentage");
		this.area = area;
		this.maxX = x;
		this.maxZ = z;
		this.size = size;
		chunks = 0;
		x++;
		z++;
		this.maxChunks = (x * z);
		this.location = location;
		fakeTime = System.currentTimeMillis();
		areas.add(this);
		int count = 1;
		AreaReloader.getInstance().getQueue().queue().put(area, count += 1);
	}

	private void progress() throws FileNotFoundException, WorldEditException, IOException {
		if (!AreaMethods.loadSchematicArea(sender, area, AreaMethods.getFileName(area, x, z), location.getWorld(), location.clone().add(x * size, 0.0D, z * size))) {
			AreaReloader.log.warning("Failed to reset section '" + AreaMethods.getFileName(area, x, z) + "'!");
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 0.5F);
			}
			return;
		} else {
			chunks += 1;
			z += 1;
		}
		if (z > this.maxZ) {
			z = 0;
			x += 1;
		}
		if (chunks == this.maxChunks) {
			z -= 1;
			complete();
			return;
		}
	}

	private void complete() {
		this.completed = true;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
		}
	}

	private static void progressAll() {
		List<Integer> completed = new ArrayList<Integer>();
		for (AreaLoader al : areas) {
			if (al.completed) {
				if ((al.sender != null)) {
					final long time = System.currentTimeMillis() - fakeTime;
					al.sender.sendMessage(prefix() + onLoadSuccess().replaceAll("%area%", al.area).replaceAll("%time%", String.valueOf(time)));
				}
				completed.add(areas.indexOf(al));
				// only remove the area from the queue when it's finished
				if (AreaReloader.getInstance().getQueue().queue().containsKey(al.area)) {
					AreaReloader.getInstance().getQueue().queue().remove(al.area);
					if (AreaReloader.debug) {
						AreaMethods.sendDebugMessage(al.getSender(), ChatColor.DARK_AQUA + al.area + ChatColor.AQUA + " has been removed from the queue list.");
					}
				}
			} else {
				try {
					if (!AreaReloader.isDeleted.contains(al.area)) {
					al.progress();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (WorldEditException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				int perc = (int) (al.chunks * 100.0D / al.maxChunks);
					if ((Math.round(perc) % percentage == 0L) && (Math.round(perc) % 100L != 0L) && (al.sender != null)) {
						al.sender.sendMessage(prefix() + "Loading area '" + ChatColor.AQUA + al.area + ChatColor.DARK_AQUA + "' " + ChatColor.AQUA + perc + "%" + ChatColor.DARK_AQUA + ".");
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
				if (TPS.getTPS() >= requiredTPS) {
				AreaLoader.progressAll();
				}
			}
		};
		AreaReloader.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(AreaReloader.plugin, br, 0L, delay / 1000 * 20);
	}

	public static String prefix() {
		return ChatColor.translateAlternateColorCodes('&', AreaReloader.plugin.getConfig().getString("Settings.Language.ChatPrefix"));
	}

	private static String onLoadSuccess() {
		return ChatColor.translateAlternateColorCodes('&', AreaReloader.plugin.getConfig().getString("Commands.Load.OnLoadSuccess"));
	}

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}
	
	/**
	 * 
	 * @return the maxZ
	 */
	public int getMaxZ() {
		return maxZ;
	}
	
	/**
	 * 
	 * @return the original X point
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * 
	 * @return the original Z point
	 */
	public int getZ() {
		return z;
	}

	/**
	 * @param maxX
	 * the maxX to set
	 */
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}
	
	public void setMaxZ(int maxZ) {
		this.maxZ = maxZ;
	}
	
	public CommandSender getSender() {
		return sender != null ? sender : null;
	}
}