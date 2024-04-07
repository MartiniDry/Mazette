package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Pledge</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme est une version améliorée de l'algorithme de
 * suivi du mur (<i>wall-following</i>), qui peut poser problème lorsque le mur
 * est isolé dans le labyrinthe. Pour que l'explorateur ne tourne pas en rond,
 * l'algorithme suggère de "compter" les virages effectués (+1 dans une
 * direction, -1 dans la direction opposée) et de partir tout droit lorsque la
 * compteur repasse à 0. De cette manière, on s'assure de suivre un autre mur
 * jusqu'à trouver celui qui mène à la sortie.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'explorateur commence par avancer tout droit dans
 * le labyrinthe ; le compteur de virages est à 0. Lorsqu'il fait face à un mur,
 * il le suit en y posant sa main (gauche ou droite mais ce sera la même main
 * pendant toute l'exécution). Le compteur monte de 1 lorsque l'on tourne de 90°
 * vers la droite et descend de 1 vers la gauche. On suit ce principe tout en
 * suivant le mur. Lorsque le compteur change de signe ou atteint 0, on revient
 * au comportement d'origine ; l'explorateur avance tout droit et le compteur
 * est remis à 0.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> A calculer.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class PledgeAlgorithm extends MazeSolvingAlgorithm {
	/** Coordonnées de l'explorateur dans la grille. */
	private int[] explorer = new int[2];
	/** Path saved for the algorithm. */
	ArrayList<int[]> path = new ArrayList<int[]>();
	/** Direction vers laquelle débute l'explorateur. */
	private Side orientation = Side.RIGHT;
	/**
	 * Décompte des virages effectués par l'explorateur du côté de sa main ; si
	 * l'explorateur a sa main posée sur le mur de gauche, le compteur monte de 1
	 * s'il tourne à gauche et descend de 1 s'il tourne à droite.
	 */
	private int turnCount = 0;

	/** Booléen indiquant si l'explorateur avance tout droit ou suit le mur. */
	private boolean goStraight = true;
	/** Booléen indiquant si l'explorateur suit le mur de gauche ou de droite. */
	private boolean handOnLeft = true;

	/**
	 * Constructeur de la classe {@link PledgeAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public PledgeAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".pledge";
	}

	@Override
	public void init() {
		/** Etape 1 : vidage du terrain */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, 0);

		/** Etape 2 : début de l'exploration */
		explorer = mazePanel.getStart();
		mazePanel.setCell(explorer[0], explorer[1], 2);
		path.add(explorer.clone());
	}

	@Override
	public boolean isComplete() {
		return Arrays.equals(explorer, mazePanel.getEnd());
	}

	@Override
	public void step() {
		if (goStraight) { // Si l'explorateur doit avancer tout droit ...
			if (mazePanel.getWall(explorer[0], explorer[1], orientation) != 1) { // ...et que la voie est libre, ...
				move(); // ...alors avancer d'une case.

				// Marquage de la case et mise à jour du chemin
				mazePanel.setCell(explorer[0], explorer[1], 2);
				if (has(path, explorer))
					removeAfter(path, explorer);
				else
					path.add(explorer.clone());
			} else { // Si l'explorateur est face à un mur, ...
				goStraight = false; // ...poser sa main dessus et regarder dans la direction du mur.
				orientation = handOnLeft ? lookToTheRight(orientation) : lookToTheLeft(orientation);
			}
		} else { // Si l'explorateur doit avancer en suivant le mur, ...
			int oldTurnCount = turnCount;
			orientation = whereToTurn(); // ...chercher la direction vers laquelle part le mur ...
			move(); // ...et y avancer d'une case.
			if (turnCount == 0 || oldTurnCount * turnCount < 0) { // Si le compteur de virages bascule, ...
				goStraight = true; // ...alors le comportement de l'explorateur change ...
				turnCount = 0; // ...et le compteur est réinitialisé.
			}

			// Marquage de la case et mise à jour du chemin
			mazePanel.setCell(explorer[0], explorer[1], 2);
			if (has(path, explorer))
				removeAfter(path, explorer);
			else
				path.add(explorer.clone());
		}
	}

	@Override
	public void finish() {
		super.finish();

		mazePanel.getPath().addAll(path);
	}

	/**
	 * Détermine la direction vers laquelle aller tout en suivant le mur.
	 * 
	 * @return Nouvelle direction.
	 */
	private Side whereToTurn() {
		Side dir = orientation;
		int counter = 0;
		if (handOnLeft) {
			dir = lookToTheLeft(dir);
			while (counter++ < Side.values().length && mazePanel.getWall(explorer[0], explorer[1], dir) == 1)
				dir = lookToTheRight(dir);
		} else {
			dir = lookToTheRight(dir);
			while (counter++ < Side.values().length && mazePanel.getWall(explorer[0], explorer[1], dir) == 1)
				dir = lookToTheLeft(dir);
		}

		return dir;
	}

	/**
	 * Oriente l'explorateur vers la gauche. Le compteur de virages est également
	 * mis à jour.
	 * 
	 * @param direction Direction courante.
	 * @return Direction représentant la gauche de la direction actuelle.
	 */
	private Side lookToTheLeft(Side direction) {
		List<Side> sides = Arrays.asList(Side.values());
		int index = sides.indexOf(direction);
		index += (sides.size() - 1);
		turnCount--;

		return sides.get(mod(index, sides.size()));
	}

	/**
	 * Oriente l'explorateur vers la droite. Le compteur de virages est également
	 * mis à jour.
	 * 
	 * @param direction Direction courante.
	 * @return Direction représentant la droite de la direction actuelle.
	 */
	private Side lookToTheRight(Side direction) {
		List<Side> sides = Arrays.asList(Side.values());
		int index = sides.indexOf(direction);
		index++;
		turnCount++;

		return sides.get(mod(index, sides.size()));
	}

	/** Déplace l'explorateur d'une case dans sa direction. */
	private void move() {
		switch (orientation) {
			case UP:
				explorer[0]--;
				break;
			case LEFT:
				explorer[1]--;
				break;
			case DOWN:
				explorer[0]++;
				break;
			case RIGHT:
				explorer[1]++;
				break;
			default:
				break;
		}
	}

	/**
	 * Indique si un élément (de la forme {@code int[]}) est présent dans une liste
	 * donnée. Cette méthode est une alternative aux méthodes
	 * {@code ArrayList.contains()} et {@code ArrayList.indexOf()} qui ne comparent
	 * que les références et non les valeurs.
	 * 
	 * @param list  Liste d'éléments {@code int[]}.
	 * @param value Elément de la forme {@code int[]}.
	 * @return {@code true} si l'élément existe dans la liste, {@code false} sinon.
	 */
	private boolean has(ArrayList<int[]> list, int[] value) {
		for (int[] item : list)
			if (Arrays.equals(item, value))
				return true;

		return false;
	}

	/**
	 * Supprime tous les éléments d'une liste après un élément donné. Si l'élément
	 * n'existe pas, la méthode supprime alors tous les éléments de la liste.
	 * 
	 * @param list  Liste d'éléments {@code int[]}.
	 * @param value Elément de la forme {@code int[]}.
	 */
	private void removeAfter(ArrayList<int[]> list, int[] value) {
		while (!list.isEmpty() && !Arrays.equals(list.get(list.size() - 1), value))
			list.remove(list.size() - 1);
	}

	/**
	 * Calcule le modulo d'un entier donné. Cette méthode est una alternative à
	 * l'opérateur {@code %} qui fournit un résultat mégatif lorsque le nombre est
	 * négatif.
	 */
	private int mod(int number, int mod) {
		int n = number % mod;

		return (n < 0) ? n + mod : n;
	}
}