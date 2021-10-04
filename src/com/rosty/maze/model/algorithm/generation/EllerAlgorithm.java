package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme d'Eller</h1>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class EllerAlgorithm extends MazeGenerationAlgorithm {
	/** Indicateur de position dans la grille. */
	private int cellId = 0;

	/** Mode de construction du labyrinthe sur la ligne courante. */
	private Mode mode = Mode.SOUTH_PROCESS;

	/** Incrémenteur de la valeur de la cellule. */
	private int inc = 2;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link EllerAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public EllerAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		for (int i = 0; i < nbRow - 1; i++) {
			for (int j = 0; j < nbCol - 1; j++) {
				mazePanel.setWall(i, j, Side.DOWN, 0);
				mazePanel.setWall(i, j, Side.RIGHT, 0);
			}

			mazePanel.setWall(i, nbCol - 1, Side.DOWN, 0);
		}

		for (int j = 0; j < nbCol - 1; j++)
			mazePanel.setWall(nbRow - 1, j, Side.RIGHT, 0);
	}

	@Override
	public boolean isComplete() {
		return cellId == nbRow * nbCol - 1;
	}

	@Override
	public void step() {
		int i = cellId / nbCol;
		int j = cellId % nbCol;

		// Assignation d'un nombre pour toutes les cases de la grille
		if (j == 0) {
			// Le mode est "renversé" à chaque début de ligne.
			mode = ((mode == Mode.EAST_PROCESS) ? Mode.SOUTH_PROCESS : Mode.EAST_PROCESS);
			
			if (mode == Mode.EAST_PROCESS) {
				if (i == 0) {
					for (int k = 0; k < nbCol; k++)
						mazePanel.setCell(i, k, inc++);
				} else {
					for (int k = 0; k < nbCol; k++)
						if (mazePanel.getWall(i, k, Side.UP) == 0)
							mazePanel.setCell(i, k, mazePanel.getCell(i - 1, k));
						else
							mazePanel.setCell(i, k, inc++);
				}
			}
		}

		// Placement des murs à chaque nouvelle ligne visitée
		if (j == 0) {
			for (int k = 0; k < nbCol - 1; k++)
				mazePanel.setWall(i, k, Side.RIGHT, 1);

			if (i < nbRow - 1)
				for (int k = 0; k < nbCol; k++)
					mazePanel.setWall(i, k, Side.DOWN, 1);
		}

		switch (mode) {
		case EAST_PROCESS:
			if (j < nbCol - 1)
				if (i == nbRow - 1) {
					if (mazePanel.getCell(i, j) != mazePanel.getCell(i, j + 1))
						mazePanel.setWall(i, j, Side.RIGHT, 1);
				} else {
					if (mazePanel.getCell(i, j) != mazePanel.getCell(i, j + 1))
						mazePanel.setWall(i, j, Side.RIGHT, rand.nextInt(2));
				}

			break;
		case SOUTH_PROCESS:
			int val = rand.nextInt(2);
			if (val == 0)
				downWall(i, j, 0);

			break;
		default:
			break;
		}

//		if (mode == Mode.EAST_PROCESS) {
//			if (j + 1 < nbCol)
//				if (i == 0) {
//					rightWall(i, j, rand.nextInt(2));
//				} else if (i == nbRow - 1) {
//					if (mazePanel.getCell(i, j) != mazePanel.getCell(i, j + 1))
//						rightWall(i, j, 0);
//				} else {
//					if (mazePanel.getCell(i, j) == mazePanel.getCell(i, j + 1))
//						rightWall(i, j, 1);
//					else
//						rightWall(i, j, rand.nextInt(2));
//				}
//		} else if (mode == Mode.SOUTH_PROCESS) {
//			if (i + 1 < nbRow)
//				decideForDownWall(i, j);
//		}

		System.out.println("(" + i + ", " + j + ") -> " + mode.name());

		// If the current cell reaches the right side of the grid without reaching the
		// bottom, two actions can be performed:
		// - if EAST_PROCESS is active, the current cell goes to the next line.
		// - if SOUTH_PROCESS is active, the current cell goes back to the beginning of
		// the line.
		// A mode reversing is made too.
		if (j == nbCol - 1 && i < nbRow - 1) {
			if (mode == Mode.SOUTH_PROCESS) {
				cellId += 1;
			} else if (mode == Mode.EAST_PROCESS) {
				cellId -= (nbCol - 1);
			}
		} else
			cellId++;
	}

	private void rightWall(int i, int j, int value) {
		mazePanel.setWall(i, j, Side.RIGHT, value);
		if (value == 0) {
			int before = mazePanel.getCell(i, j);
			int after = mazePanel.getCell(i, j + 1);
			int min = Math.min(before, after);
			int max = Math.max(before, after);

			for (int k = 0; k < nbCol; k++) {
				if (mazePanel.getCell(i, k) == max)
					mazePanel.setCell(i, k, min);
			}

//			mazePanel.setCell(i, j + 1, mazePanel.getCell(i, j));
		}
	}

	private void decideForDownWall(int i, int j) {
		int val = mazePanel.getCell(i, j);

		boolean openedDoor = false;
		for (int k = 0; k < j; k++) {
			if (mazePanel.getCell(i, k) == val && mazePanel.getWall(i, k, Side.DOWN) == 0)
				openedDoor = true;
		}

//		int k1 = j;
//		while (k1 >= 0 && mazePanel.getWall(i, k1, Side.LEFT) == 0) {
//			if (/* mazePanel.getCell(i, k1) == val && */mazePanel.getWall(i, k1, Side.DOWN) == 0)
//				openedDoor = true;
//
//			k1--;
//		}

		ArrayList<Integer> array = new ArrayList<>();
		for (int k = j + 1; k < nbCol; k++) {
			if (mazePanel.getCell(i, k) == val)
				array.add(k);
		}

//		int k2 = j;
//		while (k2 < nbCol && mazePanel.getWall(i, k2, Side.RIGHT) == 0) {
//			if (mazePanel.getCell(i, k2) == val)
//				array.add(k2);
//
//			k2++;
//		}

		if (array.isEmpty() && !openedDoor) {
			downWall(i, j, 0);
		} else {
			int placeWall = rand.nextInt(2);
			downWall(i, j, placeWall);
		}
	}

	private void downWall(int i, int j, int value) {
		mazePanel.setWall(i, j, Side.DOWN, value);
		if (value == 0)
			mazePanel.setCell(i + 1, j, mazePanel.getCell(i, j));
	}

	private enum Mode {
		SOUTH_PROCESS, EAST_PROCESS;
	}
}