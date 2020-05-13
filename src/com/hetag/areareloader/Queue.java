package com.hetag.areareloader;

import java.util.HashMap;
import java.util.Map.Entry;

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
	 * Every time the server is reloaded, restarted, the
	 * queue gets cleared.
	 * <p>
	 * If {@link #useQueue} returns true
	 * @return QUEUE
	 */
	protected HashMap<String, Integer> queue() {
		if (QUEUE != null) return QUEUE;
		return null;
	}
	
	
	/**
	 * Checks if the specified area is already inside the queue.
	 * <p>
	 * This should never be used to check whether or not an area can be reloaded.
	 * @param area
	 * @return queued area
	 */
	public boolean isAreaQueued(String area) {
		if (queue().containsKey(area)) return true;
		return false;
	}
	
	/**
	 * Checks if the specified area is queued by also checking its count.
	 * <p>
	 * This method should be used when checking if an area is already being reloaded
	 * to prevent its over loading.
	 * <p>
	 * If the count > 1 -> run action
	 * 
	 * @param area
	 * @return queued area
	 */
	
	public boolean isQueued(String area) {
		for (Entry<String, Integer> IDs : queue().entrySet()) {
			if (IDs.getKey().equals(area) && IDs.getValue() > 1) {
				return true;
			}
		}
		return false;
	}
}
