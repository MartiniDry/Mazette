package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme du blob</h1>
 * 
 * <p>
 * <h2>Principe</h2> XXX
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> XXX
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> XXX
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class GrowingTreeAlgorithm extends MazeGenerationAlgorithm {
	/** Ratio d'utilisation des algorithmes. */
	private float algoRatio = 0.5F;

	/**
	 * Liste des cellules en suspens. Celles-ci sont des points d'ancrage pour
	 * exécuter l'algorithme.
	 */
	private List<int[]> pendingCells = new ArrayList<>();

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link GrowingTreeAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public GrowingTreeAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		/* Etape 1 : remplissage du terrain avec la valeur 2. */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 2);
		
		/* Etape 2 : placement de la toute première cellule à analyser. */
		int r = rand.nextInt(nbRow), c = rand.nextInt(nbCol);
		pendingCells.add(new int[] { r, c });
		mazePanel.setCell(r, c, 1);
	}

	@Override
	public boolean isComplete() {
		return pendingCells.isEmpty();
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
	}

	private SubAlgo nextAlgo() {
		float ran = rand.nextFloat();

		if (ran == algoRatio)
			return nextAlgo();
		else
			return ran > algoRatio ? SubAlgo.RECURSIVE_BACKTRACKING : SubAlgo.PRIM;
	}

	/**
	 * Enumération des algorithmes de génération présents au coeur de celui-ci.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	private enum SubAlgo {
		PRIM, RECURSIVE_BACKTRACKING;
	}
}