package com.rosty.maze.dialog.preferences;

import java.io.IOException;

import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.application.labels.PropertiesManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class PreferencesDialog extends Stage {
	public PreferencesDialog() throws IOException {
		// Chargement de la page FXML et intégration à la fenêtre
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PreferencesDialog.fxml"),
				LocaleManager.getBundle());
		fxmlLoader.setController(this);
		PropertiesManager.load(fxmlLoader);
		setScene(fxmlLoader.load());
	}

	@FXML
	public void initialize() {
		setTitle(LocaleManager.getString("preferences.title"));
	}
}