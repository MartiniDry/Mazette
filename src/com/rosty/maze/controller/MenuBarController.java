package com.rosty.maze.controller;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.AppLauncher;
import com.rosty.maze.model.algorithm.generation.KruskalAlgorithm;
import com.rosty.maze.model.algorithm.generation.PersonalAlgorithm;
import com.rosty.maze.model.algorithm.generation.PrimAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveBacktrackingAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveDivisionAlgorithm;
import com.rosty.maze.model.algorithm.generation.ShuffledKruskalAlgorithm;

import javafx.fxml.FXML;

public class MenuBarController {
	@FXML
	private void saveDataAs() {
		System.out.println("Sauvegarde des données");
	}

	@FXML
	private void exportData() {
		System.out.println("Exportation des données");
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
}