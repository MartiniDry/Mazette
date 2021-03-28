package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class WilsonAlgorithm extends MazeGenerationAlgorithm {
	/** Portion de chemin parcourue par l'explorateur. */
	private List<WallCoord> partialPath = new ArrayList<>();
	/** Position courante de l'explorateur dans la grille. */
	private int x = -1 /* ligne */, y = -1 /* colonne */;
	/** Point d'arrivée de l'explorateur qui définit un chemin du labyrinthe. */
	private int x0 /* ligne */, y0 /* colonne */;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link WilsonAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public WilsonAlgorithm(MazePanel panel) {
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

		// Le point de départ est exploré.
		mazePanel.setCell(x0, y0, 2);
	}

	@Override
	public boolean isComplete() {
		if (!partialPath.isEmpty())
			return false;

		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) == 0)
					return false;

		return true;
	}

	@Override
	public void step() {
		if (partialPath.isEmpty()) {
			if (x == -1 && y == -1) { // Si l'on est au tout début de l'algorithme, ...
				// ...alors le point de départ est choisi au hasard (mais différent de (x0,y0)).
				do {
					x = rand.nextInt(nbRow - 1);
					y = rand.nextInt(nbCol - 1);
				} while (x == x0 && y == y0);
			} else {
				// Sinon on choisit la cellule vierge la plus en haut à gauche de la grille.
				boolean found = false;
				
				rowLoop:
				for (int i = 0; i < nbRow; i++)
					for (int j = 0; j < nbCol; j++)
						if (mazePanel.getCell(i, j) == 0) {
							x = i;
							y = j;
							found = true;
							break rowLoop;
						}

				if (!found) // Si aucune case inexplorée n'est présente, ...
					return; // ...alors l'algorithme de Wilson est terminé.
			}

			// A ce stade, on a défini une nouvelle coordonnée (x,y).
			ArrayList<WallCoord> sides = getSides(x, y);
			WallCoord direction = sides.get(rand.nextInt(sides.size()));
			partialPath.add(direction);
			mazePanel.setCell(x, y, 1);
		} else {
			WallCoord lastDirection = partialPath.get(partialPath.size() - 1);
			move(lastDirection.side);

			if (mazePanel.getCell(x, y) == 0) {
				mazePanel.setCell(x, y, 1);
				ArrayList<WallCoord> sides = getSides(x, y);
				WallCoord newDirection = sides.get(rand.nextInt(sides.size()));
				partialPath.add(newDirection);
			} else if (mazePanel.getCell(x, y) == 1) {
				int index = partialPath.size() - 1;
				WallCoord dir = lastDirection;
				do {
					partialPath.remove(index--);
					mazePanel.setCell(dir.x, dir.y, 0);
					if (index >= 0) {
						dir = partialPath.get(index);
					}
				} while (dir.x != x || dir.y != y);

				mazePanel.setCell(x, y, 1);
			} else if (mazePanel.getCell(x, y) == 2) {
				for (WallCoord wall : partialPath) {
					mazePanel.setWall(wall.x, wall.y, wall.side, 0);
					mazePanel.setCell(wall.x, wall.y, 2);
				}

				partialPath.clear();
			}
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

	/**
	 * Déplace la cellule courante d'une case dans la direction donnée.
	 * 
	 * @param direction Direction du déplacement.
	 */
	private void move(Side direction) {
		switch (direction) {
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
	}
}