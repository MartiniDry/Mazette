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
		// Tous les murs intérieurs de la grille osnt retirés.
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
			boolean rightWallToRemove = false;

			int val = mazePanel.getCell(row, col);
			int nextVal = mazePanel.getCell(row, col + 1);

			if (val != nextVal)
				if (row == nbRow - 1) {
					// S'il s'agit de la dernière ligne de la grille, le placement des murs est
					// complètement déterminé.
					rightWallToRemove = true;
				} else if (rand.nextBoolean() == false) {
					// Dans le cas contraire, les murs sont retirés aléatoirement.
					rightWallToRemove = true;
				}

			if (rightWallToRemove) {
				// ...then remove it and update the concerned line cells.
				mazePanel.setWall(row, col, Side.RIGHT, 0);

				ArrayList<Integer> family = findCellsByValue(row, nextVal);
				for (int c : family)
					mazePanel.setCell(row, c, val);
			}
		}
	}

	private void southAction(int row, int col) {
		if (row < nbRow - 1) {
			boolean downWallToRemove = false;

			int val = mazePanel.getCell(row, col);
			ArrayList<Integer> family = findCellsByValue(row, val);

			if (family.size() == 1) {
				// Un premier cas discriminant est celui où la case courante est la seule de la
				// ligne à posséder sa valeur. Cela signifie deux choses :
				// * La case est isolée des autres cases de la ligne par la droite et par la
				// gauche.
				// * Le chemin du dessus auquel est reliée la case est isolé du reste du
				// labyrinthe.
				// Dans cette situation, il faut obligatoirement briser le mur en dessous de la
				// case.
				downWallToRemove = true;
			} else if (col == family.get(family.size() - 1)) {
				// Le deuxième cas à distinguer est celui où la case courante correspond à la
				// dernière case du groupe. Si aucune cellule du groupe n'a été percée par le
				// bas, alors il faut obligatoirement briser le mur en bas de la cellule
				// courante pour éviter de créer un ilôt.
				boolean isIslet = true;
				for (int c : family)
					if (mazePanel.getWall(row, c, Side.DOWN) == 0) {
						isIslet = false;
						break;
					}

				if (isIslet)
					downWallToRemove = true;
			} else if (rand.nextBoolean() == false) {
				// Ces deux cas mis à part, l'algorithme choisit de retirer le mur du bas de
				// façon purement aléatoire.
				downWallToRemove = true;
			}

			if (downWallToRemove) {
				mazePanel.setWall(row, col, Side.DOWN, 0);
				mazePanel.setCell(row + 1, col, val);
			}
		}
	}

	protected ArrayList<Integer> findCellsByValue(int row, int value) {
		ArrayList<Integer> group = new ArrayList<Integer>();
		for (int c = 0; c < nbCol; c++)
			if (mazePanel.getCell(row, c) == value)
				group.add(c);

		return group;
	}

	private enum Mode {
		SOUTH_PROCESS, EAST_PROCESS;
	}
}