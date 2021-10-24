package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
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
		switch (nextAlgo()) {
			case PRIM:
				algo_prim();
				break;
			case RECURSIVE_BACKTRACKING:
				algo_rb();
				break;
			default:
				break;
		}
	}

	private SubAlgo nextAlgo() {
		float ran = rand.nextFloat();

		if (ran == algoRatio)
			return nextAlgo();
		else
			return ran > algoRatio ? SubAlgo.RECURSIVE_BACKTRACKING : SubAlgo.PRIM;
	}

	private void algo_prim() {
		carve(rand.nextInt(pendingCells.size()));
	}

	private void algo_rb() {
		carve(pendingCells.size() - 1);
	}

	private void carve(int index) {
		int[] selectedCell = pendingCells.get(index), selectedNewCell = null;
		int r = selectedCell[0], c = selectedCell[1];

		List<Side> removableWalls = new ArrayList<>();

		// Répérage des murs qui peuvent être retirés
		if (r > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.UP)) == 2)
			removableWalls.add(Side.UP);

		if (r < nbRow - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.DOWN)) == 2)
			removableWalls.add(Side.DOWN);

		if (c > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.LEFT)) == 2)
			removableWalls.add(Side.LEFT);

		if (c < nbCol - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.RIGHT)) == 2)
			removableWalls.add(Side.RIGHT);

		if (!removableWalls.isEmpty()) {
			Side removedWall = removableWalls.get(rand.nextInt(removableWalls.size()));
			switch (removedWall) {
				case UP:
					selectedNewCell = new int[] { r - 1, c };
					break;
				case DOWN:
					selectedNewCell = new int[] { r + 1, c };
					break;
				case LEFT:
					selectedNewCell = new int[] { r, c - 1 };
					break;
				case RIGHT:
					selectedNewCell = new int[] { r, c + 1 };
					break;
				default:
					break;
			}

			mazePanel.setWall(r, c, removedWall, 0);
			if (selectedNewCell != null)
				addPending(selectedNewCell);
		} else
			removePending(index);
	}

	private void addPending(int[] newCell) {
		pendingCells.add(newCell);
		mazePanel.setCell(newCell[0], newCell[1], 1);
	}

	private void removePending(int index) {
		int[] cell = pendingCells.get(index);
		mazePanel.setCell(cell[0], cell[1], 0);

		pendingCells.remove(index);
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