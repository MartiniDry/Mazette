package com.rosty.maze.application;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.application.labels.PropertiesManager;
import com.rosty.maze.controller.MainWindowController;
import com.rosty.maze.view.box.MessageBox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Classe qui gère toute la procédure de lancement et de fermeture de
 * l'application.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class AppLauncher extends Application {
	private static final String ICON_PATH = "com/rosty/maze/application/icons/";
	private static final String CSS_PATH = "com/rosty/maze/view/css/";

	/** Conteneur de l'IHM principale de l'application. */
	private static Stage primaryStage;

	/** Fournit le conteneur de l'IHM principale de l'application. */
	public static final Stage getPrimaryStage() {
		return primaryStage;
	}

	/** Contrôleur principal de l'application. */
	private static MainWindowController mainController;

	/** Fournit le contrôleur principal de l'application. */
	public static final MainWindowController getMainController() {
		return mainController;
	}

	@Override
	public void init() throws Exception {
		Mazette.LOGGER.info("Initialization of the application");

		super.init();
		MessageBox.setStyleSheet(CSS_PATH + "message-box.css");
		PropertiesManager.insert("hmi.properties");
	}

	@Override
	public void start(Stage stage) {
		AppLauncher.primaryStage = stage;

		Mazette.LOGGER.info("Preparing 'MainWindow.fxml'...");
		FXMLLoader loader = new FXMLLoader(Mazette.class.getResource("view/MainWindow.fxml"),
				LocaleManager.getBundle());
		try {
			Mazette.LOGGER.info("Loading 'MainWindow.fxml' and its properties...");
			PropertiesManager.load(loader);
			primaryStage.setScene(new Scene(loader.load()));

			AppLauncher.mainController = (MainWindowController) loader.getController();

			primaryStage.setTitle(LocaleManager.getString("main.title"));
			primaryStage.getIcons().add(new Image(ICON_PATH + "logo_24x24.png"));
			primaryStage.getIcons().add(new Image(ICON_PATH + "logo_128x128.png"));

			primaryStage.setOnCloseRequest(event -> Mazette.shutDown());

			primaryStage.centerOnScreen();
			primaryStage.show();
		} catch (IOException | RuntimeException e) {
			Mazette.LOGGER.fatal(e.getMessage(), e);

			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.main.creation"));
			box.setContentText(e.getLocalizedMessage());
			box.showAndWait();
		}
	}

	@Override
	public void stop() {
		Mazette.shutDown();
	}

	/**
	 * Exécute un fichier-son via un <i>thread</i> asynchrone. Cette méthode
	 * n'exécute que les fichiers de ressource du logiciel ; ceux-ci sont présents
	 * dans le <i>package</i> <b>com.rosty.maze.view.sounds</b>.
	 * <p>
	 * <u>Exemple :</u><br/>
	 * 
	 * <pre>
	 * playSound("beep.wav");
	 * </pre>
	 * </p>
	 * 
	 * @param fileName Nom du fichier-son (avec son extension).
	 */
	public static synchronized void playSound(final String fileName) {
		new Thread(() -> {
			try {
				InputStream soundStream = Mazette.class.getResourceAsStream("view/sounds/" + fileName);
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundStream);

				Clip clip = AudioSystem.getClip();
				clip.open(inputStream);
				clip.start();
			} catch (Exception e) {
				Mazette.LOGGER.error(e.getMessage(), e);
				
				MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.main.creation"));
				box.setContentText(e.getLocalizedMessage());
				box.showAndWait();
			}
		}).start();
	}

	/**
	 * Recharge l'IHM principale de l'application. Cette action :
	 * <ul>
	 * <li>Définit la langue utilisée via le gestionnaire de locale (instance
	 * {@link LocaleManager})</li>
	 * <li>Charge l'ensemble des propriétés incluses dans le gestionnaire de
	 * propriétés (instance {@link PropertiesManager}).</li>
	 * </ul>
	 */
	public static void reloadView() {
		Mazette.LOGGER.info("Reloading the FXML view");
		try {
			FXMLLoader loader = new FXMLLoader(Mazette.class.getResource("view/MainWindow.fxml"),
					LocaleManager.getBundle());
			PropertiesManager.load(loader);

			primaryStage.setScene(new Scene(loader.load()));
			primaryStage.setTitle(LocaleManager.getString("main.title"));

			AppLauncher.mainController = (MainWindowController) loader.getController();
		} catch (IOException | RuntimeException e) {
			Mazette.LOGGER.fatal(e.getMessage(), e);
			
			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.main.creation"));
			box.setContentText(e.getLocalizedMessage());
			box.showAndWait();
		}
	}
}
