package com.rosty.maze.controller;

import java.io.File;
import java.io.IOException;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.AppLauncher;
import com.rosty.maze.dialog.DialogUtility;
import com.rosty.maze.model.algorithm.generation.KruskalAlgorithm;
import com.rosty.maze.model.algorithm.generation.PersonalAlgorithm;
import com.rosty.maze.model.algorithm.generation.PrimAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveBacktrackingAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveDivisionAlgorithm;
import com.rosty.maze.model.algorithm.generation.ShuffledKruskalAlgorithm;
import com.rosty.maze.view.box.MessageBox;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MenuBarController {
	@FXML
	private void saveDataAs() {
		System.out.println("Sauvegarde des données");
	}

	@FXML
	private void exportData() {
		System.out.println("Exportation des données");

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Enregistrer le labyrinthe sous...");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("Images JPEG", "*.jpg *.jpeg"),
				new ExtensionFilter("Images PNG", "*.png"), new ExtensionFilter("Images gif", "*.gif"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(1));

		try {
			File file = chooser.showSaveDialog(AppLauncher.getPrimaryStage().getOwner());
			AppLauncher.getMainController().mazePanel.save(file);
		} catch (IOException e) {
			MessageBox box = new MessageBox(AlertType.ERROR, "Sauvegarde de l'image");
			box.setContentText(e.getLocalizedMessage());
			box.show();
		} catch (IllegalArgumentException e) {
			MessageBox box = new MessageBox(AlertType.ERROR, "Sauvegarde de l'image");
			box.setContentText(e.getLocalizedMessage());
			box.show();
		}
	}

	@FXML
	private void quitApplication() {
		Mazette.shutDown();
	}

	@FXML
	private void generateKruskal() {
		AppLauncher.getMainController().regenerate(new KruskalAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateShuffledKruskal() {
		AppLauncher.getMainController()
				.regenerate(new ShuffledKruskalAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateRecursiveBacktracker() {
		AppLauncher.getMainController()
				.regenerate(new RecursiveBacktrackingAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateRecursiveDivision() {
		AppLauncher.getMainController()
				.regenerate(new RecursiveDivisionAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generatePrim() {
		AppLauncher.getMainController().regenerate(new PrimAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generatePersonal() {
		AppLauncher.getMainController().regenerate(new PersonalAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void preferences() {
		;
	}

	@FXML
	private void about() {
		try {
			DialogUtility.openAboutDialog();
		} catch (IOException e) {
			MessageBox box = new MessageBox(AlertType.ERROR, "A propos du logiciel");
			box.setContentText("L'ouverture de la fenêtre a échoué ; veuillez recommencer.");
			box.showAndWait();
		}
	}
}