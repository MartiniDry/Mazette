package com.rosty.maze.controller;

import java.time.Duration;
import java.util.Observable;
import java.util.Observer;

import com.rosty.maze.model.ApplicationModel;
import com.rosty.maze.model.algorithm.Algorithm;
import com.rosty.maze.model.algorithm.AlgorithmRunner;
import com.rosty.maze.model.algorithm.AlgorithmRunner.ObsRunnerState;
import com.rosty.maze.model.algorithm.generation.KruskalAlgorithm;
import com.rosty.maze.model.algorithm.generation.PersonalAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveBacktrackingAlgorithm;
import com.rosty.maze.model.algorithm.generation.RecursiveDivisionAlgorithm;
import com.rosty.maze.model.algorithm.generation.ShuffledKruskalAlgorithm;
import com.rosty.maze.view.box.MessageBox;
import com.rosty.maze.widgets.GIntegerField;
import com.rosty.maze.widgets.GLongField;
import com.rosty.maze.widgets.MazePanel;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * Contrôleur de la fenêtre principale de l'application.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class MainWindowController implements Observer {
	/* ATTRIBUTS FXML */

	@FXML
	private GIntegerField newMazeRows, newMazeColumns;

	@FXML
	private CheckBox stepOrRun;

	@FXML
	private GLongField delta;

	@FXML
	private VBox generationButtons;
	@FXML
	private ToggleGroup group;

	@FXML
	private TextField test;

	@FXML
	private ToggleButton runButton;
	@FXML
	private Button stepButton;
	@FXML
	private Label elapsedTime;

	@FXML
	public MazePanel mazePanel;

	/* ATTRIBUTS */

	// Générateur de labyrinthes
	private static final AlgorithmRunner generator = ApplicationModel.getInstance().getGenerator();

	@FXML
	public void initialize() {
		mazePanel.setMaze(ApplicationModel.getInstance().getMaze());

		addGenerationButton("Algorithme de Kruskal standard", ae -> regenerate(new KruskalAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de Kruskal non-trié",
				ae -> regenerate(new ShuffledKruskalAlgorithm(mazePanel)));
		addGenerationButton("Algorithme personnel", ae -> regenerate(new PersonalAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de rembobinage récursif",
				ae -> regenerate(new RecursiveBacktrackingAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de division récursive",
				ae -> regenerate(new RecursiveDivisionAlgorithm(mazePanel)));

		delta.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			String content = delta.getText();
			if (e.getCode() == KeyCode.ENTER) {
				generator.setTimeout(Long.valueOf(content));
				System.out.println("Nouveau pas de temps : " + generator.getTimeout() + " ms.");
			}
		});

		stepOrRun.selectedProperty().addListener((bean_p, old_p, new_p) -> {
			runButton.setVisible(!new_p.booleanValue());
			stepButton.setVisible(new_p.booleanValue());
		});

		runButton.setVisible(!stepOrRun.isSelected());
		stepButton.setVisible(stepOrRun.isSelected());

		runButton.disableProperty().bind(group.selectedToggleProperty().isNull());
		stepButton.disableProperty().bind(group.selectedToggleProperty().isNull());

		generator.addObserver(this);
		group.selectedToggleProperty().addListener(b -> generator.reset());

		newMazeRows.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
			if (ke.getCode() == KeyCode.ENTER)
				reloadMaze();
		});

		newMazeColumns.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
			if (ke.getCode() == KeyCode.ENTER)
				reloadMaze();
		});
	}

	@FXML
	private void setTimeout() {
		generator.setTimeout(delta.getValue());
		System.out.println("Nouveau pas de temps : " + generator.getTimeout() + " ms.");
	}

	@FXML
	private void reloadMaze() {
		try {
			ApplicationModel.getInstance().reload(newMazeRows.getValue(), newMazeColumns.getValue());
			mazePanel.setMaze(ApplicationModel.getInstance().getMaze());
		} catch (Exception e) {
			MessageBox box = new MessageBox(AlertType.ERROR, "Création du labyrinthe");
			box.setContentText(e.getLocalizedMessage());
			box.showAndWait();
		}
	}

	@FXML
	private void runOrWait() {
		try {
			if (runButton.isSelected())
				generator.start();
			else
				generator.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void step() throws InterruptedException {
		generator.step();
	}

	/**
	 * Ajoute un bouton de génération de labyrinthe dans l'IHM.
	 * 
	 * @param label     Nom de l'algorithme de génération.
	 * @param actionner Fonction de génération du labyrinthe.
	 */
	private void addGenerationButton(String label, EventHandler<ActionEvent> actionner) {
		ToggleButton genButton = new ToggleButton(label);
		genButton.setToggleGroup(group);
		genButton.setOnAction(ae -> {
			if (genButton.isSelected())
				actionner.handle(ae);
		});

		generationButtons.getChildren().add(genButton);
	}

	/**
	 * Charge un nouvel algorithme dans le contrôleur et régénère le labyrinthe.
	 * 
	 * @param genAlgo Algorithme à exécuter.
	 */
	public final void regenerate(Algorithm genAlgo) {
		mazePanel.clear();
		generator.setAlgorithm(genAlgo);
		genAlgo.init();
	}

	@Override
	public void update(Observable o, Object arg) {
		Platform.runLater(() -> {
			Duration time = ((AlgorithmRunner) o).getTime();
			double chronoTime = time.getSeconds() + time.getNano() / 1e9D;

			elapsedTime.setText(String.format("%.3f", chronoTime) + " s");

			ObsRunnerState state = (ObsRunnerState) arg;
			if (state == ObsRunnerState.ALGORITHM) {
				Algorithm algo = ((AlgorithmRunner) o).getAlgorithm();
				if (algo instanceof KruskalAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(0));
				else if (algo instanceof ShuffledKruskalAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(1));
				else if (algo instanceof PersonalAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(2));
			}
		});
	}
}