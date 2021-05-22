package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de l'accordéon</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme consiste à explorer les cases de gauche à
 * droite et de haut en bas. A chaque étape, l'algorithme choisit soit de
 * retirer le mur de droite pour créer un couloir horizontal, soit de retirer un
 * mur au hasard au-dessus de ce couloir. Le résultat est un labyrinthe dont les
 * chemins "ruissèlent" à partir du haut de la grille, à la manière du mouvement
 * latéral d'un serpent (<i>side winding</i> en anglais) ; ce mouvement a pour
 * nom "mouvement en accordéon" en français.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> La grille est visitée ligne par ligne en partant de
 * celle du haut. A chaque étape, une liste nommée 'rowBlock' enregistre la
 * cellule courante, ainsi que les précédentes dans le cas d'un couloir. Pour
 * que l'algorithme fonctionne, tous les murs de la première ligne doivent être
 * retirés pour former un long couloir horizontal.
 * </p>
 * <p>
 * A partir de la ligne suivante, l'algorithme a la possibilité de retirer un
 * mur soit vers la droite, soit vers le haut. Plus précisément :
 * <ul>
 * <li>Soit l'algorithme fait une excavation vers l'est (il retire le mur à
 * droite de la cellule courante)</li>
 * <li>Soit l'algorithme fait une excavation "nord" ; dans ce cas, il
 * sélectionne au hasard une case dans la liste 'rowBlock' et il retire le mur
 * du haut.</li>
 * </ul>
 * En fin de ligne, l'algorithme n'a d'autre choix que de faire une excavation
 * nord.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'algorithme a une complexité mémoire en O(M) car
 * celui-ci enregistre un couloir horizontal dont la taille peut atteindre la
 * largeur du labyrinthe. Cependant, ce couloir horizontal est utilisé pour
 * déterminer le mur à retirer dans le cas d'une excavation nord. la pire
 * situation est atteinte lorsque sur chaque ligne, l'algorithme réalise autant
 * d'excavations "est" que "nord". Dans ce cas, la complexité temporelle est en
 * O(M².N).
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class SidewinderAlgorithm extends MazeGenerationAlgorithm {
	/** Indicateur de position dans la grille. */
	private int cellId = 0;
	/** Liste des cases actuellement mémorisées pour une excavation nord. */
	private List<int[]> rowBlock = new ArrayList<>();

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link SidewinderAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public SidewinderAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, 0);
	}

	@Override
	public boolean isComplete() {
		return cellId == nbRow * nbCol;
	}

	@Override
	public void step() {
		// Localisation de la case courante
		int i = cellId / nbCol;
		int j = cellId % nbCol;
		rowBlock.add(new int[] { i, j });

		if (i == 0) { // La première ligne est un long couloir horizontal.
			if (j < nbCol - 1)
				mazePanel.setWall(i, j, Side.RIGHT, 0);
			else // Arrivé à droite, aucune excavation "nord" n'est faite.
				rowBlock.clear();
		} else { // Pour les lignes suivantes, deux options sont possibles :
			if (j < nbCol - 1) {
				boolean carveToEast = rand.nextBoolean();
				if (carveToEast) // - Soit on retire le mur à droite de la cellule
					mazePanel.setWall(i, j, Side.RIGHT, 0);
				else // - Soit on fait une excavation "nord".
					carveToNorth();
			} else
				carveToNorth();
		}

		cellId++;
	}

	/** Retire un mur au hasard sur la partie nord de 'rowBlock'. */
	private void carveToNorth() {
		if (!rowBlock.isEmpty()) {
			int[] ranCell = rowBlock.get(rand.nextInt(rowBlock.size()));
			mazePanel.setWall(ranCell[0], ranCell[1], Side.UP, 0);
			rowBlock.clear();
		}
	}
}