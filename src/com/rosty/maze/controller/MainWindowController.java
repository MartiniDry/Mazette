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
import com.rosty.maze.view.box.MessageBox;
import com.rosty.maze.widgets.GIntegerField;
import com.rosty.maze.widgets.GLongField;
import com.rosty.maze.widgets.MazePanel;
import com.rosty.util.colormap.DiscreteColorMap;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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
//		colorMap.setProfile(n -> {
//			double ratio = (n * 1.) / (2 + mazePanel.getMaze().getNbRows() * mazePanel.getMaze().getNbColumns());
//			return ColorUtils.middleColor(Color.web("#0F0B"), Color.web("#00FB"), ratio);
//		});

		mazePanel.setBlockColorMap(colorMap);

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
				switch (object.intValue()) {
					case 0:
						return "1s";
					case 3:
						return "1ms";
					case 6:
						return "1µs";
					default:
						return "";
				}
			}

			@Override
			public Double fromString(String string) {
				return null;
			}
		});

		stepOrRun.selectedProperty().addListener((bean_p, old_p, new_p) -> {
			runButton.setVisible(!new_p.booleanValue());
			stepButton.setVisible(new_p.booleanValue());
		});

		runButton.setVisible(!stepOrRun.isSelected());
		stepButton.setVisible(stepOrRun.isSelected());

		generator.addObserver(this);

		newMazeRows.setValue(ApplicationModel.getInstance().getMaze().getNbRows());
		newMazeColumns.setValue(ApplicationModel.getInstance().getMaze().getNbColumns());
		
		EventHandler<? super KeyEvent> actionReload = ke -> {
			if (ke.getCode() == KeyCode.ENTER)
				reloadMaze();
		};
		newMazeRows.addEventFilter(KeyEvent.KEY_PRESSED, actionReload);
		newMazeColumns.addEventFilter(KeyEvent.KEY_PRESSED, actionReload);
	}

	@FXML
	private void setTimeout() {
		Mazette.LOGGER.info("Nouveau pas de temps : " + delta.getValue() + " µs.");
		
		generator.setTimeout(delta.getValue());
		double timeValue = 6 - Math.log10(delta.getValue());
		deltaSlider.setValue(timeValue);
	}

	@FXML
	private void setTimeoutFromSlider() {
		long timeValue = (long) Math.pow(10, 6 - deltaSlider.getValue());
		Mazette.LOGGER.info("Nouveau pas de temps : " + timeValue + " µs.");
		
		generator.setTimeout(timeValue);
		delta.setValue(timeValue);
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
	 * Charge un nouvel algorithme dans le contrôleur et régénère le labyrinthe.
	 * 
	 * @param genAlgo Algorithme à exécuter.
	 */
	public final void regenerate(Algorithm genAlgo) {
		mazePanel.clear();
		generator.setAlgorithm(genAlgo);
		generator.reset();
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
				runButton.setDisable(algo == null);
				stepButton.setDisable(algo == null);
			}
		});
	}
}