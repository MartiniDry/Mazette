package com.rosty.maze.controller;

import java.time.Duration;
import java.util.Observable;
import java.util.Observer;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.application.labels.PropertiesManager;
import com.rosty.maze.model.ApplicationModel;
import com.rosty.maze.model.MazeRoute;
import com.rosty.maze.model.algorithm.Algorithm;
import com.rosty.maze.model.algorithm.AlgorithmRunner;
import com.rosty.maze.model.algorithm.AlgorithmRunner.ObsRunnerState;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.view.box.MessageBox;
import com.rosty.maze.widgets.GIntegerField;
import com.rosty.maze.widgets.GLongField;
import com.rosty.maze.widgets.MazePanel;
import com.rosty.util.colormap.DiscreteColorMap;
import com.rosty.util.maze.MazeUtils;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
	private Label algoName;

	@FXML
	private CheckBox stepOrRun;

	@FXML
	private GLongField delta;
	@FXML
	private Slider deltaSlider;

	@FXML
	private VBox generationButtons;

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
	private static final AlgorithmRunner GENERATOR = ApplicationModel.getInstance().getGenerator();

	// Solveur de labyrinthes
	private static final AlgorithmRunner SOLVER = ApplicationModel.getInstance().getSolver();

	@FXML
	public void initialize() {
		mazePanel.setMaze(ApplicationModel.getInstance().getMaze());
		mazePanel.setRoute(ApplicationModel.getInstance().getRoute());

		DiscreteColorMap colorMap = new DiscreteColorMap();
		colorMap.add(0, Color.TRANSPARENT);
		colorMap.add(1, Color.web("#0F06"));
		colorMap.add(2, Color.web("#00F6"));
//		DiscreteColorMap colorMap = DiscreteColorMap.fromString("[0; transparent] [1; #0F06] [2; #00F6]");

		mazePanel.setBlockColorMap(colorMap);

		delta.setValue(GENERATOR.getTimeout());
		delta.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			String content = delta.getText();
			if (e.getCode() == KeyCode.ENTER) {
				GENERATOR.setTimeout(content.isEmpty() ? 0L : Long.valueOf(content));
				SOLVER.setTimeout(content.isEmpty() ? 0L : Long.valueOf(content));
				Mazette.LOGGER.info("Nouveau pas de temps : " + GENERATOR.getTimeout() + " µs.");
			}
		});

		deltaSlider.setValue((long) Math.pow(10, 6 - GENERATOR.getTimeout()));
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

		GENERATOR.addObserver(this);
		SOLVER.addObserver(this);

		newMazeRows.setValue(ApplicationModel.getInstance().getMaze().getNbRows());
		newMazeColumns.setValue(ApplicationModel.getInstance().getMaze().getNbColumns());

		EventHandler<? super KeyEvent> actionReload = ke -> {
			if (ke.getCode() == KeyCode.ENTER)
				reloadMaze();
		};
		newMazeRows.addEventFilter(KeyEvent.KEY_PRESSED, actionReload);
		newMazeColumns.addEventFilter(KeyEvent.KEY_PRESSED, actionReload);

		// Liaison des propriétés de l'application
		PropertiesManager.link("hmi.grid", mazePanel.gridColorProperty());
		PropertiesManager.link("hmi.border", mazePanel.borderColorProperty());
		PropertiesManager.link("hmi.wall", mazePanel.wallColorProperty());
	}

	@FXML
	private void setTimeout() {
		Mazette.LOGGER.info("Nouveau pas de temps : " + delta.getValue() + " µs.");

		GENERATOR.setTimeout(delta.getValue());
		SOLVER.setTimeout(delta.getValue());
		double timeValue = 6 - Math.log10(delta.getValue());
		deltaSlider.setValue(timeValue);
	}

	@FXML
	private void setTimeoutFromSlider() {
		long timeValue = (long) Math.pow(10, 6 - deltaSlider.getValue());
		Mazette.LOGGER.info("Nouveau pas de temps : " + timeValue + " µs.");

		GENERATOR.setTimeout(timeValue);
		SOLVER.setTimeout(timeValue);
		delta.setValue(timeValue);
	}

	@FXML
	private void reloadMaze() {
		try {
			ApplicationModel.getInstance().reload(newMazeRows.getValue(), newMazeColumns.getValue());
			mazePanel.setMaze(ApplicationModel.getInstance().getMaze());
			mazePanel.setRoute(ApplicationModel.getInstance().getRoute());
			GENERATOR.reset();
			SOLVER.reset();
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
			switch (ApplicationModel.getInstance().getMode()) {
				case GENERATION:
					if (runButton.isSelected())
						GENERATOR.start();
					else
						GENERATOR.stop();

					break;
				case RESOLUTION:
					if (runButton.isSelected()) {
						// Le solveur n'est lancé que si aucune route n'est visible à l'écran.
						if (mazePanel.getRoute().getPath().isEmpty())
							SOLVER.start();
					} else
						SOLVER.stop();

					break;
				default:
					break;
			}
		} catch (Exception e) {
			Mazette.LOGGER.error(e.getMessage(), e);
		}
	}

	@FXML
	private void step() throws InterruptedException {
		switch (ApplicationModel.getInstance().getMode()) {
			case GENERATION:
				GENERATOR.step();
				break;
			case RESOLUTION:
				// Le solveur n'est lancé que si aucune route n'est visible à l'écran.
				if (mazePanel.getRoute().getPath().isEmpty())
					SOLVER.step();

				break;
			default:
				break;
		}
	}

	/**
	 * Efface le labyrinthe et charge un nouvel algorithme de génération dans le
	 * contrôleur.
	 * 
	 * @param genAlgo Algorithme de génération à exécuter.
	 */
	public final void regenerate(MazeGenerationAlgorithm genAlgo) {
		mazePanel.clear();
		GENERATOR.setAlgorithm(genAlgo);
		GENERATOR.reset();

		SOLVER.reset();
	}

	/**
	 * Efface le chemin du labyrinthe et charge un nouvel algorithme de résolution
	 * dans le contrôleur.
	 * <p>
	 * <b>NB:</b> le choix de l'algorithme n'est possible que si le labyrinthe est
	 * connexe.
	 * </p>
	 * 
	 * @param solAlgo Algorithme de résolution à exécuter.
	 */
	public final void resetSolve(MazeSolvingAlgorithm solAlgo) {
		try {
			if (MazeUtils.isConnected(mazePanel.getMaze())) {
				MazeRoute route = new MazeRoute();
				route.setStart(0, 0);
				route.setEnd(mazePanel.getMaze().getNbRows() - 1, mazePanel.getMaze().getNbColumns() - 1);
				mazePanel.setRoute(route);

				SOLVER.setAlgorithm(solAlgo);
				SOLVER.reset();
			} else
				throw new Exception(LocaleManager.getString("error.maze.run.connected"));
		} catch (Exception e) {
			Mazette.LOGGER.error(e.getMessage(), e);

			MessageBox box = new MessageBox(AlertType.ERROR, LocaleManager.getString("error.maze.run"));
			box.setContentText(e.getLocalizedMessage());
			box.showAndWait();
		}
	}

	/**
	 * Affiche le nom d'un algorithme dans l'IHM avec la locale courante.
	 * 
	 * @param algo Instance de la classe {@link Algorithm}.
	 */
	final void displayAlgoName(Algorithm algo) {
		if (algo != null) {
			if (algo instanceof MazeGenerationAlgorithm || algo instanceof MazeSolvingAlgorithm) {
				String localeKey = "main.menu." + algo.getLabel();
				algoName.setText(LocaleManager.getString(localeKey));
			} else
				algoName.setText("<algo label: " + algo.getLabel() + ">");
		} else
			algoName.setText(null);
	}

	/** Affiche le nom de l'algorithme courant dans l'IHM. */
	public final void displayAlgoName() {
		switch (ApplicationModel.getInstance().getMode()) {
			case GENERATION:
				displayAlgoName(GENERATOR.getAlgorithm());
				break;
			case RESOLUTION:
				displayAlgoName(SOLVER.getAlgorithm());
				break;
			default:
				break;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof AlgorithmRunner) {
			Platform.runLater(() -> {
				Duration time = ((AlgorithmRunner) o).getTime();
				double chronoTime = time.getSeconds() + time.getNano() / 1e9D;

				elapsedTime.setText(String.format("%.3f", chronoTime) + " s");

				ObsRunnerState state = (ObsRunnerState) arg;
				if (state == ObsRunnerState.ALGORITHM) {
					Algorithm algo = ((AlgorithmRunner) o).getAlgorithm();
					displayAlgoName(algo);

					// Les boutons d'actions sont grisés lorsque aucun algorithme n'a été choisi.
					runButton.setDisable(algo == null);
					stepButton.setDisable(algo == null);
				}
			});
		} else
			Mazette.LOGGER.error("The observable event is unknown: " + arg.getClass().getSimpleName() + "." + arg);
	}
}