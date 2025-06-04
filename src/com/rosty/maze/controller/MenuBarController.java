package com.rosty.maze.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.AppLauncher;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.dialog.DialogUtility;
import com.rosty.maze.model.ApplicationModel;
import com.rosty.maze.model.Maze;
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
import com.rosty.maze.model.algorithm.solving.AStarAlgorithm;
import com.rosty.maze.model.algorithm.solving.DeadEndFillingAlgorithm;
import com.rosty.maze.model.algorithm.solving.DijkstraAlgorithm;
import com.rosty.maze.model.algorithm.solving.LeeAlgorithm;
import com.rosty.maze.model.algorithm.solving.PledgeAlgorithm;
import com.rosty.maze.model.algorithm.solving.PrimJarnikAlgorithm;
import com.rosty.maze.model.algorithm.solving.RandomMouseAlgorithm;
import com.rosty.maze.model.algorithm.solving.SoukupAlgorithm;
import com.rosty.maze.model.algorithm.solving.SuiviDeadEndFillingAlgorithm;
import com.rosty.maze.model.algorithm.solving.TremeauxAlgorithm;
import com.rosty.maze.model.algorithm.solving.WallFollowingAlgorithm;
import com.rosty.maze.view.box.MessageBox;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Contrôleur de la barre de menu de l'application. Il contient notamment
 * l'ensemble des actionneurs des algorithmes.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class MenuBarController {
	@FXML
	private void openData() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(LocaleManager.getString("open.title"));
		chooser.getExtensionFilters().add(new ExtensionFilter(LocaleManager.getString("open.maz"), "*.maz"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));

		File file = chooser.showOpenDialog(AppLauncher.getPrimaryStage().getOwner());
		if (file != null) {
			Mazette.LOGGER.info("Chargement du labyrinthe depuis le fichier : " + file.getPath());

			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				Maze inputMaze = (Maze) ois.readObject();
				AppLauncher.getMainController().mazePanel.setMaze(inputMaze);
			} catch (IOException | ClassNotFoundException e) {
				MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.open"));
				box.setContentText(e.getLocalizedMessage());
				box.show();
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void saveDataAs() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(LocaleManager.getString("save.title"));
		chooser.getExtensionFilters().add(new ExtensionFilter(LocaleManager.getString("save.maz"), "*.maz"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));

		File file = chooser.showSaveDialog(AppLauncher.getPrimaryStage().getOwner());
		if (file != null) {
			Mazette.LOGGER.info("Sauvegarde du labyrinthe dans le fichier : " + file.getPath());

			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
				Maze outputMaze = ApplicationModel.getInstance().getMaze();
				oos.writeObject(outputMaze);
			} catch (IOException e) {
				MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.save"));
				box.setContentText(e.getLocalizedMessage());
				box.show();
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void exportData() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(LocaleManager.getString("export.title"));
		chooser.getExtensionFilters().addAll( //
				new ExtensionFilter(LocaleManager.getString("export.jpg"), "*.jpg *.jpeg"),
				new ExtensionFilter(LocaleManager.getString("export.png"), "*.png"),
				new ExtensionFilter(LocaleManager.getString("export.gif"), "*.gif"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(1));

		File file = chooser.showSaveDialog(AppLauncher.getPrimaryStage().getOwner());
		if (file != null) {
			Mazette.LOGGER.info("Exportation du labyrinthe dans le fichier : " + file.getPath());

			try {
				AppLauncher.getMainController().mazePanel.saveAsImage(file);
			} catch (IOException | IllegalArgumentException e) {
				MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.export"));
				box.setContentText(e.getLocalizedMessage());
				box.show();
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void quitApplication() {
		Mazette.shutDown();
	}

	@FXML
	private void generateKruskal() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new KruskalAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateShuffledKruskal() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new ShuffledKruskalAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateRecursiveBacktracker() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new RecursiveBacktrackingAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateRecursiveDivision() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new RecursiveDivisionAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generatePrim() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new PrimAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateHuntAndKill() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new HuntAndKillAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateAldousBroder() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new AldousBroderAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateWilson() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new WilsonAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateBinaryTree() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new BinaryTreeAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateSidewinder() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new SidewinderAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generateEller() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new EllerAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generatePersonal() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new PersonalAlgorithm(controller.mazePanel));
	}

	@FXML
	private void generatePersonal2() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new Personal2Algorithm(controller.mazePanel));
	}

	@FXML
	private void generateGrowingTree() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.regenerate(new GrowingTreeAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveWallFollowing() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new WallFollowingAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveAStar() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new AStarAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveDijkstra() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new DijkstraAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solvePrimJarnik() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new PrimJarnikAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveLee() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new LeeAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solvePledge() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new PledgeAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveTremeaux() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new TremeauxAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveRandomMouse() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new RandomMouseAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveDeadEndFillingByScan() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new DeadEndFillingAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveDeadEndFillingBySuivi() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new SuiviDeadEndFillingAlgorithm(controller.mazePanel));
	}

	@FXML
	private void solveSoukup() {
		MainWindowController controller = AppLauncher.getMainController();
		controller.resetSolve(new SoukupAlgorithm(controller.mazePanel));
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
		AppLauncher.getMainController().displayAlgoName();
	}
}