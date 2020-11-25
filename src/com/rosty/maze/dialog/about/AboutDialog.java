package com.rosty.maze.dialog.about;

import java.io.IOException;

import com.rosty.maze.application.AppLauncher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Contrôleur de la fenêtre d'informations sur le logiciel (ou fenêtre "à
 * propos").
 * 
 * @author X2020636 (Martin ROSTAGNAT)
 */
public class AboutDialog extends Stage {
	/* ATTRIBUTS FXML */

	@FXML
	private Label appName;

	@FXML
	private Label appVersion, configVersion;

	@FXML
	private Label copyright;

	@FXML
	private Label description;

	@FXML
	private Label license;

	@FXML
	private Label developer;

	/* CONSTANTES */

	private static final String MIT_LICENSE = "Mazette est sous licence du MIT.\r\n"
			+ "Par la présente, la permission est accordée, à titre gratuit, à toute personne "
			+ "obtenant une copie du logiciel et des documents associés (le \"Logiciel\") de "
			+ "commercialiser le Logiciel sans restriction y compris, sans limitation, le droit "
			+ "d'utiliser, copier, fusionner, distribuer, autoriser une sous-licence et/ou vendre "
			+ "des copies du Logiciel, et d'autoriser les personnes auxquelles sont fournies le "
			+ "Logiciel de faire de même.";

	/** Constructeur de la classe {@link CommandSuiteDialog}. */
	public AboutDialog() throws IOException {
		// Initialisation de la fenêtre
		setTitle("À propos de l'application...");
		getIcons().addAll(new Image(getClass().getResource("icon.png").toExternalForm()));
		initOwner(AppLauncher.getPrimaryStage());
		setResizable(false);
		centerOnScreen();

		// Chargement de la page FXML et intégration à la fenêtre
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AboutDialog.fxml"));
		fxmlLoader.setController(this);
		setScene(fxmlLoader.load());
	}

	@FXML
	private void initialize() {
		appName.setText(""/* ConfigManager.getAppName() */);
		appVersion.setText("Version du logiciel : "/* + ConfigManager.getAppVersion() */);
		configVersion.setText("Configuration : "/* + ConfigManager.getConfigVersion() */);
		description.setText(""/* ConfigManager.getAppDescription() */);
		copyright.setText(""/* ConfigManager.getCopyright() */);

		license.setText(MIT_LICENSE);
	}
}