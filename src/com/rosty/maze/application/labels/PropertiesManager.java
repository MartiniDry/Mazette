package com.rosty.maze.application.labels;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javafx.fxml.FXMLLoader;

public class PropertiesManager {
	private static final Path PROPERTIES_LOCATION = Paths.get("res").toAbsolutePath();

	private static final Map<String, Properties> propertiesPack = new HashMap<>();

	public static void insert(String fileName) throws IOException {
		Path filePath = Paths.get(PROPERTIES_LOCATION.toString(), fileName);

		Properties propFile = new Properties();
		propFile.load(Files.newBufferedReader(filePath));
		propertiesPack.put(fileName, propFile);
	}

	public static void delete(String fileName) throws IOException {
		propertiesPack.remove(fileName);
	}

	public static void load(FXMLLoader fxmlLoader) throws IOException {
		fxmlLoader.getNamespace().clear();
		for (Properties prop : propertiesPack.values())
			for (String key : prop.stringPropertyNames())
				fxmlLoader.getNamespace().put(key, prop.getProperty(key));
	}

	public static String getString(String key) {
		for (Properties prop : propertiesPack.values()) {
			String value = prop.getProperty(key);
			if (value != null)
				return value;
		}

		return null;
	}
}