package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Trémeaux</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme est l'un des plus pratiques à utiliser dans la
 * vraie vie : à la manière du petit Poucet, l'explorateur avance au hasard dans
 * le labyrinthe en déposant un "caillou" derrière lui. Dès que l'explorateur
 * est face à un mur ou à l'un de ses marqueurs, il revient en arrière jusqu'à
 * trouver un autre chemin à suivre. L'algorithme s'arrête en atteignant
 * l'arrivée (ou revient au départ s'il n'y en a pas).
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'implémentation de l'algorithme est très simple. A
 * chaque étape, l'explorateur sélectionne au hasard toutes les directions
 * accessibles et non-explorées. Si une direction existe, l'explorateur avance
 * d'une case, sinon il recule d'une case (ainsi de suite jusqu'à trouver une
 * direction). L'algorithme garantit d'atteindre l'arrivée.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'algorithme étant basé sur la recherche en profondeur
 * avec une marche aléatoire, l'algorithme est en O(M*N).
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class TremeauxAlgorithm extends MazeSolvingAlgorithm {
	/** Coordonnées de l'explorateur dans la grille. */
	private int[] explorer = new int[2];
	/** Chemin sauvegardé pour l'algorithme. */
	ArrayList<int[]> path = new ArrayList<int[]>();

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link TremeauxAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public TremeauxAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".tremeaux";
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
		ArrayList<Side> sides = lookAround();
		if (sides.size() != 0) { // S'il est possible d'avancer quelque part, ...
			Side newDirection = sides.get(rand.nextInt(sides.size()));
			move(newDirection); // ...alors se déplacer au hasard dans l'une de ces directions.
			mazePanel.setCell(explorer[0], explorer[1], 2);
			path.add(explorer.clone());
		} else { // Sinon, reculer d'une case.
			path.remove(path.size() - 1);
			explorer = path.get(path.size() - 1).clone();
		}
	}

	@Override
	public void finish() {
		super.finish();

		mazePanel.getPath().addAll(path);
	}

	/**
	 * Détermine les directions vers lesquelles l'explorateur peut aller à sa
	 * position actuelle.
	 * 
	 * @return Liste d'instances {@code Side}.
	 */
	private ArrayList<Side> lookAround() {
		ArrayList<Side> sides = new ArrayList<Side>();
		for (Side s : Side.values())
			if (mazePanel.getWall(explorer[0], explorer[1], s) != 1
					&& mazePanel.getNeighbourCell(new WallCoord(explorer[0], explorer[1], s)) == 0)
				sides.add(s);

		return sides;
	}

	/** Déplace l'explorateur d'une case dans une direction spécifique. */
	private void move(Side direction) {
		switch (direction) {
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
}