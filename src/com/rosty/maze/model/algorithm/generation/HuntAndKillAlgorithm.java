package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme "Hunt-and-Kill"</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme fonctionne sur l'alternance de deux modes ;
 * une étape d'exploration aléatoire dans la grille (<i>hunt</i>) et une étape
 * de défrichage pour créer une nouvelle ramification (<i>kill</i>). De fait,
 * chaque nouvelle étape d'exploration définit un nouveau chemin dans le
 * labyrinthe.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> On choisit une case de départ au hasard dans la
 * grille. Les deux procédures sont alors lancées de manière récursive:
 * <ul>
 * <li><u>KILL :</u> partant du point de départ, l'algorithme recherche (au
 * hasard) une case voisine inexplorée et brise le mur pour y accéder.
 * L'algorithme génère peu à peu un chemin et ne s'arrête que lorsqu'il tombe
 * dans un cul-de-sac i.e. lorsqu'il n'y a plus aucune case non-visitée autour
 * de la case courante ; il passe alors en mode <b>HUNT</b>.</li>
 * <li><u>HUNT :</u> l'algorithme se promène à la verticale (de gauche à droite
 * et de haut en bas) ou à l'horizontale (de haut en bas et de gauche à droite)
 * ; il recherche la première case non-visitée qui possède un voisin visité.
 * Dans ce cas, l'algorithme brise le mur entre les deux et se place en mode
 * <b>KILL</b> et le point de départ est placé sur la case courante ; dans le
 * cas contraire, l'algorithme est terminé.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Pour un labyrinthe parfait (sans ilôts) :
 * <ul>
 * <li>En mode KILL, les cases sont visitées une fois chacunes, d'où une
 * complexité temporelle en O(M*N).</li>
 * <li>En mode HUNT, la durée d'exécution est très sensible au mode KILL ; si
 * les chemins sont générés en bas du labyrinthe à chaque fois, le mode HUNT
 * sera long à s'exécuter puisqu'il balaiera une bonne partie de la grille avant
 * d'atteindre le bas.</li>
 * </ul>
 * Imaginons la pire situation possible : l'algorithme choisit le point (M,N)
 * comme point de départ puis se promène en essayant de tomber dans un
 * cul-de-sac le plus vite possible. Il est possible de montrer que dans ce cas,
 * le mode KILL ne visitera pas plus de 6 cases. Ensuite, le mode HUNT est lancé
 * et place le nouveau point de départ en haut à gauche du chemin précédent. La
 * procédure est répétée jusqu'à former le labyrinthe final. Pour un grand
 * labyrinthe, le nombre d'étapes HUNT est grossièrement supérieur à M*N/6 (car
 * le nombre de cases visitées en mode KILL est inférieur ou égal à 6). Comme le
 * mode HUNT examine en moyenne M*N/2 cases, on en déduit un nombre de cases
 * examinées supérieur à (M*N)<sup>2</sup>/12 donc une complexité temporelle en
 * O((M*N)<sup>2</sup>).
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class HuntAndKillAlgorithm extends MazeGenerationAlgorithm {
	/** Mode d'exécution de l'algorithme. */
	private Mode mode;

	/** Position courante dans la grille (définie en mode KILL). */
	private int x /* ligne */, y /* colonne */;
	/** Booléen indiquant le sens de balayage en mode HUNT. */
	private boolean vertical = true;
	/** Point de lancement du mode KILL. */
	private int lineLevel;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link HuntAndKillAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public HuntAndKillAlgorithm(MazePanel panel) {
		super(panel);
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

		mazePanel.getMaze().setCell(x, y, 1); // La case départ est marquée comme explorée

		// Ligne de départ de l'algorithme
		lineLevel = 0;
		
		// Mode de démarrage
		mode = Mode.KILL;
	}

	@Override
	public boolean isComplete() {
		// L'algorithme s'arrête lorsque le mode HUNT ne mène à rien i.e. lorsqu'il
		// balaie toutes les lignes (ou toutes les colonnes) de la grille sans trouver
		// de point de départ pour le mode KILL.
		return mode == Mode.HUNT && ((vertical && lineLevel == nbRow) || (!vertical && lineLevel == nbCol));
	}

	@Override
	public void step() {
		switch (mode) {
			case HUNT:
				int index = -1;
				ArrayList<WallCoord> visitedSides = null;

				if (vertical)
					for (int j = 0; j < nbCol; j++) {
						boolean unvisitedCell = (mazePanel.getCell(lineLevel, j) == 0);
						visitedSides = getSides(lineLevel, j);
						// Retrait des murs qui sont associés à des cellules non-visitées
						visitedSides.removeIf(wc -> {
							int neighbour = mazePanel.getNeighbourCell(wc);
							return neighbour != 2 // case visitée
									&& neighbour != 1; // case marquée comme visitée
						});

						// Si la cellule n'est pas visitée mais possède un voisin qui a été visité, ...
						if (unvisitedCell && !visitedSides.isEmpty()) {
							index = j;
							break; // ...alors on arrête la recherche.
						}
					}
				else
					for (int i = 0; i < nbRow; i++) {
						boolean unvisitedCell = (mazePanel.getCell(i, lineLevel) == 0);
						visitedSides = getSides(i, lineLevel);
						// Retrait des murs qui sont associés à des cellules non-visitées
						visitedSides.removeIf(wc -> {
							int neighbour = mazePanel.getNeighbourCell(wc);
							return neighbour != 2 // case visitée
									&& neighbour != 1; // case marquée comme visitée
						});

						// Si la cellule n'est pas visitée mais possède un voisin qui a été visité, ...
						if (unvisitedCell && !visitedSides.isEmpty()) {
							index = i;
							break; // ...alors on arrête la recherche.
						}
					}

				if (index >= 0) { // Si une ligne a été trouvée en mode HUNT, ...
					mode = Mode.KILL; // ...alors la chasse est ouverte !

					// Sauvegarde du nouveau point de départ
					if (vertical) {
						x = lineLevel;
						y = index;
					} else {
						x = index;
						y = lineLevel;
					}

					mazePanel.setCell(x, y, 1);

					// visitedSides contient au moins un élément ; on le choisit au hasard dans la
					// liste pour débuter le mode KILL.
					WallCoord wall = visitedSides.get(rand.nextInt(visitedSides.size()));
					mazePanel.setWall(wall.x, wall.y, wall.side, 0);

					// Réinitialisation du mode HUNT (pour la prochaine fois)
					lineLevel = 0;
				} else
					lineLevel++;

				break;
			case KILL:
				// Repérage des directions à explorer
				List<WallCoord> unexplored = new ArrayList<>();

				List<WallCoord> sides = getSides(x, y);
				for (WallCoord side : sides) {
					int sideValue = mazePanel.getNeighbourCell(side);
					if (sideValue != -1 /* hors-grille, c'est une simple sécurité */
							&& sideValue != 1 /* case explorée */
							&& sideValue != 2 /* case explorée mais non coloriée */)
						unexplored.add(side);
				}

				// Phase d'exploration (ou de rembobinage)
				mazePanel.setCell(x, y, 2); // Case courante marquée comme explorée
				if (!unexplored.isEmpty()) {
					WallCoord selectedSide = unexplored.get(rand.nextInt(unexplored.size()));
					move(selectedSide.side);

					mazePanel.setCell(x, y, 1); // Nouvelle case explorée
					// Brisage du mur
					mazePanel.setWall(selectedSide.x, selectedSide.y, selectedSide.side, 0);
				} else {
					mode = Mode.HUNT;
				}

				break;
			default:
				break;
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

	/**
	 * Enumération des deux modes d'action de l'algorithme.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	private enum Mode {
		HUNT, KILL;
	}
}