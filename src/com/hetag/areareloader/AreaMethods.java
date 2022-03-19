package com.hetag.areareloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hetag.areareloader.configuration.Manager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;

public class AreaMethods {
	static AreaReloader plugin;
	public static boolean ignoreAirBlocks = Manager.getConfig().getBoolean("Settings.AreaLoading.IgnoreAirBlocks");
	public static boolean fastMode = Manager.getConfig().getBoolean("Settings.AreaLoading.FastMode");
	public static HashMap<String, EditSession> active_sessions = new HashMap<String, EditSession>();
	public static int blocks = 0;

	public static void performSetup() {
		File areas = new File(AreaReloader.plugin.getDataFolder() + File.separator + "Areas");
		if (!areas.exists()) {
			try {
				areas.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteArea(String area) {
		kill(area);
		Manager.areas.getConfig().set("Areas." + area, null);
		Manager.areas.saveConfig();
		File dir = new File(AreaReloader.plugin.getDataFolder() + File.separator + "Areas" + File.separator + area);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if ((files != null) && (files.length != 0)) {
				File[] arrayOfFile1;
				int j = (arrayOfFile1 = files).length;
				for (int i = 0; i < j; i++) {
					File file = arrayOfFile1[i];
					file.delete();
				}
			}
			dir.delete();
			//AreaReloader.isDeleted.add(area);
		}
	}

	public static boolean isInteger(String s) {
		return isInteger(s, 10);
	}
	
	public static String formatTime(long time) {
		time = Math.abs(time);
		final long days = time / TimeUnit.DAYS.toMillis(1);
		final long hours = time % TimeUnit.DAYS.toMillis(1) / TimeUnit.HOURS.toMillis(1);
		final long minutes = time % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
		final long seconds = time % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
		final long millis = time / TimeUnit.MILLISECONDS.toMillis(1);
		String format = "";
		if (days > 0) {
			format += String.valueOf(days) + " days";
		}
		if (hours > 0) {
			format += String.valueOf(hours) + " hours";
		}
		if (minutes > 0) {
			format += String.valueOf(minutes) + " minutes";
		}
		if (seconds >= 0) {
			format += String.valueOf(seconds) + "." + String.valueOf(Math.round(millis)) +  " seconds";
		}
		return format;
	}

	public static boolean isInteger(String s, int radix) {
		if (s.isEmpty()) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if ((i == 0) && (s.charAt(i) == '-')) {
				if (s.length() == 1) {
					return false;
				}
			} else if (Character.digit(s.charAt(i), radix) < 0) {
				return false;
			}
		}
		return true;
	}

	public static Integer getMaxInt(int min, int max, int size) {
		if (max - min < size) {
			return Integer.valueOf(max - min);
		}
		return Integer.valueOf(size);
	}

	public static boolean loadSchematicArea(CommandSender p, String area, String schemFile, World world, Location location) throws WorldEditException, FileNotFoundException, IOException {
		File file = new File(AreaReloader.plugin.getDataFolder() + File.separator + "Areas" + File.separator + area + File.separator + schemFile + ".schematic");
		if (!file.exists()) {
			return false;
		}
		if (AreaReloader.debug) {
			if (p != null)
			sendDebugMessage(p, "Schematic file found");
		}

		ClipboardFormat format = ClipboardFormats.findByFile(file);

		if (AreaReloader.debug) {
			if (p != null)
			sendDebugMessage(p, "Schematic format found.");
		}

		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			Clipboard clipboard = reader.read();

			if (AreaReloader.debug) {
				if (p != null)
				sendDebugMessage(p, "Reading schematic.");
			}

			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), Integer.MAX_VALUE)) {
				active_sessions.put(area, editSession);
				if (fastMode) {
				editSession.setFastMode(true);
				} else {
					editSession.setFastMode(false);
				}
				if (AreaReloader.debug) {
					if (p != null)
					sendDebugMessage(p, "Initializing edit session.");
				}
				Operation operation = null;
				if (ignoreAirBlocks) {
				operation = new ClipboardHolder(clipboard).createPaste(editSession)
						.to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
						.ignoreAirBlocks(false)
						.maskSource(new BlockTypeMask(editSession, BlockTypes.AIR))
						.build();
				} else {	
					operation = new ClipboardHolder(clipboard).createPaste(editSession)
						.to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
						.ignoreAirBlocks(false)
						.build();	
				}

				if (AreaReloader.debug) {
					if (p != null)
					sendDebugMessage(p, "Ran building operations.");
				}

				Operations.complete(operation);

				if (AreaReloader.debug) {
					if (p != null)
					sendDebugMessage(p, "Operations succesfully completed!");
				}
			}
		}
		return true;
	}
	
	public static int finalCount() {
		for (EditSession entry : active_sessions.values()) {
			blocks =+ entry.getBlockChangeCount();
			return blocks;
		}
		return 0;
	}
	
	public static HashMap<String, EditSession> getActiveSessions() {
		if (active_sessions.size() >= 1) {
		return active_sessions;
		} else {
			return null;
		}
	}

	public static boolean createNewArea(Player player, String area, int size, boolean copyEntities) throws WorldEditException {
		//if (AreaReloader.isDeleted.contains(area)) {
			//AreaReloader.isDeleted.remove(area);
			if (AreaReloader.debug) {
				sendDebugMessage(player, "Updating selected area.");
			//}
		}
            File dir = new File(AreaReloader.plugin.getDataFolder() + File.separator + "Areas" + File.separator + area);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if ((files != null) && (files.length != 0)) {
				File[] arrayOfFile1;
				int j = (arrayOfFile1 = files).length;
				for (int i = 0; i < j; i++) {
					File file = arrayOfFile1[i];
					file.delete();
				}
			}
			dir.delete();
		}
		BukkitPlayer lp = BukkitAdapter.adapt(player);
		LocalSession ls = WorldEdit.getInstance().getSessionManager().get(lp);
		Region sel = ls.getSelection(BukkitAdapter.adapt(player.getWorld()));
		int maxX = 0;
		int maxZ = 0;
		if ((sel instanceof CuboidRegion)) {
			int curX = 0;
			BlockVector3 min = sel.getMinimumPoint();
			BlockVector3 max = sel.getMaximumPoint();

			Manager.areas.getConfig().set("Areas." + area + ".World", sel.getWorld().getName());
			Manager.areas.getConfig().set("Areas." + area + ".HasCopiedEntities", copyEntities);
			Manager.areas.getConfig().set("Areas." + area + ".X", min.getBlockX());
			Manager.areas.getConfig().set("Areas." + area + ".Y", min.getBlockY());
			Manager.areas.getConfig().set("Areas." + area + ".Z", min.getBlockZ());
			Manager.areas.getConfig().set("Areas." + area + ".Maximum.Z", max.getBlockZ());
			Manager.areas.getConfig().set("Areas." + area + ".Maximum.Y", max.getBlockY());
			Manager.areas.getConfig().set("Areas." + area + ".Maximum.X", max.getBlockX());
			Manager.areas.saveConfig();
			for (int x = min.getBlockX(); x <= max.getBlockX(); x += size) {
				int curZ = 0;
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z += size) {
					EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(sel.getWorld(), Integer.MAX_VALUE);
					int maxY = max.getBlockY();
					Location pt1 = new Location(player.getWorld(), x, min.getBlockY(), z);
					Location pt2 = new Location(player.getWorld(), x + getMaxInt(x, max.getBlockX(), size).intValue(), maxY, z + getMaxInt(z, max.getBlockZ(), size).intValue());

					BlockVector3 bvmin = BukkitAdapter.asBlockVector(pt1);
					BlockVector3 bvmax = BukkitAdapter.asBlockVector(pt2);
					CuboidRegion region = new CuboidRegion(sel.getWorld(), bvmin, bvmax);

					BlockArrayClipboard cc = new BlockArrayClipboard(region);
					ForwardExtentCopy clipCopy = new ForwardExtentCopy(es, region, cc, region.getMinimumPoint());
					clipCopy.setCopyingEntities(copyEntities);
					Operations.complete(clipCopy);
					if (AreaReloader.debug) {
						sendDebugMessage(player, "Succesfully copied the selected clipboard to system.");
					}
					File file = new File(AreaReloader.plugin.getDataFolder() + File.separator + "Areas" + File.separator + area + File.separator + getFileName(area, curX, curZ) + ".schematic");
					if (file.exists()) {
						file.delete();
					}
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
						writer.write(cc);
						if (AreaReloader.debug) {
							sendDebugMessage(player, "Clipboard succesfully saved to file.");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					curZ++;
					maxZ = curZ;
				}
				curX++;
				maxX = curX;
			}
			maxX--;
			maxZ--;
			
			Manager.areas.getConfig().set("Areas." + area + ".Size.X", maxX);
			Manager.areas.getConfig().set("Areas." + area + ".Size.Z", maxZ);
			Manager.areas.getConfig().set("Areas." + area + ".Size.Chunk", size);
			Manager.areas.getConfig().set("Areas." + area + ".AutoReload.Enabled", false);
			Manager.areas.getConfig().set("Areas." + area + ".AutoReload.Time", 200000);
			Manager.areas.saveConfig();
			return true;
		}
		return false;
	}
	
	public static void kill(String area) {
		if (AreaReloader.getInstance().getQueue().isQueued(area)) {
			AreaReloader.getInstance().getServer().getScheduler().cancelTask(AreaReloader.getInstance().getQueue().getTaskByName(area));
			AreaReloader.getInstance().getQueue().remove(area);
		}
		if (AreaLoader.areas.contains(area))
			AreaLoader.areas.remove(area);
		
		if (AreaScheduler.areas.contains(area)) {
			AreaScheduler.areas.remove(area);
		}
	}
	public static String getXCoord(String area) {
		return Manager.areas.getConfig().getString("Areas." + area + ".X");
	}
	
	public static String getZCoord(String area) {
		return Manager.areas.getConfig().getString("Areas." + area + ".Z");
	}
	
	public static String getAreaInWorld(String area) {
		return Manager.areas.getConfig().getString("Areas." + area + ".World");
	}
	
	public static String getFileName(String file, int x, int z) {
		return file + "_" + x + "_" + z;
	}

	public static Integer getAreaSizeX(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Size.X");
	}

	public static Integer getAreaSizeZ(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Size.Z");
	}
	
	public static Integer getAreaMaxX(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Maximum.X");
	}
	
	public static Integer getAreaMaxY(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Maximum.Y");
	}
	
	public static Integer getAreaMaxZ(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Maximum.Z");
	}
	
	public static Integer getAreaX(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".X");
	}
	
	public static Integer getAreaY(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Y");
	}
	
	public static Integer getAreaZ(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Z");
	}

	public static Integer getAreaChunk(String area) {
		return Manager.areas.getConfig().getInt("Areas." + area + ".Size.Chunk");
	}

	public static void reloadConfig() {
		AreaReloader.plugin.reloadConfig();
	}
	
	public static void sendDebugMessage(Player player, String string) {
		player.sendMessage(debugPrefix() + string);
	}

	public static void sendDebugMessage(CommandSender sender, String string) {
		sender.sendMessage(debugPrefix() + string);
	}
	
	/*public static void updateAreas() {
		if (AreaReloader.isDeleted.isEmpty()) {
			return;
		} else {
			AreaReloader.isDeleted.clear();
		}
	}*/

	public static String debugPrefix() {
		return ChatColor.translateAlternateColorCodes('&', AreaReloader.plugin.getConfig().getString("Settings.Debug.Prefix"));
	}
}
