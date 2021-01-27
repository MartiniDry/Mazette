package com.rosty.maze.model;

import com.rosty.maze.model.algorithm.AlgorithmRunner;
import com.rosty.maze.view.ResourceManager;

/**
 * Classe définissant le modèle du logiciel.
 * <p>
 * Cette classe est un singleton.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class ApplicationModel {
	/** Labyrinthe affiché par le logiciel. */
	private Maze maze = new Maze(10, 10); // Labyrinthe présent au lancement du logiciel

	/** Lanceur permettant la génération d'un labyrinthe. */
	private AlgorithmRunner generator;

	/** Instance unique de la classe. */
	private static ApplicationModel INSTANCE = null;

	/** Constructeur de la classe {@link ApplicationModel}. */
	private ApplicationModel() {
		generator = new AlgorithmRunner();
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

	/** Fournit le lanceur d'algorithme pour la génération du labyrinthe. */
	public AlgorithmRunner getGenerator() {
		return generator;
	}

	/**
	 * Recharge le labyrinthe avec de nouveaux paramètres.
	 * 
	 * @param nbRow Nombre de lignes.
	 * @param nbCol Nombre de colonnes.
	 */
	public void reload(int nbRow, int nbCol) throws Exception {
		if (nbRow > 0 && nbCol > 0)
			maze = new Maze(nbRow, nbCol);
		else
			throw new Exception(ResourceManager.getString("error.maze.creation.size"));
	}
}