package com.rosty.util.maze;

import java.util.ArrayList;

import com.rosty.maze.model.Maze;
import com.rosty.maze.model.Maze.Side;

/**
 * Classe utilitaire dédiée aux opérations de plus haut niveau sur les
 * labyrinthes.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class MazeUtils {
	/**
	 * Détermine le nombre d'enclos dans le labyrinthe spécifié.
	 * <p>
	 * <h2>Définition</h2> Un <b>enclos</b> est un ensemble de cellules reliées
	 * entre elles. Dans un enclos, il est possible d'aller d'une cellule à une
	 * autre via un ou plusieurs chemins.
	 * </p>
	 */
	public static int enclosures(Maze maze) {
		Maze copy = new Maze(maze);
		int addedTokens = 0, deletedTokens = 0;

		for (int i = 0; i < copy.getNbRows(); i++) {
			for (int j = 0; j < copy.getNbColumns(); j++) {
				int up = copy.getWall(i, j, Side.UP);
				int left = copy.getWall(i, j, Side.LEFT);

				if (i == 0 && j == 0) {
					copy.setCell(i, j, ++addedTokens);
				} else if (i == 0) {
					if (left == 1)
						copy.setCell(i, j, ++addedTokens);
					else
						copy.setCell(i, j, copy.getCell(i, j - 1));
				} else if (j == 0) {
					if (up == 1)
						copy.setCell(i, j, ++addedTokens);
					else
						copy.setCell(i, j, copy.getCell(i - 1, j));
				} else {
					int upCell = copy.getCell(i - 1, j);
					int leftCell = copy.getCell(i, j - 1);

					if (up != 1 && left != 1 && upCell != leftCell) {
						copy.setCell(i, j, upCell);
						for (int k = 0; k < j; k++)
							if (copy.getCell(i, k) == leftCell)
								copy.setCell(i, k, upCell);

						deletedTokens++;
					} else if (up != 1)
						copy.setCell(i, j, upCell);
					else if (left != 1)
						copy.setCell(i, j, leftCell);
					else
						copy.setCell(i, j, ++addedTokens);
				}
			}
		}

		return addedTokens - deletedTokens;
	}

	/**
	 * Version améliorée de la fonction {@link MazeUtils#enclosures(Maze)} qui
	 * mémorise les données de l'algorithme dans un buffer de la taille d'une ligne.
	 * On passe ainsi d'une complexité mémoire de O(N²) à O(N), avec ua même
	 * complexité temporelle.
	 */
	public static int enclosures_light(Maze maze) {
		int[] buffer = new int[maze.getNbColumns()];
		int cursor = 0;
		int addedTokens = 0, deletedTokens = 0;

		for (int i = 0, lenI = maze.getNbRows(); i < lenI; i++) {
			for (int j = 0, lenJ = maze.getNbColumns(); j < lenJ; j++) {
				int up = maze.getWall(i, j, Side.UP);
				int left = maze.getWall(i, j, Side.LEFT);

				if (i == 0 && j == 0) {
					cursor = ++addedTokens;
				} else if (i == 0) {
					if (left == 1)
						cursor = ++addedTokens;
					else
						cursor = buffer[j - 1];
				} else if (j == 0) {
					if (up == 1)
						cursor = ++addedTokens;
					else
						cursor = buffer[j];
				} else {
					int upCell = buffer[j];
					int leftCell = buffer[j - 1];

					if (up != 1 && left != 1 && upCell != leftCell) {
						// cursor = upCell; // Useless to do in this case
						for (int k = 0; k < j; k++)
							if (buffer[k] == leftCell)
								buffer[k] = upCell;

						deletedTokens++;
					} else if (up != 1)
						cursor = upCell;
					else if (left != 1)
						cursor = leftCell;
					else
						cursor = ++addedTokens;
				}

				buffer[j] = cursor;
			}
		}

		return addedTokens - deletedTokens;
	}

	/**
	 * Version améliorée de la fonction {@link MazeUtils#enclosures(Maze)} qui
	 * mémorise les données de l'algorithme dans un buffer de la taille d'une ligne.
	 * On passe ainsi d'une complexité mémoire de O(N²) à O(N), avec ua même
	 * complexité temporelle.
	 */
	public static int enclosures_lighter(Maze maze) {
		int[] buffer = new int[maze.getNbColumns()];
		int addedTokens = 0, deletedTokens = 0;

		// Step 1: treating first cell
		buffer[0] = ++addedTokens;

		// Step 2: treating the rest of the first line
		for (int j = 1, lenJ = maze.getNbColumns(); j < lenJ; j++)
			buffer[j] = (maze.getWall(0, j, Side.LEFT) == 1) ? ++addedTokens : buffer[j - 1];

		// Step 3: repeating the process for other lines
		for (int i = 1, lenI = maze.getNbRows(); i < lenI; i++) {
			// Step 3.1: treating the left cell
			if (maze.getWall(i, 0, Side.UP) == 1)
				buffer[0] = ++addedTokens;

			// Step 3.2: treating the rest of the line
			for (int j = 1, lenJ = maze.getNbColumns(); j < lenJ; j++) {
				int upCell = buffer[j];
				int leftCell = buffer[j - 1];
				int up = maze.getWall(i, j, Side.UP);
				int left = maze.getWall(i, j, Side.LEFT);

				if (up != 1 && left != 1 && upCell != leftCell) {
					for (int k = 0; k < j; k++)
						if (buffer[k] == leftCell)
							buffer[k] = upCell;

					deletedTokens++;
				} else if (up != 1)
					buffer[j] = upCell;
				else if (left != 1)
					buffer[j] = leftCell;
				else
					buffer[j] = ++addedTokens;
			}
		}

		return addedTokens - deletedTokens;
	}

	/**
	 * Version améliorée de la fonction {@link MazeUtils#enclosures(Maze)} qui
	 * mémorise les données de l'algorithme dans un buffer de la taille d'une ligne.
	 * On passe ainsi d'une complexité mémoire de O(N²) à O(N), avec ua même
	 * complexité temporelle.
	 */
	public static int enclosures_alternative_lighter(Maze maze) {
		ArrayList<Integer> buffer = new ArrayList<>();
		for (int j = 0; j < maze.getNbColumns(); j++)
			buffer.add(0);

		int addedTokens = 0, deletedTokens = 0;

		// Step 1: treating first cell
		buffer.set(0, ++addedTokens);

		// Step 2: treating the rest of the first line
		for (int j = 1, lenJ = maze.getNbColumns(); j < lenJ; j++)
			buffer.set(j, (maze.getWall(0, j, Side.LEFT) == 1) ? ++addedTokens : buffer.get(j - 1));

		// Step 3: repeating the process for other lines
		for (int i = 1, lenI = maze.getNbRows(); i < lenI; i++) {
			// Step 3.1: treating the left cell
			if (maze.getWall(i, 0, Side.UP) == 1)
				buffer.set(0, ++addedTokens);

			// Step 3.2: treating the rest of the line
			for (int j = 1, lenJ = maze.getNbColumns(); j < lenJ; j++) {
				int upCell = buffer.get(j);
				int leftCell = buffer.get(j - 1);
				int up = maze.getWall(i, j, Side.UP);
				int left = maze.getWall(i, j, Side.LEFT);

				if (up != 1 && left != 1 && upCell != leftCell) {
					for (int k = 0; k < j; k++)
						if (buffer.get(k) == leftCell)
							buffer.set(k, upCell);

					deletedTokens++;
				} else if (up != 1)
					buffer.set(j, upCell);
				else if (left != 1)
					buffer.set(j, leftCell);
				else
					buffer.set(j, ++addedTokens);
			}
		}

		return addedTokens - deletedTokens;
	}

	/**
	 * Version améliorée de la fonction {@link MazeUtils#enclosures(Maze)} qui
	 * mémorise les données de l'algorithme dans un buffer de la taille d'une ligne.
	 * On passe ainsi d'une complexité mémoire de O(N²) à O(N), avec ua même
	 * complexité temporelle.
	 */
	public static int enclosures_lightest(Maze maze) {
		int[] buffer = new int[maze.getNbColumns()];
		int addedTokens = 0, deletedTokens = 0;

		// Step 1: treating first cell
		buffer[0] = ++addedTokens;

		// Step 2: treating the rest of the first line
		for (int j = 1, lenJ = maze.getNbColumns(); j < lenJ; j++)
			buffer[j] = (maze.getWall(0, j, Side.LEFT) == 1) ? ++addedTokens : buffer[j - 1];

		// Step 3: repeating the process for other lines
		for (int i = 1, lenI = maze.getNbRows(); i < lenI; i++) {
			// Step 3.1: treating the left cell
			if (maze.getWall(i, 0, Side.UP) == 1)
				buffer[0] = ++addedTokens;

			// Step 3.2: treating the rest of the line
			for (int j = 1, lenJ = maze.getNbColumns(); j < lenJ; j++) {
				int up = maze.getWall(i, j, Side.UP);
				int left = maze.getWall(i, j, Side.LEFT);

				if (up == 1)
					buffer[j] = (left == 1) ? ++addedTokens : buffer[j - 1];
				else {
					int upCell = buffer[j];
					int leftCell = buffer[j - 1];

					if (left == 1 || upCell == leftCell)
						buffer[j] = upCell;
					else {
						for (int k = 0; k < j; k++)
							if (buffer[k] == leftCell)
								buffer[k] = upCell;

						deletedTokens++;
					}
				}
			}
		}

		return addedTokens - deletedTokens;
	}

	/**
	 * Détermine le nombre d'ilôts dans le labyrinthe spécifié.
	 * <p>
	 * <h2>Définition</h2> Un <b>ilôt</b> est un ensemble de murs et de jonctions de
	 * mur reliés entre eux, qui ne sont pas reliés au bord du labyrinthe. Par
	 * conséquent, si l'on sélectionne une case voisine de l'ilôt, il est possible
	 * de tracer un chemin qui fait tout le tour de cette dernière.<br />
	 * <u>ATTENTION:</u> si l'on considère un carré de 2 cases par 2 qui ne contient
	 * aucun mur, alors la jonction entre les cases constitue un ilôt.
	 * </p>
	 * <p>
	 * <h2>Propriété</h2> Le nombre d'ilôts peut aisément être déduit du nombre
	 * d'enclos par la formule suivante :<br />
	 * <code>O = P + I - (E-1)</code> , où :
	 * <ul>
	 * <li><b>O</b> est le nombre d'ouvertures dans la grille</li>
	 * <li><b>P</b> est le nombre d'ouvertures dans un labyrinthe parfait i.e.
	 * <code>P=col*row-1</code></li>
	 * <li><b>I</b> est le nombre d'ilôts</li>
	 * <li><b>E</b> est le nombre d'enclos.</li>
	 * </ul>
	 * En effet, quelque soit le labyrinthe étudié, il est possible de le
	 * transformer en un labyrinthe parfait en ajoutant et en retirant des murs.
	 * Retirer un mur autour d'un enclos réduit de 1 le nombre total d'enclos ; de
	 * même, ajouter un mur à un ilôt va faire disparaître ce dernier. Partant de ce
	 * principe, il faut ajouter <code>I</code> murs et en enlever <code>E-1</code>
	 * pour obtenir un labyrinthe parfait. Le décompte des murs ouverts, avant et
	 * après le processus, nous fournit la formule.
	 * </p>
	 */
	public static int islets(Maze maze) {
		int r = maze.getNbRows(), c = maze.getNbColumns();

		int overEnclosures = enclosures(maze) - 1;

		int openings = 0;
		for (int i = 1; i < r; i++)
			for (int j = 0; j < c; j++)
				if (maze.getWall(i, j, Side.UP) != 1)
					openings++;

		for (int i = 0; i < r; i++)
			for (int j = 1; j < c; j++)
				if (maze.getWall(i, j, Side.LEFT) != 1)
					openings++;

		int openingsForPerfectMaze = r * c - 1;

		return overEnclosures + openings - openingsForPerfectMaze;
	}

	/**
	 * Détermine si le labyrinthe spécifié est connexe.
	 * <p>
	 * <h2>Définition</h2> Un labyrinthe est <b>connexe</b> si l'on peut tracer un
	 * chemin entre deux cases quelconques du labyrinthe.
	 * </p>
	 * <p>
	 * <h2>Propriété</h2> Un labyrinthe est connexe si et seulement s'il est
	 * constitué d'un seul et unique enclos. Le résultat se déduit aisément en
	 * observant les définitions.
	 * </p>
	 */
	public static boolean isConnected(Maze maze) {
		return (enclosures(maze) == 1);
	}

	/**
	 * Détermine si le labyrinthe spécifié est un labyrinthe parfait.
	 * <p>
	 * <h2>Définition</h2> Un labyrinthe est dit <b>parfait</b> si l'on peut relier
	 * deux cases quelconques avec un seul et unique chemin.
	 * </p>
	 * <p>
	 * <h2>Propriété</h2> Un labyrinthe est parfait si et seulement s'il est connexe
	 * et sans ilôt.
	 * <ul>
	 * <li><u>Condition nécessaire :</u> si un labyrinthe est parfait, alors il est
	 * obligatoirement connexe. La propriété sur les ilôts peut être obtenue par
	 * contraposition. Supposons qu'il existe un ilôt dans le labyrinthe ; on sait
	 * qu'il existe au moins deux cases distinctes qui se trouvent à son bord. Si
	 * l'on sélectionne deux de ces cases, alors on peut les relier grâce à deux
	 * chemins différents (par la gauche et par la droite de l'ilôt), ce qui
	 * implique que le labyrinthe n'est pas parfait.</li>
	 * <li><u>Condition suffisante :</u> faisons un raisonnement par l'absurde.
	 * Supposons un labyrinthe parfait possédant l'une des propriétés suivantes :
	 * <ol>
	 * <li>Si le labyrinthe n'est pas connexe, alors il existe au moins deux enclos.
	 * Il est impossible de tracer un chemin entre deux cases venant de deux enclos
	 * différents, ce qui est ABSURDE puisque le labyrinthe est parfait.</li>
	 * <li>Si le labyrinthe possède au moins un ilôt, alors il est possible de
	 * trouver deux cases distinctes se trouvant à son bord. Comme écrit
	 * précédemment, on peut relier ces deux cases grâce à deux chemins différents,
	 * ce qui est ABSURDE puisque le labyrinthe est parfait.</li>
	 * </ol>
	 * </li>
	 * </ul>
	 * </p>
	 */
	public static boolean isPerfect(Maze maze) {
		return (isConnected(maze) && islets(maze) == 0);
	}
}