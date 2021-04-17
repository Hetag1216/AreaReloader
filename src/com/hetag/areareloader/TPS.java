package com.hetag.areareloader;

@Deprecated
/**
 * The TPS checking function will be removed in a future update and will
 * completely be replaced by interval and blocks checks for areas' loads.
 * <p>
 * v1.9.1
 * 
 * @author dario
 *
 */
public class TPS implements Runnable {
	private static double tps;
	long sec;
	long currentSec;
	int ticks;
	int delay;

	@Override
	public void run() {
		sec = (System.currentTimeMillis() / 1000);

		if (currentSec == sec) {
			ticks++;
		} else {
			currentSec = sec;
			tps = (tps == 0 ? ticks : ((tps + ticks) / 2));
			ticks = 0;
			tps = tps + 1;
			if (tps >= 21.0D) {
				tps = 20.0D;
			}
		}
	}

	public static double getTPS() {
		return Math.round(tps);
	}
}
