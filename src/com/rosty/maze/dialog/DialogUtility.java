package com.rosty.maze.dialog;

import java.io.IOException;

import com.rosty.maze.dialog.about.AboutDialog;

/**
 * Classe utilitaire permee de toutes les fenêtres secondaires du logiciel. A
 * chaque fenêtre est associée une méthode statique qui lance l'affichage du
 * composant à l'écran.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class DialogUtility {
	private static AboutDialog aboutDialog = null;

	/** Prépare et affiche la fenêtre "À propos" du logiciel. */
	public static void openAboutDialog() throws IOException {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog();
			aboutDialog.showAndWait();
			// L'instruction suivante ne sera exécutée qu'à la fermeture de la fenêtre i.e.
			// lorsque la méthode "showAndWait" aura fini d'être exécutée.
			aboutDialog = null;
		} else
			aboutDialog.requestFocus();
	}
}
