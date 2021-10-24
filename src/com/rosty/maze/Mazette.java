package com.rosty.maze;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
	/** Journal du logiciel, utilisé pour l'écriture d'événements système. */
	public static final Logger LOGGER = Logger.getLogger(Mazette.class);

	/** Booléen indiquant si les données de débogage graphique sont affichées. */
	private static boolean fxDebug = false;

	/** Niveau d'écriture des événements dans le journal de <i>log</i>. */
	private static Level logLevel = Level.ALL;

	/**
	 * Méthode principale de l'application <b>Mazette</b> (anciennement <b>Maze
	 * Generator</b>).
	 * 
	 * @param args Paramètres de lancement du logiciel.
	 */
	public static void main(String... args) {
		createLogger();

		LOGGER.info("Playing beep.wav");
		AppLauncher.playSound("beep.wav");

		for (int id = 0, len = args.length; id < len; id++) {
			String[] kv = args[id].split("=");
			if (kv[0].equals("-loglevel")) {
				switch (kv[1].toLowerCase()) {
					case "off":
					case "fatal":
					case "error":
					case "warn":
					case "info":
					case "trace":
					case "debug":
					case "all":
						LOGGER.setLevel(Level.toLevel(kv[1].toUpperCase()));
						break;
					default:
						LOGGER.setLevel(Level.ALL);
						break;
				}

				LOGGER.info("Argument -loglevel: " + logLevel);
			} else if (kv[0].equals("-fxdebug")) {
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

				LOGGER.info("Argument -fxdebug: " + fxDebug);
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

	public static final Level arg_logLevel() {
		return LOGGER.getLevel();
	}

	public static void createLogger() {
		PropertyConfigurator.configure("res/log4j.properties");
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