package com.hetag.areareloader;

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
