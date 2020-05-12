package com.hetag.areareloader;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class Queue {
	private HashMap<String, Integer> QUEUE;
	AreaReloader plugin;
	public Queue(AreaReloader plugin) {
		this.plugin = plugin;
		QUEUE = new HashMap<>();
	}
	
	/**
	 * Stores all areas queued for reloading.
	 * <p>
	 * This does not store instances but the areas themselves.
	 * <p>
	 * Every time the server is reloaded, restarted or the /ar command is ran, the
	 * queue gets cleared.
	 * <p>
	 * If {@link #useQueue} returns true
	 * @return QUEUE
	 */
	protected HashMap<String, Integer> queue() {
		if (QUEUE != null) return QUEUE;
		return null;
	}
	
	protected boolean isQueued(String area) {
		for (Entry<String, Integer> IDs : queue().entrySet()) {
			if (IDs.getKey().equals(area) && IDs.getValue() > 1) {
				Bukkit.getServer().broadcastMessage(area + " is already being reloaded.");
				return true;
			}
		}
		return false;
	}
}
