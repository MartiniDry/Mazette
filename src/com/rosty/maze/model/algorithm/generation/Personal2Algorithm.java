package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme personnel n°2</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme consiste à tracer des chemins linéaires à
 * travers la zone non-visitée de la grille (la ligne est découpée lorsque
 * celle-ci franchit une zone déjà visitée). Une fois toutes les cases visitées,
 * les cases ne sont pas toutes nécessairement reliées entre eux ; un algorithme
 * de Kruskal est lancé pour finaliser le regroupement des labyrinthes.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Tous les murs sont présents dans la grille de
 * départ et les cases sont marquées à 0 (inexplorées). L'algorithme se scinde
 * en deux grandes étapes : le tracé de chemins et le regroupement.<br/>
 * <ul>
 * <li><u>Tracé de chemins :</u> l'algorithme sélectionne deux cases
 * non-explorées de la grille et trace la ligne droite entre elles-deux jusqu'à
 * passer par une zone visitée de la grille (mode <b>DRAFT</b>). Une fois la
 * ligne identifiée, les murs sont brisés pour ouvrir le chemin et les cellules
 * sont marquées comme explorées (mode <b>DRAWING</b>). Le processus est réitéré
 * jusqu'à ce que toutes les cases de la grille soient explorées.<br/>
 * Dans le cas où il ne reste qu'une seule case à explorer à la fin,
 * l'algorithme brise l'un de ses murs au hasard (mode
 * <b>LAST_ONE_OUT</b>).</li>
 * <li><u>Regroupement :</u> à la fin du tracé des chemins, les cellules ne sont
 * pas nécessairement toutes reliées entre elles. Un exemple trivial est le cas
 * où l'algorithme ne trace que des chemins horizontaux dans la grille. Pour
 * contrer ce problème et finaliser le labyrinthe, le programme exécute un
 * algorithme de Kruskal.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Il est difficile d'estimer la complexité de cet
 * algorithme puisque celle-ci est liée aux points sélectionnés lors de l'étape
 * 1 et à la distance qui les sépare. Le fait que la ligne puisse être coupée
 * par une zone déjà visitée ajoute un biais important qui rend l'estimation de
 * la complexité très difficile.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class Personal2Algorithm extends MazeGenerationAlgorithm {
	private int[] A = null, B = null;
	private ArrayList<WallCoord> pathFromAtoB, pathFromBtoA;

	private Mode mode = Mode.DRAFT;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	public Personal2Algorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, 0);

		pathFromAtoB = new ArrayList<>();
		pathFromBtoA = new ArrayList<>();
	}

	@Override
	public boolean isComplete() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) != 3)
					return false;

		return true;
	}

	@Override
	public void step() {
		switch (mode) {
		case DRAFT:
			// Listage de toutes les cellules non encore explorées
			ArrayList<int[]> unexploredCells = new ArrayList<>();
			for (int i = 0; i < nbRow; i++)
				for (int j = 0; j < nbCol; j++)
					if (mazePanel.getCell(i, j) == 0)
						unexploredCells.add(new int[] { i, j });

			if (unexploredCells.isEmpty()) {
				identifyGroups();
				mode = Mode.GATHERING;
			} else if (unexploredCells.size() == 1) {
				int[] lastCell = unexploredCells.get(0);
				mazePanel.setCell(lastCell[0], lastCell[1], 1);
				mode = Mode.LAST_ONE_OUT;
			} else { // Equivalent à "size > 1"
				// Choix de deux cellules distincts, aléatoirement dans la liste.
				int ranStart = rand.nextInt(unexploredCells.size());
				A = unexploredCells.get(ranStart);
				unexploredCells.remove(ranStart);
				int ranEnd = rand.nextInt(unexploredCells.size());
				B = unexploredCells.get(ranEnd);

				// Esquisse du trait entre ces deux cellules
				draftLine();
				mode = Mode.DRAWING;
			}

			break;
		case DRAWING:
			drawLine();
			// Listage de toutes les cellules non encore explorées
			ArrayList<int[]> unexploredCellsList = new ArrayList<>();
			for (int i = 0; i < nbRow; i++)
				for (int j = 0; j < nbCol; j++)
					if (mazePanel.getCell(i, j) == 0)
						unexploredCellsList.add(new int[] { i, j });

			if (unexploredCellsList.isEmpty()) {
				identifyGroups();
				mode = Mode.GATHERING;
			} else {
				mode = Mode.DRAFT;
			}
			break;
		case LAST_ONE_OUT:
			int[] L = null;
			// Listage de toutes la cellule non-explorée
			rowLoop: for (int i = 0; i < nbRow; i++)
				for (int j = 0; j < nbCol; j++)
					if (mazePanel.getCell(i, j) == 1) {
						L = new int[] { i, j };
						break rowLoop;
					}

			ArrayList<WallCoord> sides = getSides(L[0], L[1]);
			WallCoord chosenWall = sides.get(rand.nextInt(sides.size()));
			mazePanel.setWall(chosenWall.x, chosenWall.y, chosenWall.side, 0);
			mazePanel.setCell(chosenWall.x, chosenWall.y, 2);

			identifyGroups();
			mode = Mode.GATHERING;
			break;
		case GATHERING:
			gatherGroups();
			identifyGroups();
			break;
		default:
			break;
		}
	}

	/**
	 * Identifie l'ensemble des groupes de cellules reliés entre eux. L'algorithme
	 * lit les cellules inexplorées de gauche à droite et de haut en bas.
	 */
	private void identifyGroups() {
		// Applatissement des cellules avant analyse (toutes les cellules sont définies
		// à une valeur de 2)
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, 2);

		int groupValue = 3; // Identifiant du groupe actuellement étudié

		int[] point = null;
		// Repérage du premier point à explorer pour identifier les groupes.
		rowLoop: for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) == 2) {
					point = new int[] { i, j };
					break rowLoop;
				}

		while (point != null) {
			// Exploration du groupe en partant de "point" ; toutes les cellules ont pour
			// valeur "groupValue".
			visit(point, groupValue);
			groupValue++; // Pour le groupe suivant, la valeur de groupe est incrémentée.

			// Repérage du prochain point inexploré ; si on en trouve un, celui-ci sera le
			// point de départ pour explorer un nouveau groupe.
			point = null;
			rowLoop: for (int i = 0; i < nbRow; i++)
				for (int j = 0; j < nbCol; j++)
					if (mazePanel.getCell(i, j) == 2) {
						point = new int[] { i, j };
						break rowLoop;
					}
		}
	}

	/**
	 * Utilise l'algorithme de Kruskal pour fusionner deux groupes de cellules dans
	 * la grille.
	 */
	private void gatherGroups() {
		// Listage des murs qui peuvent être enlevés
		ArrayList<WallCoord> walls = new ArrayList<>();
		for (int i = 0, lenI = nbRow - 1; i < lenI; i++) {
			for (int j = 0, lenJ = nbCol - 1; j < lenJ; j++) {
				WallCoord w = new WallCoord(i, j, Side.RIGHT);
				if (mazePanel.getCell(i, j) != mazePanel.getNeighbourCell(w))
					walls.add(w);
			}

			for (int j = 0; j < nbCol; j++) {
				WallCoord w = new WallCoord(i, j, Side.DOWN);
				if (mazePanel.getCell(i, j) != mazePanel.getNeighbourCell(w))
					walls.add(w);
			}
		}

		for (int j = 0, lenJ = nbCol - 1; j < lenJ; j++) {
			WallCoord w = new WallCoord(nbRow - 1, j, Side.RIGHT);
			if (mazePanel.getCell(nbRow - 1, j) != mazePanel.getNeighbourCell(w))
				walls.add(w);
		}

		// Retrait aléatoire de l'un des murs
		if (!walls.isEmpty()) {
			WallCoord selectedWall = walls.get(rand.nextInt(walls.size()));
			mazePanel.setWall(selectedWall.x, selectedWall.y, selectedWall.side, 0);
		}
	}

	/**
	 * Visite pas-à-pas les cellules adjacentes à une cellule spécifique ; celles-ci
	 * sont marquées d'un numéro identifiant le groupe que la fonction decouvre.
	 * 
	 * @param cell  Point de départ pour analyser le groupe.
	 * @param value Numéro de groupe.
	 */
	private void visit(int[] cell, int value) {
		mazePanel.setCell(cell[0], cell[1], value);

		ArrayList<WallCoord> sides1 = getSides(cell[0], cell[1]);
		List<WallCoord> sides = new ArrayList<WallCoord>();
		for (WallCoord wall : sides1)
			if (mazePanel.getWall(wall.x, wall.y, wall.side) == 0 && mazePanel.getNeighbourCell(wall) == 2)
				sides.add(wall);

		if (!sides.isEmpty())
			for (WallCoord w : sides) {
				int[] C = Arrays.copyOf(cell, cell.length);
				C = move(C, w.side);
				visit(C, value);
			}
	}

	/**
	 * RAPPEL : les coordonnées d'une cellule de la grille ne repèrent pas le centre
	 * mais le bord supérieur gauche. Cela ne changera rien au calcul mais il est
	 * important de le noter.
	 */
	private void draftLine() {
		pathFromAtoB.clear();
		pathFromBtoA.clear();

		/*
		 * Etape 1 : modélisation de la droite passant par les cellules "A" et "B",
		 * notées A et B par la suite. La droite est modélisée par l'équation
		 * "(xB-xA)*(y-yA) - (x-xA)*(yB-yA) = 0", où (x,y) est un point de la droite.
		 */

		double a = B[0] - A[0]; // a = yB - yA
		double b = B[1] - A[1]; // b = xB - xA

		// Calcul de la longueur entre A et B (sera utilisé dans l'étape 3)
		double len = Math.sqrt(a * a + b * b);

		/*
		 * Etape 2 : détermination de la direction vers laquelle parcourir cette droite
		 * pour aller respectivement de A vers B et de B vers A.
		 */

		ArrayList<Side> fromAtoB = new ArrayList<>();
		ArrayList<Side> fromBtoA = new ArrayList<>();

		if (a < 0) {
			fromAtoB.add(Side.UP);
			fromBtoA.add(Side.DOWN);
		} else if (a > 0) {
			fromAtoB.add(Side.DOWN);
			fromBtoA.add(Side.UP);
		}

		if (b < 0) {
			fromAtoB.add(Side.LEFT);
			fromBtoA.add(Side.RIGHT);
		} else if (b > 0) {
			fromAtoB.add(Side.RIGHT);
			fromBtoA.add(Side.LEFT);
		}

		/*
		 * Etape 3.1 : parcourir la droite case par case du point A vers le point B. A
		 * ce stade, "fromAtoB" et "fromBtoA" sont tous les 2 non-vides. A chaque étape,
		 * l'objectif est de trouver le point qui passe le plus près possible de la
		 * droite ; dès que l'on atteint une case déjà explorée, le parcours s'arrête.
		 */

		int[] M = Arrays.copyOf(A, A.length);
		boolean unexplored = true;
		while (!Arrays.equals(M, B) && unexplored) {
			mazePanel.setCell(M[0], M[1], 1);

			int[] _M = Arrays.copyOf(M, M.length);
			double dist = 0; // Distance entre le point (_x,_y) et la droite (AB)

			// Par défaut, le prochain point à parcourir est celui qui part dans la première
			// direction donnée. Voyons si l'on peut trouver mieux...
			int dirIndex = 0;
			_M = move(_M, fromAtoB.get(dirIndex));
			dist = Math.abs(b * (_M[0] - A[0]) - a * (_M[1] - A[1])) / len;
			for (int id = 1; id < fromAtoB.size(); id++) {
				// On repère les coordonnées du point situé dans la direction donnée
				_M = Arrays.copyOf(M, M.length);
				_M = move(_M, fromAtoB.get(id));
				// Si ce point est plus proche que le point choisi, alors on doit mettre à jour
				// "nextDirection".
				double newDist = Math.abs(b * (_M[0] - A[0]) - a * (_M[1] - A[1])) / len;
				if (newDist < dist) {
					dist = newDist;
					dirIndex = id;
				}
			}

			// Une fois la meilleure direction choisie, il ne reste plus qu'à déplacer le
			// point.
			WallCoord direction = new WallCoord(M[0], M[1], fromAtoB.get(dirIndex));
			pathFromAtoB.add(direction);
			M = move(M, fromAtoB.get(dirIndex));
			if (mazePanel.getCell(M[0], M[1]) != 0)
				unexplored = false;
		}

		/*
		 * Etape 3.2 : réitérer l'opération en parcourant la droite du point B vers le
		 * point A.
		 */

		M = Arrays.copyOf(B, B.length);
		unexplored = true;
		while (!Arrays.equals(M, A) && unexplored) {
			mazePanel.setCell(M[0], M[1], 1);

			int[] _M = Arrays.copyOf(M, M.length);
			double dist = 0; // Distance entre le point (_x,_y) et la droite (AB)

			// Par défaut, le prochain point à parcourir est celui qui part dans la première
			// direction donnée. Voyons si l'on peut trouver mieux...
			int dirIndex = fromBtoA.size() - 1;
			_M = move(_M, fromBtoA.get(dirIndex));
			dist = Math.abs(b * (_M[0] - A[0]) - a * (_M[1] - A[1])) / len;
			for (int id = dirIndex - 1; id >= 0; id--) {
				// On repère les coordonnées du point situé dans la direction donnée
				_M = Arrays.copyOf(M, M.length);
				_M = move(_M, fromBtoA.get(id));
				// Si ce point est plus proche que le point choisi, alors on doit mettre à jour
				// "nextDirection".
				double newDist = Math.abs(b * (_M[0] - A[0]) - a * (_M[1] - A[1])) / len;
				if (newDist < dist) {
					dist = newDist;
					dirIndex = id;
				}
			}

			// Une fois la meilleure direction choisie, il ne reste plus qu'à déplacer le
			// point.
			WallCoord direction = new WallCoord(M[0], M[1], fromBtoA.get(dirIndex));
			pathFromBtoA.add(direction);
			M = move(M, fromBtoA.get(dirIndex));
			if (mazePanel.getCell(M[0], M[1]) != 0)
				unexplored = false;
		}
	}

	/**
	 * Trace la ligne esquissée entre A et B. Plus précisément, les murs enregistrés
	 * dans les listes "pathFromAtoB" et "pathFromBtoA" sont retirés.
	 */
	private void drawLine() {
		if (!pathFromAtoB.isEmpty()) {
			int index = 0;
			boolean explored = false;
			while (index < pathFromAtoB.size() && !explored) {
				WallCoord wall = pathFromAtoB.get(index);
				if (mazePanel.getCell(wall.x, wall.y) != 2) {
					mazePanel.setCell(wall.x, wall.y, 2);
					mazePanel.setWall(wall.x, wall.y, wall.side, 0);
				} else
					explored = true;

				index++;
			}
		}

		if (!pathFromBtoA.isEmpty()) {
			int index = 0;
			boolean explored = false;
			while (index < pathFromBtoA.size() && !explored) {
				WallCoord wall = pathFromBtoA.get(index);
				if (mazePanel.getCell(wall.x, wall.y) != 2) {
					mazePanel.setCell(wall.x, wall.y, 2);
					mazePanel.setWall(wall.x, wall.y, wall.side, 0);
				} else
					explored = true;

				index++;
			}
		}
	}

	/**
	 * Fournit la liste des murs adjacents à la cellule spécifiée (la méthode ne
	 * prend pas en compte les murs situés à la périphérie de la grille).
	 * 
	 * @param i Ligne de la cellule courante.
	 * @param j Colonne de la cellule courante.
	 * @return Liste d'instance {@link WallCoord}}.
	 */
	private ArrayList<WallCoord> getSides(int i, int j) {
		ArrayList<WallCoord> sides = new ArrayList<>();
		if (i > 0)
			sides.add(new WallCoord(i, j, Side.UP));

		if (j > 0)
			sides.add(new WallCoord(i, j, Side.LEFT));

		if (i < nbRow - 1)
			sides.add(new WallCoord(i, j, Side.DOWN));

		if (j < nbCol - 1)
			sides.add(new WallCoord(i, j, Side.RIGHT));

		return sides;
	}

	/**
	 * Déplace une cellule spécifique d'une case dans la direction donnée.
	 * 
	 * @param point Point représentant une cellule dnas la grille.
	 * @param dir   Direction du déplacement.
	 */
	private int[] move(int[] point, Side dir) {
		if (point.length == 2) {
			switch (dir) {
			case UP:
				return new int[] { point[0] - 1, point[1] };
			case LEFT:
				return new int[] { point[0], point[1] - 1 };
			case DOWN:
				return new int[] { point[0] + 1, point[1] };
			case RIGHT:
				return new int[] { point[0], point[1] + 1 };
			default:
				return null;
			}
		} else
			return null;
	}

	/**
	 * Enumération des différents modes d'action de l'algorithme.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	enum Mode {
		DRAFT, DRAWING, LAST_ONE_OUT, GATHERING
	}
}