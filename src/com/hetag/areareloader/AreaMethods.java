package com.hetag.areareloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		AreaReloader.areas.getConfig().set("Areas." + area, null);
		AreaReloader.areas.saveConfig();
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
			AreaReloader.isDeleted.add(area);
		}
	}

	public static boolean isInteger(String s) {
		return isInteger(s, 10);
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
			p.sendMessage(debugPrefix() + "Schematic file found!");
		}

		ClipboardFormat format = ClipboardFormats.findByFile(file);

		if (AreaReloader.debug) {
			if (p != null)
			p.sendMessage(debugPrefix() + "Schematic format found.");
		}

		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			Clipboard clipboard = reader.read();

			if (AreaReloader.debug) {
				if (p != null)
				p.sendMessage(debugPrefix() + "Reading schematic.");
			}

			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), Integer.MAX_VALUE)) {

				if (AreaReloader.debug) {
					if (p != null)
					p.sendMessage(debugPrefix() + "Initializing editSession.");
				}

				Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
						.to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
						.ignoreAirBlocks(false).build();

				if (AreaReloader.debug) {
					if (p != null)
					p.sendMessage(debugPrefix() + "Ran building operations.");
				}

				Operations.complete(operation);

				if (AreaReloader.debug) {
					if (p != null)
					p.sendMessage(debugPrefix() + "Operations succesfully completed!");
				}
			}
		}
		return true;
	}

	public static boolean createNewArea(Player player, String area, int size) throws WorldEditException {
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

			AreaReloader.areas.getConfig().set("Areas." + area + ".World", sel.getWorld().getName());
			AreaReloader.areas.getConfig().set("Areas." + area + ".X", min.getBlockX());
			AreaReloader.areas.getConfig().set("Areas." + area + ".Z", min.getBlockZ());
			AreaReloader.areas.getConfig().set("Areas." + area + ".Maximum.Z", max.getBlockZ());
			AreaReloader.areas.getConfig().set("Areas." + area + ".Maximum.X", max.getBlockX());
			AreaReloader.areas.saveConfig();
			for (int x = min.getBlockX(); x <= max.getBlockX(); x += size) {
				int curZ = 0;
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z += size) {
					EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(sel.getWorld(), Integer.MAX_VALUE);

					Location pt1 = new Location(player.getWorld(), x, 0, z);
					Location pt2 = new Location(player.getWorld(), x + getMaxInt(x, max.getBlockX(), size).intValue(), player.getWorld().getMaxHeight(), z + getMaxInt(z, max.getBlockZ(), size).intValue());

					BlockVector3 bvmin = BukkitAdapter.asBlockVector(pt1);
					BlockVector3 bvmax = BukkitAdapter.asBlockVector(pt2);
					CuboidRegion region = new CuboidRegion(sel.getWorld(), bvmin, bvmax);

					BlockArrayClipboard cc = new BlockArrayClipboard(region);
					ForwardExtentCopy clipCopy = new ForwardExtentCopy(es, region, cc, region.getMinimumPoint());
					clipCopy.setCopyingEntities(true);
					Operations.complete(clipCopy);
					if (AreaReloader.debug) {
						player.sendMessage(debugPrefix() + "Succesfully copied the selected clipboard to system.");
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
							player.sendMessage(debugPrefix() + "Clipboard succesfully saved to file.");
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
			
			AreaReloader.areas.getConfig().set("Areas." + area + ".Size.X", maxX);
			AreaReloader.areas.getConfig().set("Areas." + area + ".Size.Z", maxZ);
			AreaReloader.areas.getConfig().set("Areas." + area + ".Size.Chunk", size);
			AreaReloader.areas.getConfig().set("Areas." + area + ".AutoReload.Enabled", false);
			AreaReloader.areas.getConfig().set("Areas." + area + ".AutoReload.Time", 200000);
			AreaReloader.areas.saveConfig();
			return true;
		}
		return false;
	}
	
	public static String getXCoord(String area) {
		return AreaReloader.areas.getConfig().getString("Areas." + area + ".X");
	}
	
	public static String getZCoord(String area) {
		return AreaReloader.areas.getConfig().getString("Areas." + area + ".Z");
	}
	
	public static String getAreaInWorld(String area) {
		return AreaReloader.areas.getConfig().getString("Areas." + area + ".World");
	}
	
	public static String getFileName(String file, int x, int z) {
		return file + "_" + x + "_" + z;
	}

	public static Integer getAreaSizeX(String area) {
		return AreaReloader.areas.getConfig().getInt("Areas." + area + ".Size.X");
	}

	public static Integer getAreaSizeZ(String area) {
		return AreaReloader.areas.getConfig().getInt("Areas." + area + ".Size.Z");
	}
	
	public static Integer getAreaMaxX(String area) {
		return AreaReloader.areas.getConfig().getInt("Areas." + area + ".Maximum.X");
	}
	
	public static Integer getAreaMaxZ(String area) {
		return AreaReloader.areas.getConfig().getInt("Areas." + area + ".Maximum.Z");
	}
	
	public static Integer getAreaX(String area) {
		return AreaReloader.areas.getConfig().getInt("Areas." + area + ".X");
	}
	
	public static Integer getAreaZ(String area) {
		return AreaReloader.areas.getConfig().getInt("Areas." + area + ".Z");
	}

	public static Integer getAreaChunk(String area) {
		return AreaReloader.areas.getConfig().getInt("Areas." + area + ".Size.Chunk");
	}

	public static void reloadConfig() {
		AreaReloader.plugin.reloadConfig();
	}

	public static String debugPrefix() {
		return ChatColor.translateAlternateColorCodes('&', AreaReloader.plugin.getConfig().getString("Settings.Debug.Prefix"));
	}
}
