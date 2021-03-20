package com.rosty.maze.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;

public class ResourceManager {
	private static final String BUNDLE_LOCATION = "com.rosty.maze.view.labels.labels";
	private static final Path PROPERTIES_LOCATION = Paths.get("res").toAbsolutePath();

	private static ResourceBundle languageBundle;
	private static ArrayList<Properties> propertiesPack = new ArrayList<>();

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

	public static String getLanguageString(String key) {
		return languageBundle.getString(key);
	}
	
	public static void addProperties(String fileName) throws IOException {
		Path filePath = Paths.get(PROPERTIES_LOCATION.toString(), fileName);
		
		Properties propFile = new Properties();
		propFile.load(Files.newBufferedReader(filePath));
		propertiesPack.add(propFile);
	}
	
	public static void loadProperties(FXMLLoader loader) throws IOException {
		for (Properties prop : propertiesPack)
			for (String key : prop.stringPropertyNames())
				loader.getNamespace().put(key, prop.getProperty(key));
	}
	
	public static String getPropertiesString(String key) {
		for (Properties prop : propertiesPack) {
			String value = prop.getProperty(key);
			if (value != null)
				return value;
		}
		
		return null;
	}
}