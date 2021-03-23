package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de rembobinage récursif</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme consiste à laisser un "explorateur" visiter
 * aléatoirement la grille jusqu'à ce qu'il en explore la totalité. Au fil de
 * l'exploration, les chemins qu'il emprunte constituent les chemins du
 * labyrinthe.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> On part d'une case choisie au hasard dans la grille
 * ; l'explorateur se ballade aléatoirement dans cette dernière et ne doit
 * passer que par des cases inexplorées. S'il se retrouve bloqué dans son
 * exploration i.e. s'il fait face au bord de la grille et/ou à des grilles déjà
 * explorées, l'explorateur doit faire machine arrière case par case, jusqu'à
 * voir une case inexplorée au bord de son chemin. Ce procédé se nomme
 * <b>rembobinage</b> (ou <i>backtracking</i> en anglais).
 * </p>
 * <p>
 * Ce procédé est récursif ; celui-ci prend fin lorsque l'explorateur est revenu
 * à son point de départ après rembobinage.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Pour un labyrinthe parfait (sans ilôts), l'explorateur
 * passe deux fois sur chaque case de la grille (à l'exception du bout des
 * impasses) et observe les 4 cases voisines. Le nombre d'étapes est donc
 * sensiblement égal à 4*2*M*N, d'où une complexité temporelle en O(N*M).
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class RecursiveBacktrackingAlgorithm extends MazeGenerationAlgorithm {
	/** Position initiale de l'explorateur dans la grille. */
	private int x0 /* ligne */, y0 /* colonne */;
	/** Chemin de l'explorateur sans prendre en compte les retours en arrière. */
	private List<int[]> directPath = new ArrayList<>();

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link RecursiveBacktrackingAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public RecursiveBacktrackingAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		// Initialisation de la grille ; toutes les cases sont marquées à 0 pour
		// indiquer que la case est inexplorée.
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 0);

		// Définition (aléatoire) du point de départ
		x0 = rand.nextInt(nbRow - 1);
		y0 = rand.nextInt(nbCol - 1);

		// Placement de l'explorateur sur ce point de départ
		directPath.add(new int[] { x0, y0 });
		mazePanel.getMaze().setCell(x0, y0, 1); // La case est maintenant explorée
	}

	@Override
	public boolean isComplete() {
		return directPath.isEmpty(); // L'explorateur a tout visité et est revenu à son point de départ.
	}

	@Override
	public void step() {
		// Repérage des directions à explorer
		List<WallCoord> unexplored = new ArrayList<>();

		int[] pos = directPath.get(directPath.size() - 1); // Position courante
		int x = pos[0], y = pos[1];

		List<WallCoord> sides = getSides(x, y);
		for (WallCoord side : sides) {
			int sideValue = mazePanel.getNeighbourCell(side);
			if (sideValue != -1 /* hors-grille, c'est une simple sécurité */
					&& sideValue != 1 /* case explorée */
					&& sideValue != 2 /* case explorée mais non coloriée */)
				unexplored.add(side);
		}

		// Phase d'exploration (ou de rembobinage)
		if (!unexplored.isEmpty()) {
			WallCoord selectedSide = unexplored.get(rand.nextInt(unexplored.size()));
			switch (selectedSide.side) {
			case UP:
				x--;
				break;
			case LEFT:
				y--;
				break;
			case DOWN:
				x++;
				break;
			case RIGHT:
				y++;
				break;
			default:
				break;
			}

			directPath.add(new int[] { x, y });
			mazePanel.setCell(x, y, 1); // Nouvelle case explorée
			// Brisage du mur
			mazePanel.setWall(selectedSide.x, selectedSide.y, selectedSide.side, 0);
		} else {
			mazePanel.setCell(x, y, 2); // Case marquée comme explorée (rembobinage)
			if (!directPath.isEmpty())
				directPath.remove(directPath.size() - 1);
		}
	}

	/**
	 * Fournit la direction vers l'ensemble des cellules voisines à la cellule
	 * spécifiée. Chaque direction est indiquée par un mur adjacent i.e. une
	 * instance {@link WallCoord}.
	 * 
	 * @param i Ligne de la cellule courante.
	 * @param j Colonne de la cellule courante.
	 * @return Liste des murs voisins à la cellule courante.
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
}