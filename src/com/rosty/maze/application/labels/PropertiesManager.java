package com.rosty.maze.application.labels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;

/**
 * Gestionnaire des propriétés du logiciel. Cette classe permet d'enregistrer
 * des instances {@link Properties} provenant de différents fichiers de
 * ressource (extension <b>.xml</b> ou <b>.properties</b>) et de les associer au
 * logiciel :
 * <ul>
 * <li><u>Dans une classe Java :</u> une propriété peut être appelée depuis un
 * fichier spécifique (ex :
 * <code>getString("hmi.properties","main_color")</code>) ou depuis l'ensemble
 * du gestionnaire (ex : <code>getString("main_color")</code>. De la même
 * manière, une propriété peut être définie dans un fichier spécifique ou plus
 * globalement si la propriété existe.</li>
 * <li><u>Dans une page JavaFX :</u> les données du gestionnaire peuvent être
 * chargées dans une IHM via la fonction {@link #load(FXMLLoader)}. On appelle
 * ensuite la valeur que l'on souhaite grâce au <i>binding</i> (ex :
 * <code>&lt;Label text="${app_title}"/&gt;</code>).</li>
 * </ul>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class PropertiesManager {
	/** Chemin vers les fichiers de propriétés. */
	private static final Path PROPERTIES_LOCATION = Paths.get("res").toAbsolutePath();

	/** Conteneur de tous les fichiers de propriétés importés. */
	private static final Map<String, Properties> propertiesPack = new HashMap<>();

	/**
	 * Incorpore les données d'un fichier de propriétés. Tout le contenu sera ajouté
	 * aux autres propriétés lors de la prochaine mise à jour de l'IHM de
	 * l'application.
	 * 
	 * @param fileName Nom du fichier de propriétés.
	 * @throws IOException En cas de problème lors de la lecture du fichier.
	 */
	public static void insert(String fileName) throws IOException {
		Path filePath = Paths.get(PROPERTIES_LOCATION.toString(), fileName);

		Properties propFile = new Properties();
		propFile.load(Files.newBufferedReader(filePath));
		propertiesPack.put(fileName, propFile);
	}

	/**
	 * Retire les données liées à un fichier de propriétés.
	 * 
	 * @param fileName Nom du fichier de propriétés.
	 */
	public static void delete(String fileName) {
		propertiesPack.remove(fileName);
	}

	/**
	 * Charge toutes les propriétés du gestionnaire dans une IHM spécifique
	 * (instance {@link FXMLLoader}).
	 * 
	 * @param fxmlLoader Outil de chargement de l'IHM.
	 */
	public static void load(FXMLLoader fxmlLoader) {
		fxmlLoader.getNamespace().clear();
		for (Properties prop : propertiesPack.values())
			for (String key : prop.stringPropertyNames())
				fxmlLoader.getNamespace().put(key, prop.getProperty(key));
	}

	/**
	 * Fournit la valeur d'une propriété dans un fichier spécifique du gestionnaire.
	 * 
	 * @param fileName Nom du fichier à analyser.
	 * @param key      Nom de la propriété.
	 * @return Valeur de la propriété si celle-ci est présente, <code>null</code>
	 *         sinon.
	 */
	public static String getString(String fileName, String key) {
		if (propertiesPack.containsKey(fileName))
			return propertiesPack.get(fileName).getProperty(key);
		else
			return null;
	}

	/**
	 * Fournit la valeur d'une propriété si celle-ci est présente dans le
	 * gestionnaire.
	 * 
	 * @param key Nom de la propriété.
	 * @return Valeur de la propriété si celle-ci est présente, <code>null</code>
	 *         sinon.
	 */
	public static String getString(String key) {
		for (Properties prop : propertiesPack.values()) {
			String value = prop.getProperty(key);
			if (value != null)
				return value;
		}

		return null;
	}

	/**
	 * Définit la valeur d'une propriété dans un fichier spécifique du gestionnaire.
	 * Cette méthode est inclusive ; si la propriété n'est pas présente dans le
	 * fichier, elle y sera incorporée.
	 * 
	 * @param fileName Nom du fichier à analyser.
	 * @param key      Nom de la propriété.
	 * @param value    Valeur de la propriété.
	 * @return Booléen indiquant si le fichier est présent dans le gestionnaire.
	 */
	public static boolean setString(String fileName, String key, String value) {
		if (propertiesPack.containsKey(fileName)) {
			propertiesPack.get(fileName).setProperty(key, value);
			return true;
		} else
			return false;
	}

	/**
	 * Définit la valeur d'une propriété du gestionnaire. La méthode recherche le
	 * premier fichier possédant cette propriété puis redéfinit sa valeur. Cette
	 * méthode est non-inclusive ; si la propriété n'est présente dans aucun
	 * fichier, alors elle ne sera pas créée.
	 * 
	 * @param key   Nom de la propriété.
	 * @param value Valeur de la propriété.
	 * @return Booléen indiquant si la propriété est présente quelque part dans le
	 *         gestionnaire.
	 */
	public static boolean setString(String key, String value) {
		for (Properties prop : propertiesPack.values()) {
			String existingValue = prop.getProperty(key);
			if (existingValue != null) {
				prop.setProperty(key, value);
				return true;
			}
		}

		return false;
	}

	/**
	 * Sauvegarde les fichiers de propriété possédant une clé spécifique, afin de
	 * mettre à jour la propriété.
	 * 
	 * @param key Clé de la propriété.
	 */
	public static void save(String key) {
		for (Entry<String, Properties> item : propertiesPack.entrySet()) {
			File file = new File(PROPERTIES_LOCATION.toString(), item.getKey());
			if (getString(item.getKey(), key) != null)
				try (FileOutputStream fos = new FileOutputStream(file)) {
					Properties prop = item.getValue();
					prop.store(fos, null);
				} catch (IOException e) {
					// Ne rien faire
				}
		}
	}

	/**
	 * Établit une liaison de données entre une propriété FXML <b>double</b> et une
	 * propriété Java (via une clé spécifique dans les fichiers ".properties").
	 * 
	 * @param key      Clé de la propriété Java.
	 * @param property Propriété FXML (instance {@link DoubleProperty}).
	 */
	public static void link(String key, DoubleProperty property) {
		String value = getString(key);
		if (value != null)
			property.set(Double.parseDouble(value));

		property.addListener((bean_p, old_p, new_p) -> {
			if (new_p != null && !new_p.equals(old_p)) {
				setString(key, property.getValue().toString());
				save(key);
			}
		});
	}

	/**
	 * Établit une liaison de données entre une propriété FXML <b>float</b> et une
	 * propriété Java (via une clé spécifique dans les fichiers ".properties").
	 * 
	 * @param key      Clé de la propriété Java.
	 * @param property Propriété FXML (instance {@link FloatProperty}).
	 */
	public static void link(String key, FloatProperty property) {
		String value = getString(key);
		if (value != null)
			property.set(Float.parseFloat(value));

		property.addListener((bean_p, old_p, new_p) -> {
			if (new_p != null && !new_p.equals(old_p)) {
				setString(key, property.getValue().toString());
				save(key);
			}
		});
	}

	/**
	 * Établit une liaison de données entre une propriété FXML <b>int</b> et une
	 * propriété Java (via une clé spécifique dans les fichiers ".properties").
	 * 
	 * @param key      Clé de la propriété Java.
	 * @param property Propriété FXML (instance {@link IntegerProperty}).
	 */
	public static void link(String key, IntegerProperty property) {
		String value = getString(key);
		if (value != null)
			property.set(Integer.parseInt(value));

		property.addListener((bean_p, old_p, new_p) -> {
			if (new_p != null && !new_p.equals(old_p)) {
				setString(key, property.getValue().toString());
				save(key);
			}
		});
	}

	/**
	 * Établit une liaison de données entre une propriété FXML <b>long</b> et une
	 * propriété Java (via une clé spécifique dans les fichiers ".properties").
	 * 
	 * @param key      Clé de la propriété Java.
	 * @param property Propriété FXML (instance {@link LongProperty}).
	 */
	public static void link(String key, LongProperty property) {
		String value = getString(key);
		if (value != null)
			property.set(Long.parseLong(value));

		property.addListener((bean_p, old_p, new_p) -> {
			if (new_p != null && !new_p.equals(old_p)) {
				setString(key, property.getValue().toString());
				save(key);
			}
		});
	}

	/**
	 * Établit une liaison de données entre une propriété de couleur FXML et une
	 * propriété Java (via une clé spécifique dans les fichiers ".properties").
	 * 
	 * @param key      Clé de la propriété Java.
	 * @param property Propriété FXML {@link Color}.
	 */
	public static void link(String key, ObjectProperty<Color> property) {
		String value = getString(key);
		if (value != null)
			property.set(Color.web(value));

		property.addListener((bean_p, old_p, new_p) -> {
			if (new_p != null && !new_p.equals(old_p)) {
				setString(key, property.get().toString());
				save(key);
			}
		});
	}
}