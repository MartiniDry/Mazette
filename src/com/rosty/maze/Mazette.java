package com.rosty.maze;

import com.rosty.maze.application.AppLauncher;

import javafx.application.Application;
import javafx.application.Platform;

/**
 * Classe principale de l'application. Celle-ci traite les arguments passés en
 * entrée au lancement du logiciel.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class Mazette {
	/**
	 * Méthode principale de l'application <b>Mazette</b> (anciennement <b>Maze
	 * Generator</b>).
	 * 
	 * @param args Paramètres de lancement du logiciel.
	 */
	public static void main(String... args) {
		AppLauncher.playSound("beep.wav");
		Application.launch(AppLauncher.class, args);
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