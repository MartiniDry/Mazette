package com.rosty.maze.controller;

import java.time.Duration;
import java.util.Observable;
import java.util.Observer;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.model.ApplicationModel;
import com.rosty.maze.model.algorithm.Algorithm;
import com.rosty.maze.model.algorithm.AlgorithmRunner;
import com.rosty.maze.model.algorithm.AlgorithmRunner.ObsRunnerState;
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
import com.rosty.maze.view.box.MessageBox;
import com.rosty.maze.widgets.GIntegerField;
import com.rosty.maze.widgets.GLongField;
import com.rosty.maze.widgets.MazePanel;
import com.rosty.maze.widgets.Spacing;
import com.rosty.util.colormap.DiscreteColorMap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * Contrôleur de la fenêtre principale de l'application.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class MainWindowController implements Observer {
	/* ATTRIBUTS FXML */

	@FXML
	private BorderPane mainPane;

	@FXML
	private GIntegerField newMazeRows, newMazeColumns;

	@FXML
	private CheckBox stepOrRun;

	@FXML
	private GLongField delta;
	@FXML
	private Slider deltaSlider;

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

		DiscreteColorMap colorMap = new DiscreteColorMap();
		colorMap.put(0, Color.TRANSPARENT);
		colorMap.put(1, Color.web("#0F06"));
		colorMap.put(2, Color.web("#00F6"));
//		DiscreteColorMap colorMap = DiscreteColorMap.fromString("[0; transparent] [1; #0F06] [2; #00F6]");
		
		mazePanel.setBlockColorMap(colorMap);

		addGenerationButton("Algorithme de Kruskal standard", ae -> regenerate(new KruskalAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de Kruskal non-trié",
				ae -> regenerate(new ShuffledKruskalAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de rembobinage récursif",
				ae -> regenerate(new RecursiveBacktrackingAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de division récursive",
				ae -> regenerate(new RecursiveDivisionAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de Prim", ae -> regenerate(new PrimAlgorithm(mazePanel)));
		addGenerationButton("Algorithme \"Hunt-and-Kill\"", ae -> regenerate(new HuntAndKillAlgorithm(mazePanel)));
		addGenerationButton("Algorithme d'Aldous-Broder", ae -> regenerate(new AldousBroderAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de Wilson", ae -> regenerate(new WilsonAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de l'arbre binaire", ae -> regenerate(new BinaryTreeAlgorithm(mazePanel)));
		addGenerationButton("Algorithme de l'accordéon", ae -> regenerate(new SidewinderAlgorithm(mazePanel)));
		addGenerationButton("Algorithme d'Eller", ae -> regenerate(new EllerAlgorithm(mazePanel)));
		addGenerationButton("Algorithme personnel", ae -> regenerate(new PersonalAlgorithm(mazePanel)));
		addGenerationButton("Algorithme personnel #2", ae -> regenerate(new Personal2Algorithm(mazePanel)));
		addGenerationButton("Algorithme du blob", ae -> regenerate(new GrowingTreeAlgorithm(mazePanel)));

		delta.setValue(generator.getTimeout());
		delta.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			String content = delta.getText();
			if (e.getCode() == KeyCode.ENTER) {
				generator.setTimeout(content.isEmpty() ? 0L : Long.valueOf(content));
				Mazette.LOGGER.info("Nouveau pas de temps : " + generator.getTimeout() + " µs.");
			}
		});

		deltaSlider.setValue((long) Math.pow(10, 6 - generator.getTimeout()));
		deltaSlider.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double object) {
				if (object == 0)
					return "1s";
				else if (object == 3)
					return "1ms";
				else if (object == 6)
					return "1µs";
				else
					return "";
			}

			@Override
			public Double fromString(String string) {
				return null;
			}
		});

		deltaSlider.valueProperty().addListener((bean_p, old_p, new_p) -> {
			long timeValue = (long) Math.pow(10, 6 - new_p.doubleValue());
			generator.setTimeout(timeValue);
			delta.setValue(timeValue);
			Mazette.LOGGER.info("Nouveau pas de temps : " + generator.getTimeout() + " µs.");
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

		newMazeRows.setValue(ApplicationModel.getInstance().getMaze().getNbRows());
		newMazeRows.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
			if (ke.getCode() == KeyCode.ENTER)
				reloadMaze();
		});

		newMazeColumns.setValue(ApplicationModel.getInstance().getMaze().getNbColumns());
		newMazeColumns.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
			if (ke.getCode() == KeyCode.ENTER)
				reloadMaze();
		});
	}

	@FXML
	private void setTimeout() {
		generator.setTimeout(delta.getValue());
		Mazette.LOGGER.info("Nouveau pas de temps : " + generator.getTimeout() + " µs.");

		double timeValue = 6 - Math.log10(delta.getValue());
		deltaSlider.setValue(timeValue);
	}

	@FXML
	private void selectTime() {
		/*
		 * String val = Double.toString(((int) (deltaSlider.getValue() * 10)) / 10.0);
		 * Pane thumb = (Pane) deltaSlider.lookup(".thumb");
		 * thumb.getChildren().clear(); thumb.getChildren().add(new Label(val));
		 */
	}

	@FXML
	private void setTime() {
		;
	}

	@FXML
	private void reloadMaze() {
		try {
			ApplicationModel.getInstance().reload(newMazeRows.getValue(), newMazeColumns.getValue());
			mazePanel.setMaze(ApplicationModel.getInstance().getMaze());
		} catch (Exception e) {
			Mazette.LOGGER.error(e.getMessage(), e);

			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.maze.creation"));
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
			Mazette.LOGGER.error(e.getMessage(), e);
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
		genButton.setPadding(new Spacing(0, 5));
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
				else if (algo instanceof RecursiveBacktrackingAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(2));
				else if (algo instanceof RecursiveDivisionAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(3));
				else if (algo instanceof PrimAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(4));
				else if (algo instanceof HuntAndKillAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(5));
				else if (algo instanceof AldousBroderAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(6));
				else if (algo instanceof WilsonAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(7));
				else if (algo instanceof BinaryTreeAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(8));
				else if (algo instanceof SidewinderAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(9));
				else if (algo instanceof EllerAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(10));
				else if (algo instanceof PersonalAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(11));
				else if (algo instanceof Personal2Algorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(12));
				else if (algo instanceof GrowingTreeAlgorithm)
					group.selectToggle((ToggleButton) generationButtons.getChildren().get(13));
			}
		});
	}
}