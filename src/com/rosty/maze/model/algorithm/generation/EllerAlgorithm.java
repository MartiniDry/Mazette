package com.rosty.maze.model.algorithm.generation;

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
			mode = (mode == Mode.EAST_PROCESS ? Mode.SOUTH_PROCESS : Mode.EAST_PROCESS);
			if (mode == Mode.EAST_PROCESS) {
				fillWalls(i); // Placement des murs à chaque nouvelle ligne visitée
				fillCells(i); // Remplissage des cases vides au commencement d'une nouvelle ligne
			}
		}

		switch (mode) {
			case EAST_PROCESS:
				eastAction(i, j);
				break;
			case SOUTH_PROCESS:
				southAction(i, j);
				break;
			default:
				break;
		}

		System.out.println("(" + i + ", " + j + ") -> " + mode.name());

		// If the current cell reaches the right side of the grid without reaching the
		// bottom, two actions can be performed:
		// - if EAST_PROCESS is active, the current cell goes to the next line.
		// - if SOUTH_PROCESS is active, the current cell goes back to the beginning of
		// the line.
		//
		// A mode reversing is made too.
		if (j == nbCol - 1 && i < nbRow - 1) {
			if (mode == Mode.SOUTH_PROCESS)
				cellId++;
			else if (mode == Mode.EAST_PROCESS)
				cellId -= (nbCol - 1);
		} else
			cellId++;
	}

	private void fillWalls(int row) {
		for (int k = 0; k < nbCol - 1; k++)
			mazePanel.setWall(row, k, Side.RIGHT, 1);

		if (row < nbRow - 1)
			for (int k = 0; k < nbCol; k++)
				mazePanel.setWall(row, k, Side.DOWN, 1);
	}

	private void fillCells(int row) {
		if (row == 0)
			for (int k = 0; k < nbCol; k++)
				mazePanel.setCell(row, k, inc++);
		else
			for (int k = 0; k < nbCol; k++)
				if (mazePanel.getWall(row, k, Side.UP) == 0)
					mazePanel.setCell(row, k, mazePanel.getCell(row - 1, k));
				else
					mazePanel.setCell(row, k, inc++);
	}

	private void eastAction(int row, int col) {
		if (col < nbCol - 1) {
			int val = mazePanel.getCell(row, col);
			int nextVal = mazePanel.getCell(row, col + 1);

			if (row == nbRow - 1) { // If this is the last grid line, placing walls is completely determined.
				if (val != nextVal) {
					mazePanel.setWall(row, col, Side.RIGHT, 0);
					mazePanel.setCell(row, col + 1, val);
				}
			} else {
				if (rand.nextBoolean() == false) { // If it is decided to break the right wall, ...
					// ...then remove it and update the current cell value.
					mazePanel.setWall(row, col, Side.RIGHT, 0);
					mazePanel.setCell(row, col + 1, val);
				}
			}
		}
	}

	private void southAction(int row, int col) {
		if (row < nbRow - 1) {
			// First case to study is when there is a wall to the right side. Each group of
			// adjacent cells must be connected to the lower row, so we must be sure at
			// least one adjacent cell is connected to it.
			if (mazePanel.getWall(row, col, Side.RIGHT) == 1) {
				;
			}

			if (rand.nextBoolean() == false) { // If it is decided to break the down wall, ...
				mazePanel.setWall(row, col, Side.DOWN, 0);
				mazePanel.setCell(row + 1, col, mazePanel.getCell(row, col));
			}
		}
	}

	private enum Mode {
		SOUTH_PROCESS, EAST_PROCESS;
	}
}