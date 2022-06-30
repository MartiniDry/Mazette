package com.rosty.maze.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.config.ConfigManager;
import com.rosty.util.xml.checksum.ChecksumException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Contrôleur de la fenêtre de chargement de l'application.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class SplashScreenController {
	@FXML
	private Label title, version, copyright;

	@FXML
	public void initialize() {
		try {
			title.setText(ConfigManager.getInstance().getAppName().toUpperCase());
			version.setText("v " + ConfigManager.getInstance().getAppVersion());
			copyright.setText(ConfigManager.getInstance().getCopyright());
		} catch (ParserConfigurationException | SAXException | IOException | ChecksumException e) {
			Mazette.LOGGER.error(e.getMessage(), e);
		}
	}
}