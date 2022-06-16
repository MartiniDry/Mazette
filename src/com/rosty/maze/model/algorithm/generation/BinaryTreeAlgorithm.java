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
 * <h1>Algorithme de l'arbre binaire</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme génère un "arbre des chemins" dont la racine
 * se situe dans l'un des coins de la grille. Les branches se séparent en deux
 * branches distinctes juqu'à atteindre le bord opposé. Par conséquent, toutes
 * les branches du labyrinthe sont orientées de la racine vers le coin opposé.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'algorithme analyse chaque cellule de gauche à
 * droite et de haut en bas et retire un mur dès que possible à chaque étape.
 * Pour illustrer l'exemple, considérons un labyrinthe dont la racine débute
 * dans le coin supérieur droit de la grille (<code>Corner.UPPER_RIGHT</code>).
 * A chaque étape, l'algorithme n'a le droit de retirer que deux murs autour de
 * la cellule ; celui du haut et celui de droite. L'algorithme vérifie si les
 * murs sont présents puis en retire un au hasard. Bien entendu, si un seul des
 * deux murs est présent, il sera automatiquement retiré. L'algorithme s'arrête
 * lorsque la dernière case (en bas à gauche) a été analysée.
 * </p>
 * <p>
 * Le résultat final de l'algorithme est un labyrinthe dont les branches
 * "ruissellent" du nord-est au sud-ouest de la grille. Si l'on suit
 * l'algorithme, tous les murs situés le long de la ligne du haut et de celle de
 * droite ont été retirés, ce qui forme deux longs couloirs.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> A chaque étape, l'algorithme repère les murs
 * destructibles ; une opération de conplexité temporelle O(1). De même à chaque
 * étape, l'algorithme ne nécessite de connaître que la case courante et les
 * quatre murs latéraux. Par conséquent, l'algorithme a une complexité mémoire
 * en O(1) et une complexité temporelle en O(M*N).
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class BinaryTreeAlgorithm extends MazeGenerationAlgorithm {
	/** Position du curseur dans la grille. */
	private int x0 /* ligne */, y0 /* colonne */;
	/** Coin de la grille où se situe la racine de l'arbre binaire. */
	private Corner corner = Corner.UPPER_RIGHT;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link BinaryTreeAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public BinaryTreeAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		// Initialisation de la grille
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 0);

		// Positionnement dans la grille
		x0 = 0;
		y0 = -1;
	}

	@Override
	public boolean isComplete() {
		return (x0 == nbRow - 1) && (y0 == nbCol - 1);
	}

	@Override
	public void step() {
		// Incrémentation des coordonnées (x0,y0)
		y0++;
		if (y0 == nbCol) {
			y0 = 0;
			x0++;
		}

		// Sélection des murs qui peuvent être retirés.
		ArrayList<WallCoord> sides = getSides(x0, y0);
		sides.removeIf(wall -> !corner.sides.contains(wall.side));

		// Retrait aléatoire d'un mur
		if (!sides.isEmpty()) { // S'il est possible de retirer un mur, ...
			// ...alors on retire l'un des murs au hasard.
			WallCoord selectedSide = sides.get(rand.nextInt(sides.size()));
			mazePanel.setWall(selectedSide.x, selectedSide.y, selectedSide.side, 0);
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
	 * Enumérations des coins de la grille pour débuter l'algorithme.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	enum Corner {
		/** Bord supérieur gauche */
		UPPER_LEFT(Arrays.asList(Side.UP, Side.LEFT)),
		/** Bord supérieur droit */
		UPPER_RIGHT(Arrays.asList(Side.UP, Side.RIGHT)),
		/** Bord inférieur gauche */
		LOWER_LEFT(Arrays.asList(Side.DOWN, Side.LEFT)),
		/** Bord inférieur droit */
		LOWER_RIGHT(Arrays.asList(Side.DOWN, Side.RIGHT));

		/** Côtés accessibles pour un coin donné de la grille. */
		private List<Side> sides;

		/**
		 * Constructeur de l'énuméré {@link Corner}.
		 * 
		 * @param sides Indique les murs accessibles pour un coin donné.
		 */
		private Corner(List<Side> sides) {
			this.sides = sides;
		}
	}
}