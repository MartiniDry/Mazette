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
import com.rosty.maze.model.algorithm.generation.GrowingTreeAlgorithm;
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
import com.rosty.maze.model.algorithm.solving.WallFollowingAlgorithm;
import com.rosty.maze.view.box.MessageBox;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MenuBarController {
	private static final MainWindowController controller = AppLauncher.getMainController();

	@FXML
	private void saveDataAs() {
		Mazette.LOGGER.info("Sauvegarde des données");
	}

	@FXML
	private void exportData() {
		Mazette.LOGGER.info("Exportation des données");

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Enregistrer le labyrinthe sous...");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("Images JPEG", "*.jpg *.jpeg"),
				new ExtensionFilter("Images PNG", "*.png"), new ExtensionFilter("Images gif", "*.gif"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(1));

		try {
			File file = chooser.showSaveDialog(AppLauncher.getPrimaryStage().getOwner());
			controller.mazePanel.save(file);
		} catch (IOException | IllegalArgumentException e) {
			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.export"));
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
		controller.regenerate(new KruskalAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateShuffledKruskal() {
		controller.regenerate(new ShuffledKruskalAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateRecursiveBacktracker() {
		controller.regenerate(new RecursiveBacktrackingAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateRecursiveDivision() {
		controller.regenerate(new RecursiveDivisionAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generatePrim() {
		controller.regenerate(new PrimAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateHuntAndKill() {
		controller.regenerate(new HuntAndKillAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateAldousBroder() {
		controller.regenerate(new AldousBroderAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateWilson() {
		controller.regenerate(new WilsonAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateBinaryTree() {
		controller.regenerate(new BinaryTreeAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateSidewinder() {
		controller.regenerate(new SidewinderAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateEller() {
		controller.regenerate(new EllerAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generatePersonal() {
		controller.regenerate(new PersonalAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generatePersonal2() {
		controller.regenerate(new Personal2Algorithm(controller.mazePanel));
	}

	@FXML
	private void generateGrowingTree() {
		controller.regenerate(new GrowingTreeAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveWallFollowing() {
		controller.resetSolve(new WallFollowingAlgorithm(controller.mazePanel));
	}

	@FXML
	private void changeColors() {
		;
	}

	@FXML
	private void changeFullScreen() {
		AppLauncher.getPrimaryStage().setFullScreen(!AppLauncher.getPrimaryStage().isFullScreen());
	}

	@FXML
	private void preferences() {
		try {
			DialogUtility.openPreferencesDialog();
		} catch (IOException e) {
			Mazette.LOGGER.error(e.getMessage(), e);

			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.preferences"));
			box.setContentText(LocaleManager.getString("error.preferences.failed"));
			box.showAndWait();
		}
	}

	@FXML
	private void switchToFrench() {
		switchTo(Locale.FRENCH);
	}

	@FXML
	private void switchToEnglish() {
		switchTo(Locale.ENGLISH);
	}

	@FXML
	private void switchToGerman() {
		switchTo(Locale.GERMAN);
	}

	@FXML
	private void switchToSpanish() {
		switchTo(new Locale("es", "ES")); // Spanish is not a standard Java locale
	}

	@FXML
	private void about() {
		try {
			DialogUtility.openAboutDialog();
		} catch (IOException e) {
			Mazette.LOGGER.error(e.getMessage(), e);

			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.about"));
			box.setContentText(LocaleManager.getString("error.about.failed"));
			box.showAndWait();
		}
	}

	private void switchTo(Locale language) {
		LocaleManager.set(language);
		AppLauncher.reloadView();
		controller.displayAlgoName();
	}
}