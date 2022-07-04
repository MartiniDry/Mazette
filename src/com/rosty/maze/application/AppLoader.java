package com.rosty.maze.application;

import java.io.IOException;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.view.box.MessageBox;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Classe qui gère l'affichage de la fenêtre de chargement (avant l'apparition
 * du logiciel).
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class AppLoader extends Preloader {
	private static final String ICON_PATH = "com/rosty/maze/application/icons/";

	/** Conteneur de la fenêtre de chargement. */
	private static Stage primaryStage;

	@Override
	public void start(Stage stage) throws Exception {
		AppLoader.primaryStage = stage;

		Mazette.LOGGER.info("Preparing 'SplashScreen.fxml'...");
		FXMLLoader loader = new FXMLLoader(Mazette.class.getResource("view/SplashScreen.fxml"),
				LocaleManager.getBundle());
		try {
			Scene sc = new Scene(loader.load());
			sc.setFill(Color.TRANSPARENT); // Commande importante pour faire disparaître le fond blanc de l'image
			stage.setScene(sc);

			stage.getIcons().add(new Image(ICON_PATH + "logo_24x24.png"));
			stage.getIcons().add(new Image(ICON_PATH + "logo_128x128.png"));

			stage.initStyle(StageStyle.TRANSPARENT); // Fait disparaître la bordure de la fenêtre
			stage.show();

			center();
		} catch (IOException | RuntimeException e) {
			Mazette.LOGGER.fatal(e.getMessage(), e);

			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.main.creation"));
			box.setContentText(e.getLocalizedMessage());
			box.showAndWait();
		}
	}

	/** Place la fenêtre de chargement au centre de la fenêtre principale du PC. */
	private void center() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
		primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
		if (stateChangeNotification.getType() == Type.BEFORE_START)
			primaryStage.hide();
	}
}