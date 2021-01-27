package com.rosty.maze.view;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceManager {
	private static final String BUNDLE_LOCATION = "com.rosty.maze.view.labels.labels";

	private static ResourceBundle languageBundle;

	static {
		languageBundle = ResourceBundle.getBundle(BUNDLE_LOCATION, Locale.getDefault());
	}

	public static void setLanguage(Locale locale) {
		languageBundle = ResourceBundle.getBundle(BUNDLE_LOCATION, locale);
	}

	public static Locale getLanguage() {
		return languageBundle.getLocale();
	}

	public static ResourceBundle getBundle() {
		return languageBundle;
	}

	public static String getString(String key) {
		return languageBundle.getString(key);
	}
}