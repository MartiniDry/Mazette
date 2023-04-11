package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Lee</h1>
 * 
 * <p>
 * <h2>Principe</h2> Il s'agit de l'un des algorithmes de résolution les plus
 * simples à imaginer sur papier. Le parcours se fait en associant aux cellules
 * un coût qui représente le nombre de mouvements nécessaires pour atteindre le
 * point de départ. Par opposition à l'algorithme de Prim, le parcours se fait
 * en largeur (BFS pour "<i>breadth-first search</i>").
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Le parcours de l'algorithme se fait récursivement ;
 * partant du départ, on identifie toutes les cellules voisines inexplorées, on
 * leur associe un coût incrémenté puis on réitère en partant de cette nouvelle
 * liste. La liste des cellules marquées lors d'une itération s'appelle la
 * "peau". A chaque itération, on accumule les "peaux" jusqu'à atteindre le
 * point d'arrivée.<br/>
 * L'algorithme n'utilise pas d'arbre de données pour parcourir la grille. Le
 * chemin entre le départ et l'arrivée se fait en parcourant le labyrinthe en
 * sens inverse, pas-à-pas en décrémentant le coût.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Comme tout le monde ; O(MN) dans le pire des cas.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class LeeAlgorithm extends MazeSolvingAlgorithm {
	/**
	 * Coût de l'exploration, représentant le nombre de cases parcourues entre le
	 * départ et la cellule actuellement explorée.
	 */
	private int cost = 1;

	/**
	 * Liste des cellules pondérées par le poids courant. De par la nature de
	 * l'algorithme (BFS), la liste représente une "peau" entre les zones explorée
	 * et inexplorée.
	 */
	private ArrayList<int[]> cellSkin = new ArrayList<>();

	/**
	 * Constructeur de la classe {@link LeeAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public LeeAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".lee";
	}

	@Override
	public void init() {
		/** Etape 1 : vidage du terrain */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, 0);

		/** Etape 2 : formation de la "peau" */
		int[] start = mazePanel.getStart();
		mazePanel.setCell(start[0], start[1], cost);
		cellSkin.add(start);
	}

	@Override
	public boolean isComplete() {
		int[] end = mazePanel.getEnd();

		return mazePanel.getCell(end[0], end[1]) != 0;
	}

	@Override
	public void step() {
		// Formation de la nouvelle "peau" en incrémentant le coût
		cost++;
		ArrayList<int[]> newSkin = new ArrayList<>();

		// Les cellules de la nouvelle "peau" sont inexplorées et distantes d'un pas
		// avec les cellules de la "peau" actuelle.
		for (int[] cell : cellSkin) {
			int i = cell[0], j = cell[1];
			if (i > 0 && mazePanel.getWall(i, j, Side.UP) != 1 && mazePanel.getCell(i - 1, j) == 0)
				newSkin.add(new int[] { i - 1, j });

			if (j > 0 && mazePanel.getWall(i, j, Side.LEFT) != 1 && mazePanel.getCell(i, j - 1) == 0)
				newSkin.add(new int[] { i, j - 1 });

			if (i < nbRow - 1 && mazePanel.getWall(i, j, Side.DOWN) != 1 && mazePanel.getCell(i + 1, j) == 0)
				newSkin.add(new int[] { i + 1, j });

			if (j < nbCol - 1 && mazePanel.getWall(i, j, Side.RIGHT) != 1 && mazePanel.getCell(i, j + 1) == 0)
				newSkin.add(new int[] { i, j + 1 });
		}

		// La nouvelle "peau" est marquée et sauvegardée pour la prochaine itération.
		cellSkin.clear();
		cellSkin.addAll(newSkin);
		for (int[] cell : newSkin)
			mazePanel.setCell(cell[0], cell[1], cost);
	}

	@Override
	public void finish() {
		super.finish();

		int[] cell = mazePanel.getEnd();
		mazePanel.getRoute().getPath().add(cell);
		int rCost = cost - 1; // "Reverted cost" ou "coût renversé"
		while (rCost > 0) {
			int i = cell[0], j = cell[1];
			if (i > 0 && mazePanel.getWall(i, j, Side.UP) != 1 && mazePanel.getCell(i - 1, j) == rCost)
				cell = new int[] { i - 1, j };
			else if (j > 0 && mazePanel.getWall(i, j, Side.LEFT) != 1 && mazePanel.getCell(i, j - 1) == rCost)
				cell = new int[] { i, j - 1 };
			else if (i < nbRow - 1 && mazePanel.getWall(i, j, Side.DOWN) != 1 && mazePanel.getCell(i + 1, j) == rCost)
				cell = new int[] { i + 1, j };
			else if (j < nbCol - 1 && mazePanel.getWall(i, j, Side.RIGHT) != 1 && mazePanel.getCell(i, j + 1) == rCost)
				cell = new int[] { i, j + 1 };

			mazePanel.getRoute().getPath().add(0, cell);
			rCost--;
		}
	}
}