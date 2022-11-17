package com.rosty.maze.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

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
	private TextField test;

	@FXML
	private ToggleButton runButton;
	@FXML
	private Button stepButton;
	@FXML
	private Label elapsedTime;

	@FXML
	Label justTheResult;

	@FXML
	public MazePanel mazePanel;

	/* ATTRIBUTS */

	// Générateur de labyrinthes
	private static final AlgorithmRunner GENERATOR = ApplicationModel.getInstance().getGenerator();

	// Solveur de labyrinthes
	private static final AlgorithmRunner SOLVER = ApplicationModel.getInstance().getSolver();

	// Table des "labels" des algorithmes. Ces identifiants permettent d'afficher le
	// nom des algorithmes à l'écran avec la langue choisie par l'utilisateur.
	private static final HashMap<Class<? extends Algorithm>, String> ALGO_LABELS = new HashMap<>();
	static {
		ALGO_LABELS.put(AldousBroderAlgorithm.class, "main.menu.generation.aldous_broder");
		ALGO_LABELS.put(BinaryTreeAlgorithm.class, "main.menu.generation.binary_tree");
		ALGO_LABELS.put(EllerAlgorithm.class, "main.menu.generation.eller");
		ALGO_LABELS.put(GrowingTreeAlgorithm.class, "main.menu.generation.growing_tree");
		ALGO_LABELS.put(HuntAndKillAlgorithm.class, "main.menu.generation.hunt_and_kill");
		ALGO_LABELS.put(KruskalAlgorithm.class, "main.menu.generation.kruskal.unsorted");
		ALGO_LABELS.put(PersonalAlgorithm.class, "main.menu.generation.personal._1");
		ALGO_LABELS.put(Personal2Algorithm.class, "main.menu.generation.personal._2");
		ALGO_LABELS.put(PrimAlgorithm.class, "main.menu.generation.prim");
		ALGO_LABELS.put(RecursiveBacktrackingAlgorithm.class, "main.menu.generation.recursive_backtracker");
		ALGO_LABELS.put(RecursiveDivisionAlgorithm.class, "main.menu.generation.recursive_division");
		ALGO_LABELS.put(ShuffledKruskalAlgorithm.class, "main.menu.generation.kruskal.sorted");
		ALGO_LABELS.put(SidewinderAlgorithm.class, "main.menu.generation.sidewinder");
		ALGO_LABELS.put(WilsonAlgorithm.class, "main.menu.generation.wilson");

		ALGO_LABELS.put(WallFollowingAlgorithm.class, "main.menu.resolution.wall_following");
	}

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
		double timeValue = 6 - Math.log10(delta.getValue());
		deltaSlider.setValue(timeValue);
	}

	@FXML
	private void setTimeoutFromSlider() {
		long timeValue = (long) Math.pow(10, 6 - deltaSlider.getValue());
		Mazette.LOGGER.info("Nouveau pas de temps : " + timeValue + " µs.");

		GENERATOR.setTimeout(timeValue);
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
			if (runButton.isSelected())
				GENERATOR.start();
			else
				GENERATOR.stop();
		} catch (Exception e) {
			Mazette.LOGGER.error(e.getMessage(), e);
		}
	}

	@FXML
	private void justATest() {
		int r = ApplicationModel.getInstance().getMaze().getNbRows();
		int c = ApplicationModel.getInstance().getMaze().getNbColumns();

		int xa, ya, xb, yb;

		xa = (new Random()).nextInt(r);
		ya = (new Random()).nextInt(c);

		do {
			xb = (new Random()).nextInt(r);
			yb = (new Random()).nextInt(c);
		} while (xa == xb && ya == yb);

		MazeRoute route = new MazeRoute();
		route.setStart(xa, ya);
		route.setEnd(xb, yb);

		justTheResult.setText(String.format("(%s,%s) -> (%s,%s)", xa, ya, xb, yb));

		int acc = 0, i = ya, j = xa, lastOne = -1;
		do {
			route.getPath().add(new int[] { i, j });

			int current = -1;
			boolean outOfBounds = false;
			do {
				current = (int) (2 * Math.sqrt((new Random()).nextInt(4)));

				switch (current) {
					case 0:
						outOfBounds = (i + 1 >= r);
						break;
					case 1:
						outOfBounds = (j + 1 >= c);
						break;
					case 2:
						outOfBounds = (i - 1 < 0);
						break;
					case 3:
						outOfBounds = (j - 1 < 0);
						break;
					default:
						break;
				}
			} while (current == lastOne || outOfBounds);

			lastOne = current;

			System.out.println(current);

			switch (current) {
				case 0:
					i++;
					break;
				case 1:
					j++;
					break;
				case 2:
					i--;
					break;
				case 3:
					j--;
					break;
				default:
					break;
			}
		} while (acc++ < 10);

		mazePanel.setRoute(route);
	}

	@FXML
	private void step() throws InterruptedException {
		switch (ApplicationModel.getInstance().getMode()) {
			case GENERATION:
				GENERATOR.step();
				break;
			case RESOLUTION:
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
	 * 
	 * @param solAlgo Algorithme de résolution à exécuter.
	 */
	public final void resetSolve(MazeSolvingAlgorithm solAlgo) {
		// TODO insérer le critère de connexité du labyrinthe avant de choisir l'algo
		mazePanel.setRoute(null);
		SOLVER.setAlgorithm(solAlgo);
		SOLVER.reset();
	}

	/**
	 * Affiche le nom d'un algorithme dans l'IHM avec la locale courante.
	 * 
	 * @param algo Instance de la classe {@link Algorithm}.
	 */
	final void displayAlgoName(Algorithm algo) {
		if (algo != null) {
			String algoLabel = ALGO_LABELS.get(algo.getClass());

			// Le label peut faire partie d'une sous-famille d'algorithmes (ex :
			// l'algorithme de Kruskal se décline en 2 versions, triée et non-triée). Cette
			// sous-famille est représentée par un point supplémentaire dans le label.
			String temp = algoLabel.substring("main.menu.".length()); // Décomposition du label couche par couche
			if (temp.startsWith("generation")) {
				temp = temp.substring("generation.".length());
				if (!temp.contains(".")) { // Si l'algorithme est identifié seulement par un nom, ...
					// ...alors l'afficher dans l'IHM.
					algoName.setText(LocaleManager.getString(algoLabel));
				} else { // Si l'algorithme est identifié par un nom et un ou plusieurs aspects, ...
					// ...alors afficher le nom suivi des caractéristiques entre parenthèses.
					StringBuilder finalText = new StringBuilder();
					String[] keys = temp.split("\\.");

					// Recomposition du label
					temp = "main.menu.generation";
					temp += ("." + keys[0]);
					finalText.append(LocaleManager.getString(temp) + " ("); // Nom

					for (int i = 1; i < keys.length; i++) {
						temp += ("." + keys[i]);
						finalText.append(LocaleManager.getString(temp) + ", ");
					}

					finalText.delete(finalText.length() - 2, finalText.length());
					finalText.append(")");
					algoName.setText(finalText.toString());
				}
			} else
				algoName.setText(LocaleManager.getString(algoLabel));
		} else
			algoName.setText(null);
	}

	/** Affiche le nom de l'algorithme courant dans l'IHM. */
	public final void displayAlgoName() {
		displayAlgoName(GENERATOR.getAlgorithm());
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