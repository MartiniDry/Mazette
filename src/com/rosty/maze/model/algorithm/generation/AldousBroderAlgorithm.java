package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme d'Aldous-Broder</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme explore la grille de façon aléatoire et trace
 * un chemin chaque fois qu'une case non-visitée est découverte. Contrairement à
 * l'algorithme "Hunt-and-Kill", il s'agit d'une exploration à l'aveugle puisque
 * le déplacement est indépendant des valeurs présentes dans la grille.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> On choisit un point de départ de façon aléatoire et
 * on se promène pas à pas dans la grille. A chaque étape, deux situations se
 * présentent :
 * <ul>
 * <li>Si la nouvelle case n'est pas visitée, on brise le mur et on marque la
 * nouvelle case comme visitée.</li>
 * <li>Si la nouvelle case n'est pas visitée, il ne se passe rien.</li>
 * </ul>
 * Pas à pas, le processus se répète jusqu'à ce qu'il n'y ait plus aucune
 * cellule à visiter dans la grille.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Les performances de l'algorithme dépendent du parcours
 * totalement aléatoire dans la grille ; on ne peut donc pas déterminer la
 * complexité temporelle de cet algorithme.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class AldousBroderAlgorithm extends MazeGenerationAlgorithm {
	/** Position courante dans la grille (définie en mode KILL). */
	private int x /* ligne */, y /* colonne */;
	/** Compteur de cases visitées par l'algorithme. */
	private int cellCounter;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link AldousBroderAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public AldousBroderAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".aldous_broder";
	}

	@Override
	public void init() {
		// Initialisation de la grille ; toutes les cases sont marquées à 0 pour
		// indiquer que la case est inexplorée.
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 0);

		// Placement aléatoire du point de départ
		x = rand.nextInt(nbRow - 1);
		y = rand.nextInt(nbCol - 1);

		// Lancement du décompte des cellules explorées
		cellCounter = 0;

		mazePanel.getMaze().setCell(x, y, 1); // La case départ est marquée comme explorée
		cellCounter++;
	}

	@Override
	public boolean isComplete() {
		return cellCounter == nbRow * nbCol;
	}

	@Override
	public void step() {
		// La cellule courante est marquée comme visitée (si ce n'est déjà fait).
		if (mazePanel.getCell(x, y) != 2)
			mazePanel.setCell(x, y, 2);

		List<WallCoord> sides = getSides(x, y);
		WallCoord selectedSide = sides.get(rand.nextInt(sides.size()));
		move(selectedSide.side); // Déplacement aléatoire d'une case

		if (mazePanel.getNeighbourCell(selectedSide) == 0) { // Si la nouvelle cellule n'a pas été visitée, ...
			cellCounter++; // ...celle-ci est notifiée ...
			mazePanel.setWall(selectedSide.x, selectedSide.y, selectedSide.side, 0); // ...et le mur est brisé.
		}

		mazePanel.setCell(x, y, 1); // Marquage de la nouvelle cellule.
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