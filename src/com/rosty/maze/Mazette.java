package com.rosty.maze;

import com.rosty.maze.application.AppLauncher;

import javafx.application.Application;
import javafx.application.Platform;

/**
 * Classe principale de l'application. Celle-ci traite les arguments passés en
 * entrée au lancement du logiciel.
 * 
 * @author Martin Rostagnat
 * @version 1.1
 */
public class Mazette {
	/**
	 * Booléen indiquant si les données de débogage graphique sont affichées sur
	 * l'IHM.
	 */
	private static boolean fxDebug = false;

	/**
	 * Méthode principale de l'application <b>Mazette</b> (anciennement <b>Maze
	 * Generator</b>).
	 * 
	 * @param args Paramètres de lancement du logiciel.
	 */
	public static void main(String... args) {
		AppLauncher.playSound("beep.wav");

		for (int id = 0, len = args.length; id < len; id++) {
			String[] kv = args[id].split("=");
			if (kv[0].equals("-fxdebug")) {
				switch (kv[1]) {
				case "0":
				case "false":
					fxDebug = false;
					break;
				case "1":
				case "true":
					fxDebug = true;
					break;
				default:
					break;
				}
			}
		}

		Application.launch(AppLauncher.class, args);
	}

	/**
	 * Fournit le paramètre de débogage <b>fxDebug</b>. Celui-ci indique si les
	 * informations de débogage sont affichés sur l'IHM de l'application.
	 * 
	 * @return Booléen (<code>false</code> par défaut).
	 */
	public static final boolean arg_fxDebug() {
		return fxDebug;
	}

	/**
	 * Ferme le logiciel. La méthode arrête d'abord le <i>thread</i> graphique pour
	 * effacer le contenu à l'écran, puis efface l'application en mémoire en fermant
	 * la JVM.
	 */
	public static void shutDown() {
		Platform.exit();
		System.exit(0);
	}
}