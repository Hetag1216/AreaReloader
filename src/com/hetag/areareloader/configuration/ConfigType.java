package com.hetag.areareloader.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigType {
	private static final HashMap<String, ConfigType> ALL_TYPES = new HashMap<>();
	public static final ConfigType DEFAULT = new ConfigType("Default");
	public static final ConfigType[] CORE_TYPES = { DEFAULT };
	private final String string;

	public ConfigType(String string) {
		this.string = string;
		ALL_TYPES.put(string, this);
	}

	public static List<ConfigType> addonValues() {
		List<ConfigType> values = new ArrayList<>();
		for (String key : ALL_TYPES.keySet()) {
			if (!Arrays.asList(CORE_TYPES).contains(ALL_TYPES.get(key))) {
				values.add((ConfigType) ALL_TYPES.get(key));
			}
		}
		return values;
	}

	public static List<ConfigType> coreValues() {
		return Arrays.asList(CORE_TYPES);
	}

	public String toString() {
		return this.string;
	}

	public static List<ConfigType> values() {
		List<ConfigType> values = new ArrayList<>();
		for (String key : ALL_TYPES.keySet()) {
			values.add((ConfigType) ALL_TYPES.get(key));
		}
		return values;
	}
}
