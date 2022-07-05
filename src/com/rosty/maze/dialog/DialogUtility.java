package com.rosty.maze.dialog;

import java.io.IOException;

import com.rosty.maze.dialog.about.AboutDialog;
import com.rosty.maze.dialog.preferences.PreferencesDialog;

/**
 * Classe utilitaire permee de toutes les fenêtres secondaires du logiciel. A
 * chaque fenêtre est associée une méthode statique qui lance l'affichage du
 * composant à l'écran.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class DialogUtility {
	private static PreferencesDialog preferencesDialog = null;
	private static AboutDialog aboutDialog = null;

	public static void openPreferencesDialog() throws IOException {
		if (preferencesDialog == null) {
			preferencesDialog = new PreferencesDialog();
			preferencesDialog.showAndWait();
			// L'instruction suivante ne sera exécutée qu'à la fermeture de la fenêtre i.e.
			// lorsque la méthode "showAndWait" aura fini d'être exécutée.
			preferencesDialog = null;
		} else
			preferencesDialog.requestFocus();
	}

	/** Prépare et affiche la fenêtre "À propos" du logiciel. */
	public static void openAboutDialog() throws IOException {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog();
			aboutDialog.showAndWait();
			aboutDialog = null;
		} else
			aboutDialog.requestFocus();
	}
}
