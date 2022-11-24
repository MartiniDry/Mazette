package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de suivi du mur</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme explore le labyrinthe en suivant la paroi
 * situé à côté d'elle (à droite par défaut). Le chemin est tracé case par case
 * jusqu'à atteindre le point d'arrivée ; en cas d'impasse, l'algorithme "fait
 * rebrousse-chemin".
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Au point de départ, l'algorithme sélectionne le mur
 * qui sera suivi jusqu'à la fin (ce premier mur est choisi aléatoirement). A
 * chaque étape, l'algorithme récupère la direction précédente et en déduit le
 * chemin le plus à droite qu'il peut sélectionner pour en déduire la nouvelle
 * direction de déplacement. En cas d'impasse, le chemin le plus à droite sera
 * le sens opposé, auquel cas l'algorithme rebrousse chemin jusqu'à la prochaine
 * cellule inexplorée.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Le critère de performance dépend directement de la taille
 * totale du labyrinthe. Empiriquement, le nombre de cases visitées correspond à
 * la moitié des cases du labyrinthe ; avec une étape de calcul en O(1), la
 * complexité temporelle est donc O(M*N). La complexité mémoire est quant à elle
 * en O(1) ; il ne suffit que de voir les murs autour de la case et de connaître
 * la direction actuelle pour avancer.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class WallFollowingAlgorithm extends MazeSolvingAlgorithm {
	private int cur_i, cur_j; // Current position
	private Side lastDirection = null;

	public WallFollowingAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return "wall_following";
	}

	@Override
	public void init() {
		for (int i = 0; i < mazePanel.getMaze().getNbRows(); i++)
			for (int j = 0; j < mazePanel.getMaze().getNbColumns(); j++)
				mazePanel.setCell(i, j, 0);

		int[] start = mazePanel.getStart();
		cur_i = start[0];
		cur_j = start[1];
		mazePanel.setCell(cur_i, cur_j, 1);
		mazePanel.getPath().add(new int[] { cur_i, cur_j });
	}

	@Override
	public boolean isComplete() {
		int[] end = mazePanel.getEnd();

		return cur_i == end[0] && cur_j == end[1];
	}

	@Override
	public void step() {
		Side sideToGo = rightestWall();
		move(sideToGo);
		lastDirection = sideToGo;

		if (mazePanel.getPath().size() >= 2) {
			int[] anteCell = mazePanel.getPath().get(mazePanel.getPath().size() - 2);
			if (anteCell[0] == cur_i && anteCell[1] == cur_j)
				mazePanel.getPath().remove(mazePanel.getPath().size() - 1);
			else
				mazePanel.getPath().add(new int[] { cur_i, cur_j });
		} else
			mazePanel.getPath().add(new int[] { cur_i, cur_j });

		if (mazePanel.getCell(cur_i, cur_j) != 1)
			mazePanel.setCell(cur_i, cur_j, 1);
	}

	private Side rightestWall() {
		ArrayList<Side> sides = getSides(cur_i, cur_j);

		// Lors de la première étape (lastDirection non-défini), un mur est choisi au
		// hasard ; c'est celui-ci qui sera suivi durant l'exécution de l'algorithme.
		if (lastDirection == null) {
			Random ran = new Random();
			return sides.get(ran.nextInt(sides.size()));
		}

		// Utilisation de la direction opposée à lastDirection
		Side wall = null;
		switch (lastDirection) {
			case UP:
				wall = Side.DOWN;
				break;
			case DOWN:
				wall = Side.UP;
				break;
			case LEFT:
				wall = Side.RIGHT;
				break;
			case RIGHT:
				wall = Side.LEFT;
				break;
		}

		// Incrémentation cyclique de l'index
		int id = sides.indexOf(wall);
		id = (id + 1) % sides.size();

		return sides.get(id);
	}

	/**
	 * Fournit la liste des directions accessibles pour la cellule spécifiée (la
	 * méthode ne prend pas en compte les murs situés à la périphérie de la grille).
	 * 
	 * @param i Ligne de la cellule courante.
	 * @param j Colonne de la cellule courante.
	 * @return Liste d'instance {@link Side}}.
	 */
	private ArrayList<Side> getSides(int i, int j) {
		ArrayList<Side> sides = new ArrayList<>();
		if (i > 0 && mazePanel.getWall(i, j, Side.UP) != 1)
			sides.add(Side.UP);

		if (j > 0 && mazePanel.getWall(i, j, Side.LEFT) != 1)
			sides.add(Side.LEFT);

		if (i < nbRow - 1 && mazePanel.getWall(i, j, Side.DOWN) != 1)
			sides.add(Side.DOWN);

		if (j < nbCol - 1 && mazePanel.getWall(i, j, Side.RIGHT) != 1)
			sides.add(Side.RIGHT);

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
				cur_i--;
				break;
			case LEFT:
				cur_j--;
				break;
			case DOWN:
				cur_i++;
				break;
			case RIGHT:
				cur_j++;
				break;
			default:
				break;
		}
	}
}