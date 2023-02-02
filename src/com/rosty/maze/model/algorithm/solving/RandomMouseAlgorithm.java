package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.model.algorithm.generation.WilsonAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de la souris perdue</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme explore pas-à-pas les chemins devant lui, de
 * façon aléatoire sans jamais mémoriser son chemin. Le nom de l'algorithme
 * s'inspire des tests cognitifs en laboratoire, dans lesquels on demande à des
 * souris d'avancer dans un labyrinthe pour retrouver la source d'une odeur.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'algorithme est l'équivalent "résolution" de
 * l'algorithme de Wilson, dans lequel on génère les chemins pas-à-pas en se
 * déplaçant de façon aléatoire (cf. {@link WilsonAlgorithm}).
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Puisqu'aucun chemin n'est mémorisé, l'algorithme a une
 * complexité mémoire en O(1). De fait, la souris peut passer plusieurs fois par
 * le même point, ce qui rend le temps de calcul impossible à prédire par le
 * caractère purement aléatoire de l'exploration.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class RandomMouseAlgorithm extends MazeSolvingAlgorithm {
	/** Cellule actuellement explorée (symbolisée ici par une souris). */
	private int[] mouse = new int[2];

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link RandomMouseAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public RandomMouseAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".random-mouse";
	}

	@Override
	public void init() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, 0);

		mouse = mazePanel.getStart();
		mazePanel.setCell(mouse[0], mouse[1], 2);
	}

	@Override
	public boolean isComplete() {
		return Arrays.equals(mouse, mazePanel.getEnd());
	}

	@Override
	public void step() {
		ArrayList<Side> sides = getSides();
		int index = rand.nextInt(sides.size());

		mazePanel.setCell(mouse[0], mouse[1], 1);
		move(sides.get(index));
		mazePanel.setCell(mouse[0], mouse[1], 2);
	}

	/**
	 * Fournit la liste des directions accessibles pour la souris (la méthode ne
	 * prend pas en compte les murs situés en périphérie de la grille).
	 * 
	 * @return Liste d'instance {@link Side}}.
	 */
	private ArrayList<Side> getSides() {
		int i = mouse[0], j = mouse[1];

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
	 * Déplace la souris d'une case dans la direction donnée.
	 * 
	 * @param direction Direction du déplacement.
	 */
	private void move(Side direction) {
		switch (direction) {
			case UP:
				mouse[0]--;
				break;
			case LEFT:
				mouse[1]--;
				break;
			case DOWN:
				mouse[0]++;
				break;
			case RIGHT:
				mouse[1]++;
				break;
			default:
				break;
		}
	}
}