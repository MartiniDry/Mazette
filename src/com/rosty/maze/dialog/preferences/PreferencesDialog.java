package com.rosty.maze.dialog.preferences;

import java.io.IOException;

import com.rosty.maze.application.AppLauncher;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.application.labels.PropertiesManager;
import com.rosty.maze.widgets.MazePanel;
import com.rosty.util.javafx.FxUtils;

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

		MazePanel mainPanel = AppLauncher.getMainController().mazePanel;
		FxUtils.link(wallColor.valueProperty(), mainPanel::getWallColor, mainPanel::setWallColor);
//		FxUtils.link(cellColor.valueProperty(), mainPanel::getCellColor, mainPanel::setCellColor);
		FxUtils.link(borderColor.valueProperty(), mainPanel::getBorderColor, mainPanel::setBorderColor);
		FxUtils.link(gridColor.valueProperty(), mainPanel::getGridColor, mainPanel::setGridColor);
	}
}