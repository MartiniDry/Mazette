package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Wilson</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme explore la grille de façon incrémentale en
 * traçant des chemins de la zone vierge vers la zone explorée. L'algorithme
 * s'arrête lorsqu'il n'est plus possible de tracer de chemin, ce qui implique
 * qu'il n'y a plus de case à explorer.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2>
 * <ul>
 * <li>On choisit un point d'ancrage <b>P0(x0,y0)</b> et un point <b>P != P0</b>
 * où est placé l'explorateur. Ce dernier va se promener pas à pas dans la
 * grille de façon aléatoire, jusqu'à rencontrer <b>P0</b>. Une fois le chemin
 * trouvé, l'algorithme brise les murs de <b>P</b> à <b>P0</b> et marque les
 * cases comme explorées.</li>
 * <li>L'algorithme répète la procédure mais cette fois-ci, le point <b>P</b>
 * correspond à la cellule vierge située le plus au coin de la grille ; soit le
 * coin en haut à gauche (<code>reversed=false</code>), soit le coin en bas à
 * droite (<code>reversed=true</code>). Le chemin ne vise plus un point
 * d'ancrage <b>P0</b> mais l'ensemble des cases visitées. En d'autres termes,
 * si l'explorateur atteint une cellule explorée au cours de sa promenade, alors
 * l'étape est terminée ; les murs sont brisés et les cases sont marquées comme
 * visitées.</li>
 * <li>Lors de la phase d'exploration, si le chemin passe deux fois par la même
 * cellule dans la grille (création d'une boucle), l'algorithme rembobine le
 * chemin pas à pas jusqu'a atteindre la cellule en question ; il reprend alors
 * son exploration en partant de ce point.</li>
 * <li>L'algorithme se temrine lorsque l'explorateur s'est promené sur toutes
 * les cases de la grille.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Les performances de l'algorithme dépendent du parcours
 * totalement aléatoire dans la grille ; on ne peut donc pas déterminer la
 * complexité temporelle de cet algorithme. Cependant, on peut affirmer que
 * l'algorithme est statistiquement plus court qu'Aldous-Broder car celui-ci
 * prend en compte la présence de cellules déjà explorées ; ce n'est pas une
 * exploration "à l'aveugle".
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class WilsonAlgorithm extends MazeGenerationAlgorithm {
	/** Portion de chemin explorée. */
	private List<WallCoord> partialPath;
	/** Position courante de l'explorateur dans la grille. */
	private int x /* ligne */, y /* colonne */;
	/**
	 * Point d'ancrage initial ; au démarrage de l'algorithme, l'explorateur doit
	 * arriver à ce point.
	 */
	private int x0 /* ligne */, y0 /* colonne */;

	/**
	 * Booléen indiquant si l'explorateur débute ses promenades en partant du haut
	 * ou du bas de la grille.
	 */
	private boolean reversed;

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
		partialPath = new ArrayList<>();

		// Initialisation de la grille ; toutes les cases sont marquées à 0 pour
		// indiquer que la case est inexplorée.
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 0);

		// Définition (aléatoire) du point de départ
		x0 = rand.nextInt(nbRow - 1);
		y0 = rand.nextInt(nbCol - 1);

		// Positionnement de l'explorateur hors de la grille ; sa valeur sera définie au
		// lancement de l'algorithme
		x = -1;
		y = -1;

		// Le point de départ est exploré.
		mazePanel.setCell(x0, y0, 2);

		// Définition du sens de balayage
		reversed = false;
	}

	@Override
	public boolean isComplete() {
		// Si un chemin est en cours d'exploration, alors l'algorithme n'est pas
		// terminé.
		if (!partialPath.isEmpty())
			return false;

		// S'il reste encore des cases à explorer (valeur 0), alors l'algorithme n'est
		// pas terminé.
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) == 0)
					return false;

		return true;
	}

	@Override
	public void step() {
		// Au départ de l'algorithme, aucun chemin n'est lancé et les coordonnées (x,y)
		// sont à définir pour débuter l'exploration.
		if (partialPath.isEmpty()) {
			if (x == -1 && y == -1) { // Si l'on est au tout début de l'algorithme, ...
				// ...alors le point de départ est choisi au hasard (mais différent de (x0,y0)).
				do {
					x = rand.nextInt(nbRow - 1);
					y = rand.nextInt(nbCol - 1);
				} while (x == x0 && y == y0);
			} else {
				// Cette étape est atteinte lorsque l'algorithme a validé la portion de chemin
				// et qu'une nouvelle exploration de la grille est lancée.
				boolean found = false;
				if (!reversed) {
					// Recherche de la cellule vierge la plus en haut à gauche de la grille.
					rowLoop: for (int i = 0; i < nbRow; i++)
						for (int j = 0; j < nbCol; j++)
							if (mazePanel.getCell(i, j) == 0) {
								x = i;
								y = j;
								found = true;
								break rowLoop;
							}
				} else {
					// Recherche de la cellule vierge la plus en bas à droite de la grille.
					rowLoop: for (int i = nbRow - 1; i >= 0; i--)
						for (int j = nbCol - 1; j >= 0; j--)
							if (mazePanel.getCell(i, j) == 0) {
								x = i;
								y = j;
								found = true;
								break rowLoop;
							}
				}

				if (!found) // Si aucune case inexplorée n'est présente, ...
					return; // ...alors l'algorithme est terminé.
			}

			// A ce stade, on a défini une nouvelle coordonnée (x,y) ; la case est marquée
			// comme explorée.
			mazePanel.setCell(x, y, 1);

			// Choix du prochain pas à réaliser, qui sera inséré dans le chemin partiel.
			ArrayList<WallCoord> sides = getSides(x, y);
			WallCoord direction = sides.get(rand.nextInt(sides.size()));
			partialPath.add(direction);
		} else {
			// Cette étape est atteinte lorsqu'une exploration est en cours. Le dernier
			// élément du chemin partiel indique le dernier pas choisi par l'algorithme ;
			// nous le faisons ici.
			WallCoord lastDirection = partialPath.get(partialPath.size() - 1);
			move(lastDirection.side);

			switch (mazePanel.getCell(x, y)) { // Nouvelle position de l'explorateur
				case 0: // Si la case n'est pas visitée, ...
					// ...alors on la marque comme explorée et on détermine le prochain pas à
					// réaliser.
					mazePanel.setCell(x, y, 1);
					ArrayList<WallCoord> sides = getSides(x, y);
					WallCoord newDirection = sides.get(rand.nextInt(sides.size()));
					partialPath.add(newDirection);

					break;
				case 1: // Si la case est marquée comme visitée, ...
					// ...alors elle fait partie du chemin courant, ce qui indique que l'explorateur
					// tourne en rond ! Il doit faire demi-tour pour que la boucle disparaisse.
					int index = partialPath.size() - 1;
					WallCoord dir = lastDirection;
					do { // Faire un pas en arrière, ...
						partialPath.remove(index--);
						mazePanel.setCell(dir.x, dir.y, 0);
						if (index >= 0) // Simple sécurité
							dir = partialPath.get(index);
						else
							break;
					} while (dir.x != x || dir.y != y); // ...tant que l'on n'est pas revenu à (x,y).

					mazePanel.setCell(x, y, 1); // Le chemin est relancé en (x,y).

					break;
				case 2: // Si la case est déjà explorée, ...
					// ...alors on valide le chemin partiel en brisant les murs et en marquant les
					// cases comme valides (valeur 2).
					partialPath.forEach(wall -> {
						mazePanel.setWall(wall.x, wall.y, wall.side, 0);
						mazePanel.setCell(wall.x, wall.y, 2);
					});
					partialPath.clear(); // Le chemin est effacé pour débuter une nouvelle exploration.

					break;
				default:
					break;
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