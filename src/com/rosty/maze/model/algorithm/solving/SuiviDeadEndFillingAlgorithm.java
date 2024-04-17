package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Arrays;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de colmatage par suivi</h1>
 * 
 * <p>
 * <h2>Principe</h2> cf. {@link DeadEndFillingAlgorithm}.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Pour commencer, l'algorithme détecte le bout de
 * toutes les impasses présentes dans la grille. L'exploration débute dans
 * chaque impasse découverte ; une liste mémorise la position des explorateurs
 * ainsi que la direction que chacun va emprunter pour avancer. A chaque étape,
 * les explorateurs avancent d'un pas, marquent la case comme "visitée" et
 * déterminent la nouvelle direction à emprunter. On estime qu'un explorateur
 * peut avancer dans l'impasse s'il n'y a qu'une seule direction qui peut être
 * empruntée (les autres présentent soit un mur, soit une impasse déjà visitée)
 * ; si ce critère n'est plus respecté, alors l'impasse est retirée de la liste.
 * L'algorithme s'arrête lorsque la liste des impasses est vide. Le résultat de
 * cet algorithme est l'ensemble des chemins possibles du départ vers l'arrivée
 * (un seul chemin si le labyrinthe est parfait). Un algorithme de
 * <i>backtracking</i> rudimentaire permet alors de tracer la solution.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'algorithme par suivi trace le chemin de chaque impasse
 * mémorisée. Cette méthode optimise le temps de calcul mais nécessite de
 * sauvegarder l'état d'exploration de chaque impasse. Les complexités en temps
 * et en mémoire sont en O(M*N) dans le pire des cas (labyrinthe en escargot
 * pour la complexité temporelle, labyrinthe en forme de peignes empilés pour la
 * complexité mémoire).
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class SuiviDeadEndFillingAlgorithm extends MazeSolvingAlgorithm {
	/** Liste des explorateurs d'impasses. */
	ArrayList<WallCoord> deadEnds = new ArrayList<>();

	/**
	 * Constructeur de la classe {@link SuiviDeadEndFillingAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public SuiviDeadEndFillingAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".deadend-filling.suivi";
	}

	@Override
	public void init() {
		// Remise des cellules à 0
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, 0);

		// Détection de toutes les impasses pour démarrer l'algorithme
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (isDeadEnd(i, j)) { // La fin d'une impasse est mémorisée puis marquée dans la grille.
					// Pour chaque impasse, on détermine la prochaine direction où aller.
					deadEnds.add(new WallCoord(i, j, way(i, j)));
					mazePanel.setCell(i, j, 1);
				}
	}

	@Override
	public boolean isComplete() {
		return deadEnds.isEmpty();
	}

	@Override
	public void step() {
		for (int k = 0; k < deadEnds.size();) { // Pour chaque impasse à explorer, ...
			// ...se déplacer d'une case dans la direction mémorisée.
			WallCoord explorer = deadEnds.get(k);
			int[] coord = move(explorer.x, explorer.y, explorer.side);

			if (isFalseDeadEnd(coord[0], coord[1])) { // Si la nouvelle position est une "fausse impasse", ...
				// ...alors mettre à jour l'exploration de l'impasse en notant la prochaine
				// direction à prendre ...
				deadEnds.set(k, new WallCoord(coord[0], coord[1], way(coord[0], coord[1])));
				mazePanel.setCell(coord[0], coord[1], 1);

				// ...et passer à la suite.
				k++;
			} else // Sinon, arrêter l'exploration de l'impasse.
				deadEnds.remove(k);
		}
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
			Side direction = way(explorer[0], explorer[1]);
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
	private Side way(int i, int j) {
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