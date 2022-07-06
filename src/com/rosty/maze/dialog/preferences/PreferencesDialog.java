package com.rosty.maze.dialog.preferences;

import java.io.IOException;

import com.rosty.maze.application.AppLauncher;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.application.labels.PropertiesManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ColorPicker;
import javafx.stage.Stage;

public class PreferencesDialog extends Stage {
	/* ATTRIBUTS FXML */

	@FXML
	private ColorPicker wallColor, cellColor, borderColor, gridColor;

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

		wallColor.setValue(AppLauncher.getMainController().mazePanel.getWallColor());
		wallColor.valueProperty().addListener((bean_p, old_p, new_p) -> //
		AppLauncher.getMainController().mazePanel.setWallColor(new_p));

//		cellColor.setValue(AppLauncher.getMainController().mazePanel.getWallColor());
//		cellColor.valueProperty().addListener((bean_p, old_p, new_p) -> //
//		AppLauncher.getMainController().mazePanel.setWallColor(new_p));

		borderColor.setValue(AppLauncher.getMainController().mazePanel.getBorderColor());
		borderColor.valueProperty().addListener((bean_p, old_p, new_p) -> //
		AppLauncher.getMainController().mazePanel.setBorderColor(new_p));

		gridColor.setValue(AppLauncher.getMainController().mazePanel.getGridColor());
		gridColor.valueProperty().addListener((bean_p, old_p, new_p) -> //
		AppLauncher.getMainController().mazePanel.setGridColor(new_p));
	}
}