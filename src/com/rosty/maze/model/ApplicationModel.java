package com.rosty.maze.model;

import java.util.Observable;
import java.util.Observer;

import com.rosty.maze.Mazette;
import com.rosty.maze.application.labels.LocaleManager;
import com.rosty.maze.model.algorithm.AlgorithmRunner;
import com.rosty.maze.model.algorithm.AlgorithmRunner.ObsRunnerState;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;

/**
 * Classe définissant le modèle du logiciel.
 * <p>
 * Cette classe est un singleton.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class ApplicationModel implements Observer {
	/** Labyrinthe affiché par le logiciel. */
	private Maze maze = new Maze(10, 10); // Labyrinthe présent au lancement du logiciel

	/** Chemin emprunté dans le labyrinthe. */
	private MazeRoute route = new MazeRoute(); // Egalement présent au lancement

	/** Lanceur permettant la génération d'un labyrinthe. */
	private AlgorithmRunner generator;

	/** Lanceur dédié à la résolution du labyrinthe sus-mentionné. */
	private AlgorithmRunner solver;

	/**
	 * Mode d'exécution de l'application (résolution ou génération de labyrinthe).
	 */
	private Mode mode = Mode.GENERATION;

	/** Instance unique de la classe. */
	private static ApplicationModel INSTANCE = null;

	/** Constructeur de la classe {@link ApplicationModel}. */
	private ApplicationModel() {
		generator = new AlgorithmRunner();
		generator.addObserver(this);

		solver = new AlgorithmRunner();
		solver.addObserver(this);
	}

	/** Fournit l'instance du singleton {@link ApplicationModel}. */
	public static ApplicationModel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ApplicationModel();

		return INSTANCE;
	}

	/** Fournit le labyrinthe actuellement utilisé. */
	public Maze getMaze() {
		return maze;
	}

	/** Fournit la route empruntée au sein du labyrinthe. */
	public MazeRoute getRoute() {
		return route;
	}

	/** Fournit le lanceur d'algorithme pour la génération du labyrinthe. */
	public AlgorithmRunner getGenerator() {
		return generator;
	}

	/** Fournit le lanceur d'algorithme pour la résolution de labyrinthe. */
	public AlgorithmRunner getSolver() {
		return solver;
	}

	/** Fournit le mode d'exécution actuel de l'application. */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Recharge le labyrinthe avec de nouveaux paramètres.
	 * 
	 * @param nbRow Nombre de lignes.
	 * @param nbCol Nombre de colonnes.
	 */
	public void reload(int nbRow, int nbCol) throws Exception {
		if (nbRow > 0 && nbCol > 0) {
			maze = new Maze(nbRow, nbCol);
			Mazette.LOGGER.info("New maze created: " + nbRow + " rows, " + nbCol + " columns");
		} else
			throw new Exception(LocaleManager.getString("error.maze.creation.size"));
	}

	/**
	 * Enumération des modes de fonctionnement généraux de l'application.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	public enum Mode {
		GENERATION, RESOLUTION;
	}

	@Override
	public void update(Observable o, Object arg) {
		// Il est essentiel de vérifier l'état de l'observable pour ne pas crasher
		// l'application : en effet, si l'on modifie l'algorithme puis que l'on "reset"
		// le lanceur, une NullPointerException va être lancée car l'on va remettre à
		// zéro une instance Algorithm qui a été retirée.
		if ((ObsRunnerState) arg == ObsRunnerState.ALGORITHM) {
			// Recherche de la super-classe pour détecter le type d'algorithme
			Class<?> algoClass = ((AlgorithmRunner) o).getAlgorithm().getClass().getSuperclass();
			if (algoClass == MazeGenerationAlgorithm.class)
				mode = Mode.GENERATION;
			else if (algoClass == MazeSolvingAlgorithm.class)
				mode = Mode.RESOLUTION;
			else
				Mazette.LOGGER.error("This algorithm cannot be determined for the application.");
		}
	}
}