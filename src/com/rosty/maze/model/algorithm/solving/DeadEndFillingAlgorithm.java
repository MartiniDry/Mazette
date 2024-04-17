package com.rosty.maze.model.algorithm.solving;

import java.util.Arrays;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de colmatage</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme est basé sur une recherche globale et
 * itérative de toutes les impasses du labyrinthe ; puisque le trajet entre le
 * départ et l'arrivée ne passe jamais par une impasse, on peut toutes les
 * supprimer en partant du bout. Cette méthode garantit de ne garder que le (ou
 * les) chemin(s) utile(s) reliant l'entrée et la sortie du labyrinthe.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Pour commencer, l'algorithme détecte le bout de
 * toutes les impasses présentes dans la grille. A chaque étape, l'algorithme
 * avance d'un pas dans chaque impasse et marque la case comme "visitée" ; un
 * compteur indique le nombre d'impasses restantes. On estime que l'on peut
 * avancer dans l'impasse s'il n'y a qu'une seule direction qui peut être
 * empruntée (les autres présentent soit un mur, soit une impasse déjà visitée).
 * L'algorithme s'arrête lorsque le compteur des impasses atteint 0. Le résultat
 * de cet algorithme est l'ensemble des chemins possibles du départ vers
 * l'arrivée (un seul chemin si le labyrinthe est parfait). Un algorithme de
 * <i>backtracking</i> rudimentaire permet alors de tracer la solution.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'algorithme peut être implémenté de deux façons
 * différentes :
 * <ul>
 * <li><u>Par balayage :</u> on scanne la grille à chaque étape pour identifier
 * les impasses restantes. La méthode utilise très peu de mémoire (complexité en
 * O(1)) mais est coûteuse en temps de calcul : la complexité temporelle atteint
 * O(M²*N²) dans le pire des cas (labyrinthe en forme d'escargot).</li>
 * <li><u>Par suivi :</u> on mémorise les impasses et on suit leur chemin. Cette
 * méthode optimise fortement le temps de calcul mais nécessite de sauvegarder
 * l'état d'exploration de chaque impasse. Les complexités en temps et en
 * mémoire sont en O(M*N) dans le pire des cas.</li>
 * </ul>
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class DeadEndFillingAlgorithm extends MazeSolvingAlgorithm {
	/** Compteur d'impasses à chaque étape de l'algorithme. */
	int visibleDeadEnds = 0;

	/**
	 * Constructeur de la classe {@link DeadEndFillingAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public DeadEndFillingAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".deadend-filling.scan";
	}

	@Override
	public void init() {
		// Détection de toutes les impasses dans le labyrinthe
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (isDeadEnd(i, j)) {
					mazePanel.setCell(i, j, 1);
					visibleDeadEnds++;
				} else
					mazePanel.setCell(i, j, 0);
	}

	@Override
	public boolean isComplete() {
		return visibleDeadEnds == 0;
	}

	@Override
	public void step() {
		visibleDeadEnds = 0;

		// Etape 1 : marquage des cases non-visitées filant vers des impasses
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (isFalseDeadEnd(i, j)) {
					mazePanel.setCell(i, j, 2);
					visibleDeadEnds++;
				}

		// Etape 2 : coloriage des cases détectées
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) == 2)
					mazePanel.setCell(i, j, 1);
	}

	@Override
	public void finish() {
		super.finish();

		// Les impasses sont toutes comblées ; laissons un explorateur tracer un chemin
		// par "backtracking".
		int[] explorer = mazePanel.getStart().clone();
		mazePanel.getPath().add(explorer);
		mazePanel.setCell(explorer[0], explorer[1], 2);

		while (!Arrays.equals(explorer, mazePanel.getEnd())) {
			Side direction = canMove(explorer[0], explorer[1]);
			if (direction != null) { // Le trajet de l'explorateur suit le premier chemin venu.
				explorer = move(explorer[0], explorer[1], direction);
				mazePanel.getPath().add(explorer);
				mazePanel.setCell(explorer[0], explorer[1], 2);
			} else // Si aucun chemin n'est trouvé, revenir en arrière jusqu'à en trouver un.
				mazePanel.getPath().remove(mazePanel.getPath().size() - 1);
		}
	}

	/**
	 * Indique si la case représente une impasse dans le labyrinthe i.e. entourée de
	 * murs sauf dans une seule direction. Le point de départ et le point d'arrivée
	 * ne sont pas comptabilisés comme impasses.
	 * 
	 * @param i Numéro de ligne.
	 * @param j Numéro de colonne.
	 */
	private boolean isDeadEnd(int i, int j) {
		if (Arrays.equals(mazePanel.getStart(), new int[] { i, j })
				|| Arrays.equals(mazePanel.getEnd(), new int[] { i, j }))
			return false;

		int nbWays = 0;
		for (Side s : Side.values())
			if (mazePanel.getWall(i, j, s) != 1)
				nbWays++;

		return nbWays == 1;
	}

	/**
	 * Indique si la case est une "fausse impasse" i.e. entourée de murs et de cases
	 * déjà visitées, à l'exception d'une direction. Le point de départ et le point
	 * d'arrivée ne sont pas comptabilisés comme "fausses impasses", sous peine de
	 * les faire disparaître du chemin final.
	 * 
	 * @param i Numéro de ligne.
	 * @param j Numéro de colonne.
	 */
	private boolean isFalseDeadEnd(int i, int j) {
		if (mazePanel.getCell(i, j) == 1) // Une case déjà visitée n'est pas comptée.
			return false;

		if (Arrays.equals(mazePanel.getStart(), new int[] { i, j })
				|| Arrays.equals(mazePanel.getEnd(), new int[] { i, j }))
			return false;

		int nbWays = 0;
		for (Side s : Side.values())
			if (mazePanel.getWall(i, j, s) != 1 && mazePanel.getNeighbourCell(new WallCoord(i, j, s)) != 1)
				nbWays++;

		return nbWays == 1;
	}

	/**
	 * Détermine la première direction dans laquelle l'explorateur peut avancer à
	 * une position spécifique, une fois les impasses retirées du labyrinthe.
	 * 
	 * @param i Numéro de ligne.
	 * @param j Numéro de colonne.
	 */
	private Side canMove(int i, int j) {
		for (Side s : Side.values())
			if (mazePanel.getWall(i, j, s) != 1 && mazePanel.getNeighbourCell(new WallCoord(i, j, s)) == 0)
				return s;

		return null;
	}

	/**
	 * Déplace l'explorateur d'une case dans une direction spécifique.
	 * 
	 * @param i   Numéro de ligne.
	 * @param j   Numéro de colonne.
	 * @param dir Direction du déplacement.
	 * @return Nouvelles coordonnées de l'explorateur.
	 */
	private int[] move(int i, int j, Side dir) {
		switch (dir) {
			case UP:
				i--;
				break;
			case LEFT:
				j--;
				break;
			case DOWN:
				i++;
				break;
			case RIGHT:
				j++;
				break;
			default:
				break;
		}

		return new int[] { i, j };
	}
}