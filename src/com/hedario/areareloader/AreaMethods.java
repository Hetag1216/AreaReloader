package com.hedario.areareloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hedario.areareloader.commands.ARCommand;
import com.hedario.areareloader.commands.DisplayCommand;
import com.hedario.areareloader.configuration.Manager;
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
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;

public class AreaMethods {
	static AreaReloader plugin;
	public static boolean fastMode = Manager.getConfig().getBoolean("Settings.AreaLoading.FastMode");
	public static List<String> creations;
	public static boolean isAsyncCreation = false;
	
	public static void performSetup() {
		File areas = new File(AreaReloader.plugin.getDataFolder() + File.separator + "Areas");
		if (!areas.exists()) {
			try {
				areas.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (Manager.getConfig().getBoolean("Commands.Create.Asynchronously")) {
			creations = new ArrayList<String>();
			isAsyncCreation = true;
		}
	}

	public static void deleteArea(String area) {
		kill(area);
		Manager.getAreasConfig().set("Areas." + area, null);
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
		}
	}

	public static boolean isInteger(String s) {
		return isInteger(s, 10);
	}
	
	/**
	 * Formats time from milliseconds to: <b>days, hours, minutes, seconds</b>
	 * @param time must be in milliseconds
	 * @return a string with the formatted value
	 */
	public static String formatTime(final long time) {
		String result = new String();
		if (time < 0) {
			result = "-";
		}
		final long days = TimeUnit.MILLISECONDS.toDays(time);
		final long hours = TimeUnit.MILLISECONDS.toHours(time) % 24;
		final long minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
		final long seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
		if (days > 0) 
			result += String.valueOf(days) + " days ";
		if (hours > 0)
			result += String.valueOf(hours) + " hours ";
		if (minutes > 0) 
			result += String.valueOf(minutes) + " minutes ";
		if (seconds >= 0) {
			if (time > 0) {
				result += String.valueOf(seconds) + "." + (String.valueOf(time).length() > 2 ? String.valueOf(time).substring(0, 1) : String.valueOf(time)) + " seconds";
			} else {
				result += String.valueOf(seconds) + " seconds";
			}
		}
		return result;
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
		Manager.printDebug("-=-=-=-=-=-=-=-=-=-=- Area Building -=-=-=-=-=-=-=-=-=-=-");
		Manager.printDebug("File: " + file.getName());
		if (!file.exists()) {
			Manager.printDebug("Schematic file: Missing.");
			return false;
		}
		Manager.printDebug("Schematic File: Found.");
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		if (format == null) {
			Manager.printDebug("Schematic format: Missing.");
		} else {
			Manager.printDebug("Schematic format: Found.");
		}
		
		try {
			ClipboardReader reader = format.getReader(new FileInputStream(file));
			Clipboard clipboard = reader.read();
			Manager.printDebug("Reading Schematic.");

			try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
				Manager.printDebug("Building section.");
				Operation operation = new ClipboardHolder(clipboard)
						.createPaste(editSession)
						.to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
						.build();
				Operations.complete(operation);
				Manager.printDebug("Section has been built!");
			} catch (Exception e) {
				e.printStackTrace();
				Manager.printDebug("An error has occurred during the loading operations.");
				Manager.printDebug(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Manager.printDebug("An error has occurred while retriving the clipboard.");
			Manager.printDebug(e.getMessage());
		}
		return true;
	}

	public static boolean createNewArea(Player player, String area, int size, boolean copyEntities, boolean copyBiomes) throws WorldEditException {
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
		final World world = player.getWorld();
		BukkitPlayer lp = BukkitAdapter.adapt(player);
		LocalSession ls = WorldEdit.getInstance().getSessionManager().get(lp);
		Region sel = ls.getSelection(BukkitAdapter.adapt(world));
		int maxX = 0;
		int maxZ = 0;
		if (!(sel instanceof CuboidRegion)) {
			return false;
		}
			int curX = 0;
			BlockVector3 min = sel.getMinimumPoint();
			BlockVector3 max = sel.getMaximumPoint();
			Manager.getAreasConfig().set("Areas." + area + ".World", sel.getWorld().getName());
			Manager.getAreasConfig().set("Areas." + area + ".HasCopiedEntities", copyEntities);
			Manager.getAreasConfig().set("Areas." + area + ".HasCopiedBiomes", copyBiomes);
			Manager.getAreasConfig().set("Areas." + area + ".Minimum.X", min.getBlockX());
			Manager.getAreasConfig().set("Areas." + area + ".Minimum.Y", min.getBlockY());
			Manager.getAreasConfig().set("Areas." + area + ".Minimum.Z", min.getBlockZ());
			Manager.getAreasConfig().set("Areas." + area + ".Maximum.X", max.getBlockX());
			Manager.getAreasConfig().set("Areas." + area + ".Maximum.Y", max.getBlockY());
			Manager.getAreasConfig().set("Areas." + area + ".Maximum.Z", max.getBlockZ());
			Manager.areas.saveConfig();
			for (int x = min.getBlockX(); x <= max.getBlockX(); x += size) {
				int curZ = 0;
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z += size) {
					int maxY = max.getBlockY();
					Location pt1 = new Location(player.getWorld(), x, min.getBlockY(), z);
					Location pt2 = new Location(player.getWorld(), x + getMaxInt(x, max.getBlockX(), size).intValue(), maxY, z + getMaxInt(z, max.getBlockZ(), size).intValue());

					BlockVector3 bvmin = BukkitAdapter.asBlockVector(pt1);
					BlockVector3 bvmax = BukkitAdapter.asBlockVector(pt2);
					CuboidRegion region = new CuboidRegion(bvmin, bvmax);
					BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

					ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(BukkitAdapter.adapt(world), region, clipboard, region.getMinimumPoint());
					forwardExtentCopy.setCopyingEntities(copyEntities);
					forwardExtentCopy.setCopyingBiomes(copyBiomes);
					Operations.complete(forwardExtentCopy);
					
					Manager.printDebug("-=-=-=-=-=-=-=-=-=-=- Area Creation -=-=-=-=-=-=-=-=-=-=-");					
					try {
						Operations.complete(forwardExtentCopy);
						Manager.printDebug("Succesfully copied the selected clipboard to system.");
					} catch (Exception e) {
						e.printStackTrace();
						Manager.printDebug("An error has occurred when coping the selected clipboard!");
						Manager.printDebug(e.getMessage());
					}
					File file = new File(AreaReloader.plugin.getDataFolder() + File.separator + "Areas" + File.separator + area + File.separator + getFileName(area, curX, curZ) + ".schematic");
					if (file.exists()) {
						file.delete();
					}
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
						Manager.printDebug("Creating Areas' files directory.");
					}
					if (!file.exists()) {
						try {
							file.createNewFile();
							Manager.printDebug("Saving to file the selected clipboard.");
						} catch (IOException e) {
							e.printStackTrace();
							Manager.printDebug("An error has occurred when saving to clipboard area: " + file.getName());
							Manager.printDebug(e.getMessage());
						}
					}
					try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
						writer.write(clipboard);
						Manager.printDebug("The clipboard was succesfully saved to file.");
					} catch (IOException e) {
						e.printStackTrace();
						Manager.printDebug("An error has occurred when saving the clipboard to file for: " + file.getName());
						Manager.printDebug(e.getMessage());
					}
					Manager.printDebug("-=-=-=-=-=-=-=-=-=-=- -=- -=-=-=-=-=-=-=-=-=-=-");
					Manager.printDebug("");
					curZ++;
					maxZ = curZ;
				}
				curX++;
				maxX = curX;
			}
			maxX--;
			maxZ--;
			Manager.getAreasConfig().set("Areas." + area + ".Size.X", maxX);
			Manager.getAreasConfig().set("Areas." + area + ".Size.Z", maxZ);
			Manager.getAreasConfig().set("Areas." + area + ".Size.Chunk", (maxX * maxZ > 0 ? maxX * maxZ : 1));
			Manager.getAreasConfig().set("Areas." + area + ".Loading.Interval.Global", true);
			Manager.getAreasConfig().set("Areas." + area + ".Loading.Interval.Time", 200);
			Manager.getAreasConfig().set("Areas." + area + ".AutoReload.Enabled", false);
			Manager.getAreasConfig().set("Areas." + area + ".AutoReload.Time", 200000);
			Manager.areas.saveConfig();
			return true;
	}
	
	public static void kill(String area) {
		Manager.printDebug("-=-=-=-=-=-=-=-=-=-=- Area Killing -=-=-=-=-=-=-=-=-=-=-");
		Manager.printDebug("Area: " + area);
		if (AreaReloader.getQueue().isQueued(area)) {
			AreaReloader.getInstance().getServer().getScheduler().cancelTask(AreaReloader.getQueue().getTaskByName(area));
			AreaLoader.reset(area);
			AreaReloader.getQueue().remove(area, AreaReloader.getQueue().getTaskByName(area));
			Manager.printDebug("Killed area's execution.");
		}
		if (DisplayCommand.isDisplaying(area)) {
			DisplayCommand.remove(area, null);
		}
		Manager.printDebug("Removed from the loading instances.");
		Manager.printDebug("Removed from the automatic loading instances.");
		Manager.printDebug("-=-=-=-=-=-=-=-=-=-=- -=- -=-=-=-=-=-=-=-=-=-=-");
		Manager.printDebug("");
	}
	
	public static List<String> getAreas() {
		List<String> areas = new ArrayList<String>();
		if (Manager.getAreasConfig().contains("Areas")) {
			for (String keys : Manager.getAreasConfig().getConfigurationSection("Areas").getKeys(false)) {
				areas.add(keys);
			}
		}
		return areas;
	}
	
	public static boolean areaExist(final String area) {
		if (Manager.getAreasConfig().contains("Areas." + area)) {
			return true;
		}
		return false;
	}
	
	public static long getGlobalInterval() {
		return Manager.getConfig().getLong("Settings.AreaLoading.GlobalInterval");
	}
	
	public static boolean isGlobalInterval(String area) {
		return Manager.getAreasConfig().getBoolean("Areas." + area + ".Loading.Interval.Global");
	}
	
	public static long getInterval(String area) {
		if (!isGlobalInterval(area))
			return Manager.getAreasConfig().getLong("Areas." + area + ".Loading.Interval.Time");
		return getGlobalInterval();
	}
	
	public static String getAreaInWorld(String area) {
		return Manager.getAreasConfig().getString("Areas." + area + ".World");
	}
	
	public static World getWorld(String area) {
		return Bukkit.getWorld(getAreaInWorld(area));
	}
	
	public static String getFileName(String file, int x, int z) {
		return file + "_" + x + "_" + z;
	}

	public static Integer getAreaSizeX(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Size.X");
	}

	public static Integer getAreaSizeZ(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Size.Z");
	}
	
	public static Integer getAreaMaxX(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Maximum.X");
	}
	
	public static Integer getAreaMaxY(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Maximum.Y");
	}
	
	public static Integer getAreaMaxZ(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Maximum.Z");
	}
	
	public static Integer getAreaX(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Minimum.X");
	}
	
	public static Integer getAreaY(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Minimum.Y");
	}
	
	public static Integer getAreaZ(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Minimum.Z");
	}

	public static Integer getAreaChunk(String area) {
		return Manager.getAreasConfig().getInt("Areas." + area + ".Size.Chunk");
	}

	public static void reloadConfig() {
		AreaReloader.plugin.reloadConfig();
	}
	
	public static void sendMessage(CommandSender sender, String message, boolean prefix) {
		if (prefix) {
			sender.sendMessage(ARCommand.formatColors(getPrefix() + message));
		} else {
			sender.sendMessage(ARCommand.formatColors(message));
		}
	}
	
	public static String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', Manager.getConfig().getString("Settings.Language.ChatPrefix"));
	}
}
