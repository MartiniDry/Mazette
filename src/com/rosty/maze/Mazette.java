package com.rosty.maze;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rosty.maze.application.AppLauncher;
import com.rosty.maze.application.AppLauncher.Dimensions;
import com.rosty.maze.application.AppLoader;

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
	/** Journal du logiciel, utilisé pour l'écriture des événements système. */
	public static final Logger LOGGER = Logger.getLogger(Mazette.class);

	/** Booléen indiquant si les données de débogage graphique sont affichées. */
	private static boolean fxDebug = false;

	/** Instance mémorisant les dimensions de l'IHM voulues par l'utilisateur. */
	public static final Dimensions dimensions = new Dimensions();

	/**
	 * Booléen indiquant si la fenêtre de chargement est affichée au lancement du
	 * logiciel.
	 */
	private static boolean noSplashScreen = false;

	/**
	 * Méthode principale de l'application <b>Mazette</b> (anciennement <b>Maze
	 * Generator</b>).
	 * 
	 * @param args Paramètres de lancement du logiciel.
	 */
	public static void main(String... args) {
		// En premier lieu, préparer le journal des événements du logiciel.
		PropertyConfigurator.configure("res/log4j.properties");

		LOGGER.info("╔═════════════════╗");
		LOGGER.info("║     MAZETTE     ║");
		LOGGER.info("╚═════════════════╝ ©MartiniDry");
		LOGGER.info(null);
		LOGGER.info(" BEGIN            |");
		LOGGER.info("──────────────────┘");
		LOGGER.info(null);

		LOGGER.info("Playing beep.wav");
		AppLauncher.playSound("beep.wav");

		LOGGER.info("Reading arguments:");
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

				LOGGER.info(" * loglevel: " + LOGGER.getLevel());
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

				LOGGER.info(" * fxdebug: " + fxDebug);
			} else if (kv[0].equals("-size")) {
				String sizeValue = kv[1];
				if (sizeValue.equals("full")) {
					dimensions.setFullScreen(true);
				} else if (sizeValue.equals("max")) {
					dimensions.setMaximized(true);
				} else if (sizeValue.equals("min")) {
					dimensions.setWidth(0D);
					dimensions.setHeight(0D);
				} else {
					String[] values = sizeValue.split("x");
					switch (values.length) {
						case 1:
							if (values[0].matches("^\\d+$")) {
								double dValue = Double.parseDouble(values[0]);
								dimensions.setWidth(dValue);
								dimensions.setHeight(dValue);
							} else if (values[0].matches("^\\d+%$")) {
								Double pValue = Double.parseDouble(values[0].replace("%", ""));
								dimensions.setPercHeight(pValue / 100D);
								dimensions.setPercWidth(pValue / 100D);
							} else
								LOGGER.error("Erreur de syntaxe !");

							break;
						case 2:
							if (values[0].matches("^\\d+$")) {
								double dValue = Double.parseDouble(values[0]);
								dimensions.setWidth(dValue);
							} else if (values[0].matches("^\\d+%$")) {
								Double pValue = Double.parseDouble(values[0].replace("%", ""));
								dimensions.setPercWidth(pValue / 100D);
							} else
								LOGGER.error("Erreur de syntaxe : " + values[0]);

							if (values[1].matches("^\\d+$")) {
								double dValue = Double.parseDouble(values[1]);
								dimensions.setWidth(dValue);
							} else if (values[1].matches("^\\d+%$")) {
								Double pValue = Double.parseDouble(values[1].replace("%", ""));
								dimensions.setPercWidth(pValue / 100D);
							} else
								LOGGER.error("Erreur de syntaxe : " + values[1]);

							break;
						default:
							LOGGER.warn("Seuls un ou deux paramètres de taille sont autorisés !");
							break;
					}
				}
			} else if (kv[0].equals("--nosplashscreen")) {
				noSplashScreen = true;
				LOGGER.info(" * noSplashScreen");
			}
		}

		if (!noSplashScreen)
			System.setProperty("javafx.preloader", AppLoader.class.getCanonicalName());

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
	 * Fournit le niveau de traçage des événements système dans le journal
	 * (<i>log</i>).
	 */
	public static final Level arg_logLevel() {
		return LOGGER.getLevel();
	}

	/**
	 * Ferme le logiciel. La méthode arrête d'abord le <i>thread</i> graphique pour
	 * effacer le contenu à l'écran, puis efface l'application en mémoire en fermant
	 * la JVM.
	 */
	public static void shutDown() {
		LOGGER.info(null);
		LOGGER.info(" END              |");
		LOGGER.info("──────────────────┘");
		LOGGER.info(null);
		LOGGER.info("Shutting down the JavaFX application...");
		Platform.exit();

		LOGGER.info("Shutting down the JVM...");
		System.exit(0);
	}
}