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
	private List<int[]> partialPath = new ArrayList<>();
	/** Position courante de l'explorateur dans la grille. */
	private int x /* ligne */, y /* colonne */;
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
	}

	@Override
	public boolean isComplete() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getMaze().get(i, j) != 2)
					return false;
		
		return true;
	}

	@Override
	public void step() {
		if (partialPath.isEmpty()) {
			;
		} else {
			;
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