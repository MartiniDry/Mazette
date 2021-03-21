package com.rosty.maze.application.labels;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe de gestion de la locale pour le logiciel. Cette classe permet au
 * logiciel d'afficher du texte multilingue en fonction du paramètre choisi mais
 * également de définir des styles et des formats propres aux différents
 * langages, par exemple la lecture des caractères chinois de haut en bas. Ces
 * données sont catégorisées dans un groupe de fichiers nommés un <i>bundle</i>
 * ; dans notre logiciel, les fichiers ont pour préfixe
 * {@value #LABELS_FILE_NAME}.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class LocaleManager {
	/** Chemin vers le répertoire du <i>bundle</i>. */
	static final String BUNDLE_LOCATION = "com.rosty.maze.application.labels";

	/** Préfixe des fichiers du <i>bundle</i>. */
	private static final String LABELS_FILE_NAME = "labels";

	/**
	 * Conteneur de données du <i>bundle</i> pour une langue donnée.
	 */
	private static ResourceBundle localeBundle = ResourceBundle.getBundle(BUNDLE_LOCATION + "." + LABELS_FILE_NAME,
			Locale.getDefault());

	/**
	 * Définit la locale de l'application ; cette méthode permet de charger les
	 * données du <i>bundle</i> pour la langue choisie.
	 */
	public static void set(Locale locale) {
		localeBundle = ResourceBundle.getBundle(BUNDLE_LOCATION + "." + LABELS_FILE_NAME, locale);
	}

	/** Fournit la locale de l'application. */
	public static Locale get() {
		return localeBundle.getLocale();
	}

	/** Fournit le conteneur de données du <i>bundle</i>. */
	public static ResourceBundle getBundle() {
		return localeBundle;
	}

	/** Fournit la valeur du <i>bundle</i> indiquée par la clé. */
	public static String getString(String key) {
		return localeBundle.getString(key);
	}
}