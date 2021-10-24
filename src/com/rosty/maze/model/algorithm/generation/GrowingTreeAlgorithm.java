package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme du blob</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme trace des chemins multiples dans la grille en
 * partant d'une cellule aléatoirement choisie. A chaque étape, l'algorithme
 * enregistre une liste représentant les points en attente d'être explorés. Le
 * nom de l'algorithme en anglais est "<i>growing tree</i>", mais sa traduction
 * est plutôt moche an français. J'ai donc choisi ce nom parce que la génération
 * des chemins est visiblement similaire à la croissance d'un blob.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'algorithme correspond au mélange de deux autres :
 * <ul>
 * <li><u>L'algorithme de rembobinage récursif :</u> le chemin est tracé à
 * partir du <b>dernier élément de la liste</b> et le premier élément retiré
 * dans la liste est la dernière case du chemin.</li>
 * <li><u>L'algorithme de Prim :</u> le chemin est tracé à partir de
 * <b>n'importe quel élément de la liste</b> et idem pour le retrait.<br/>
 * ATTENTION : cette version <font color="#FF8888">n'est pas optimisée</font>
 * par rapport à celle de la classe {@link PrimAlgorithm} ; ici, la liste
 * <b>pendingCells</b> enregistre une majorité des cases explorées, tandis que
 * la version optimisée n'enregistre que les cases présentes à la frontière
 * entre les zones explorée et inexplorée.</li>
 * </ul>
 * </p>
 * <p>
 * Un nombre noté <b>algoRatio</b> définit le ratio d'utilisation de chaque
 * algorithme. L'algorithme de rembobinage récursif est exclusivement utilisé
 * lorsque le ratio est à 0% ; celui de Prim l'est à 100%.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Chaque case de la grille est visitée deux fois ; la
 * première quand elle est incluse dans la liste d'attente, la seconde quand
 * elle en est retirée. La complexité temporelle est donc en O(MN) (2*M*N
 * étapes). La complexité mémoire est également en O(MN) : en effet, la liste
 * d'attente peut potentiellement enregistrer la totalité des cases, par exemple
 * lorsque l'on pose le ratio à 100% et qu'il n'y a aucun rembobinage du chemin
 * avant la fin.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class GrowingTreeAlgorithm extends MazeGenerationAlgorithm {
	/** Ratio d'utilisation des algorithmes. */
	private float algoRatio = 0.5F;

	/**
	 * Liste des cellules en suspens. Celles-ci enregistre les cellules sur
	 * lesquelles exécuter l'algorithme.
	 */
	private List<int[]> pendingCells = new ArrayList<>();

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link GrowingTreeAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public GrowingTreeAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		/* Etape 1 : remplissage du terrain avec la valeur 2. */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 2);

		/* Etape 2 : placement de la toute première cellule à analyser. */
		int r = rand.nextInt(nbRow), c = rand.nextInt(nbCol);
		pendingCells.add(new int[] { r, c });
		mazePanel.setCell(r, c, 1);
	}

	@Override
	public boolean isComplete() {
		return pendingCells.isEmpty();
	}

	@Override
	public void step() {
		switch (nextAlgo()) {
			case PRIM:
				algo_prim();
				break;
			case RECURSIVE_BACKTRACKING:
				algo_rb();
				break;
			default:
				break;
		}
	}

	/**
	 * Détermine aléatoirement le type d'algorithme à utiliser en fonction du
	 * paramètre {@link GrowingTreeAlgorithm#algoRatio}.
	 * 
	 * @return {@link SubAlgo#RECURSIVE_BACKTRACKING} if the random value is
	 *         strictly under the ratio, {@link SubAlgo#PRIM} if it is strictly over
	 *         the ratio.
	 */
	private SubAlgo nextAlgo() {
		float ran = rand.nextFloat();
		if (ran == algoRatio)
			return nextAlgo();
		else
			return ran > algoRatio ? SubAlgo.RECURSIVE_BACKTRACKING : SubAlgo.PRIM;
	}

	/** Exécute une étape de m'algorithme (non-optimisé) de Prim. */
	private void algo_prim() {
		carve(rand.nextInt(pendingCells.size()));
	}

	/** Exécute une étape de m'algorithme de rembobinage récursif. */
	private void algo_rb() {
		carve(pendingCells.size() - 1);
	}

	/**
	 * Creuse le chemin en partant d'une cellule présente dans la liste
	 * {@link GrowingTreeAlgorithm#pendingCells}. Si une cellule inexplorée est
	 * repérée à proximité de la cellule, on l'insère dans la liste, autrement on
	 * retire la cellule courante de la liste.
	 * 
	 * @param index Indice de la cellule dans la liste.
	 */
	private void carve(int index) {
		int[] selectedCell = pendingCells.get(index), selectedNewCell = null;
		int r = selectedCell[0], c = selectedCell[1];

		// Répérage des murs qui peuvent être retirés
		List<Side> removableWalls = new ArrayList<>();
		if (r > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.UP)) == 2)
			removableWalls.add(Side.UP);

		if (r < nbRow - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.DOWN)) == 2)
			removableWalls.add(Side.DOWN);

		if (c > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.LEFT)) == 2)
			removableWalls.add(Side.LEFT);

		if (c < nbCol - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.RIGHT)) == 2)
			removableWalls.add(Side.RIGHT);

		if (!removableWalls.isEmpty()) { // Si une cellule inexplorée est repérée, ...
			Side removedWall = removableWalls.get(rand.nextInt(removableWalls.size()));
			switch (removedWall) {
				case UP:
					selectedNewCell = new int[] { r - 1, c };
					break;
				case DOWN:
					selectedNewCell = new int[] { r + 1, c };
					break;
				case LEFT:
					selectedNewCell = new int[] { r, c - 1 };
					break;
				case RIGHT:
					selectedNewCell = new int[] { r, c + 1 };
					break;
				default:
					break;
			}

			// ...alors la cellule est insérée dans la liste et marquée dans la grille.
			mazePanel.setWall(r, c, removedWall, 0);
			if (selectedNewCell != null)
				addPending(selectedNewCell);
		} else // Sinon retirer la cellule courante et le marquer dans la grille.
			removePending(index);
	}

	/**
	 * Insère une cellule dans la liste {@link GrowingTreeAlgorithm#pendingCells}.
	 * 
	 * @param newCell Coordonnées de la cellule.
	 */
	private void addPending(int[] newCell) {
		pendingCells.add(newCell);
		mazePanel.setCell(newCell[0], newCell[1], 1);
	}

	/**
	 * Retire le cellule de la liste {@link GrowingTreeAlgorithm#pendingCells} à
	 * l'indice spécifié.
	 * 
	 * @param index Indice de la cellule dans la liste.
	 */
	private void removePending(int index) {
		int[] cell = pendingCells.get(index);
		mazePanel.setCell(cell[0], cell[1], 0);

		pendingCells.remove(index);
	}

	/**
	 * Enumération des algorithmes de génération présents au coeur de celui-ci.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	private enum SubAlgo {
		/** Algorithme non-optimisé de Prim */
		PRIM,
		/** Algorithme de rembobinage récursif */
		RECURSIVE_BACKTRACKING;
	}
}