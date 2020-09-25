package com.rosty.maze.application;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.rosty.maze.controller.MainWindowController;
import com.rosty.maze.view.ResourceManager;
import com.rosty.maze.view.box.MessageBox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

	private static Stage primaryStage;

	public static final Stage getPrimaryStage() {
		return primaryStage;
	}

	private static MainWindowController mainController;

	public static final MainWindowController getMainController() {
		return mainController;
	}

	@Override
	public void init() throws Exception {
		super.init();
		MessageBox.setStyleSheet(CSS_PATH + "message-box.css");
	}

	@Override
	public void start(Stage stage) {
		AppLauncher.primaryStage = stage;

		FXMLLoader loader = new FXMLLoader(ResourceManager.class.getResource("MainWindow.fxml"));
		try {
			primaryStage.setScene(new Scene(loader.load()));

			AppLauncher.mainController = (MainWindowController) loader.getController();

			primaryStage.setTitle("Générateur de labyrinthes");
			primaryStage.getIcons().add(new Image(ICON_PATH + "logo_24x24.png"));
			primaryStage.getIcons().add(new Image(ICON_PATH + "logo_128x128.png"));

			primaryStage.centerOnScreen();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			primaryStage.show();
		}
	}

	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}

	/**
	 * Exécute un fichier-son via un <i>thread</i> asynchrone. Cette méthode
	 * n'exécute que les fichiers de ressource du logiciel ; ceux-ci sont présents
	 * dans le <i>package</i> <b>com.rosty.maze.view.sounds</b>.
	 * 
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
				AudioInputStream inputStream = AudioSystem
						.getAudioInputStream(ResourceManager.class.getResourceAsStream("sounds/" + fileName));

				Clip clip = AudioSystem.getClip();
				clip.open(inputStream);
				clip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
