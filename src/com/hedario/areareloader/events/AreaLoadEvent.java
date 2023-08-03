package com.hedario.areareloader.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AreaLoadEvent extends Event implements Cancellable {
	public static final HandlerList handlers = new HandlerList();
	private final String area;
	private final Player player;
	private boolean cancelled = false;

	public AreaLoadEvent(final String area) {
		this.area = area;
		this.player = null;
	}

	public AreaLoadEvent(final Player player, final String area) {
		this.player = player;
		this.area = area;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * @return the player loading the area
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * @param cancelled the cancelled to set
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}