package com.rosty.maze.controller;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.AppLauncher;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.dialog.DialogUtility;
import com.rosty.maze.model.algorithm.generation.AldousBroderAlgorithm;
import com.rosty.maze.model.algorithm.generation.BinaryTreeAlgorithm;
import com.rosty.maze.model.algorithm.generation.EllerAlgorithm;
import com.rosty.maze.model.algorithm.generation.HuntAndKillAlgorithm;
import com.rosty.maze.model.algorithm.generation.KruskalAlgorithm;
import com.rosty.maze.model.algorithm.generation.Personal2Algorithm;
import com.rosty.maze.model.algorithm.generation.PersonalAlgorithm;
import com.rosty.maze.model.algorithm.generation.PrimAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveBacktrackingAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveDivisionAlgorithm;
import com.rosty.maze.model.algorithm.generation.ShuffledKruskalAlgorithm;
import com.rosty.maze.model.algorithm.generation.SidewinderAlgorithm;
import com.rosty.maze.model.algorithm.generation.WilsonAlgorithm;
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
	private void generateHuntAndKill() {
		AppLauncher.getMainController().regenerate(new HuntAndKillAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateAldousBroder() {
		AppLauncher.getMainController()
				.regenerate(new AldousBroderAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateWilson() {
		AppLauncher.getMainController().regenerate(new WilsonAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateBinaryTree() {
		AppLauncher.getMainController().regenerate(new BinaryTreeAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateSidewinder() {
		AppLauncher.getMainController().regenerate(new SidewinderAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generateEller() {
		AppLauncher.getMainController().regenerate(new EllerAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generatePersonal() {
		AppLauncher.getMainController().regenerate(new PersonalAlgorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void generatePersonal2() {
		AppLauncher.getMainController().regenerate(new Personal2Algorithm(AppLauncher.getMainController().mazePanel));
	}

	@FXML
	private void changeColors() {
		;
	}

	@FXML
	private void preferences() {
		;
	}

	@FXML
	private void switchToFrench() {
		LocaleManager.set(Locale.FRENCH);
		AppLauncher.reloadView();
	}

	@FXML
	private void switchToEnglish() {
		LocaleManager.set(Locale.ENGLISH);
		AppLauncher.reloadView();
	}

	@FXML
	private void about() {
		try {
			DialogUtility.openAboutDialog();
		} catch (IOException e) {
			MessageBox box = new MessageBox(AlertType.ERROR, "A propos du logiciel");
			box.setContentText("L'ouverture de la fenêtre a échoué ; veuillez recommencer.");
			box.showAndWait();
			e.printStackTrace();
		}
	}
}