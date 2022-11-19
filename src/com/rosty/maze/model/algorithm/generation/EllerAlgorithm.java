package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme d'Eller</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme a été conceptualisé par l'ingénieur américain
 * Marlin Eller en 1984. Celui-ci consiste à balayer les cases de haut en bas et
 * de gauche à droite, en retirant sur chaque ligne les murs verticaux, puis les
 * horizontaux. Le processus génère pas à pas des groupes de chemins en retirant
 * aléatoirement des murs ; ces groupes peuvent se mélanger lorsque l'algorithme
 * choisit de briser un mur vertical. A la fin du processus, la dernière ligne
 * se charge de rassembler les groupes restants pour ne former qu'un seul et
 * unique labyrinthe.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'algorithme génère les murs verticaux et du bas à
 * chaque nouvelle ligne visitée ; il se promène deux fois sur chaque ligne et
 * retire des murs à chaque étape. Le premier balayage se concentre sur le mur à
 * droite de la cellule courante et le second, sur le mur du dessous. Un
 * incrémenteur est utilisé pour donner une valeur <b>unique</b> à chaque
 * nouvelle case qui n'a pas encore été visitée.
 * <ul>
 * <li><u>Processus EST :</u>le mur de droite ne peut être retiré si les cases
 * adjacentes sont de même valeur. Dans le reste des cas, on choisit de briser
 * le mur aléatoirement. Si un mur est brisé, la case de droite, ainsi que
 * toutes les cases de la ligne possédant cette valeur, prennent la valeur de la
 * case courante.</li>
 * <li><u>Processus SUD :</u>si la case courante est isolée du reste de la
 * grille, alors il faut retirer le mur du bas pour la rattacher à la ligne
 * suivante (situation 1). Plus généralement, en considérant l'ensemble des
 * cases de la ligne possédant la valeur de la cellule courante, si aucun mur du
 * bas n'a été retiré, il faut obligatoirement en enlever un pour ne pas créer
 * d'ilôt (situation 2). Dans le reste des cas, le mur est retiré aléatoirement.
 * Si le mur du bas est brisé, la valeur de la case courante est reportée sur
 * celle du dessous.</li>
 * </ul>
 * </p>
 * <p>
 * L'algorithme prend fin une fois que la case en bas à droite est atteinte lors
 * du processus EST.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'algorithme a une complexité temporelle en O(M*N) ; plus
 * précisément, (2M-1)*N opérations sont effectuées. La complexité mémoire est
 * en O(M) car l'évolution des groupes de chemins peut être suivie en ne se
 * focalisant que sur la ligne courante.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class EllerAlgorithm extends MazeGenerationAlgorithm {
	/** Indicateur de position dans la grille. */
	private int cellId;

	/** Mode de construction du labyrinthe sur la ligne courante. */
	private Mode mode;

	/** Incrémenteur de la valeur de la cellule. */
	private int inc;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link EllerAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public EllerAlgorithm(MazePanel panel) {
		super(panel);
	}
	
	@Override
	public String getLabel() {
		return super.getLabel() + ".eller";
	}

	@Override
	public void init() {
		// Tous les murs intérieurs de la grille sont retirés.
		for (int i = 0; i < nbRow - 1; i++) {
			for (int j = 0; j < nbCol - 1; j++) {
				mazePanel.setWall(i, j, Side.DOWN, 0);
				mazePanel.setWall(i, j, Side.RIGHT, 0);
			}

			mazePanel.setWall(i, nbCol - 1, Side.DOWN, 0);
		}

		for (int j = 0; j < nbCol - 1; j++)
			mazePanel.setWall(nbRow - 1, j, Side.RIGHT, 0);
		
		// Initialisation de la position et de l'incrémenteur
		cellId = 0;
		inc = 2;
		
		// Le balayage commence par les murs du bas.
		mode = Mode.SOUTH_PROCESS;
	}

	@Override
	public boolean isComplete() {
		return cellId == nbRow * nbCol - 1;
	}

	@Override
	public void step() {
		// Calcul des coordonnées de la case courante
		int i = cellId / nbCol;
		int j = cellId % nbCol;

		// Assignation d'un nombre pour toutes les cases de la grille
		if (j == 0) {
			// Le mode est "renversé" à chaque début de ligne.
			mode = (mode == Mode.EAST_PROCESS ? Mode.SOUTH_PROCESS : Mode.EAST_PROCESS);
			if (mode == Mode.EAST_PROCESS) {
				fillWalls(i); // Placement des murs à chaque nouvelle ligne visitée
				fillCells(i); // Remplissage des cases vides au commencement d'une nouvelle ligne
			}
		}

		// Lancement du processus sur la cellule courante
		switch (mode) {
			case EAST_PROCESS:
				eastAction(i, j);
				break;
			case SOUTH_PROCESS:
				southAction(i, j);
				break;
			default:
				break;
		}

		// Si la cellule courante atteint le bord droit de la grille sans se trouver en
		// bas, deux actions peuvent être réalisées :
		// - si EAST_PROCESS est actif, la cellule courante file vers la ligne suivante.
		// - si SOUTH_PROCESS est actif, la cellule courante revient en début de ligne.
		if (j == nbCol - 1 && i < nbRow - 1) {
			if (mode == Mode.SOUTH_PROCESS)
				cellId++;
			else if (mode == Mode.EAST_PROCESS)
				cellId -= (nbCol - 1);
		} else
			cellId++;
	}

	/**
	 * Remplit une ligne spécifique de la grille avec tous les murs latéraux et tous
	 * les murs du bas.
	 * 
	 * @param row Numéro de ligne.
	 */
	private void fillWalls(int row) {
		for (int k = 0; k < nbCol - 1; k++)
			mazePanel.setWall(row, k, Side.RIGHT, 1);

		if (row < nbRow - 1)
			for (int k = 0; k < nbCol; k++)
				mazePanel.setWall(row, k, Side.DOWN, 1);
	}

	/**
	 * Remplit les cases d'une ligne spécifique de gauche à droite en utilisant
	 * l'incrémenteur {@link EllerAlgorithm#inc}. Seules les cases "vides" (valeur
	 * <b>0</b>) sont prises en compte.
	 * 
	 * @param row Numéro de ligne.
	 */
	private void fillCells(int row) {
		if (row == 0)
			for (int k = 0; k < nbCol; k++)
				mazePanel.setCell(row, k, inc++);
		else
			for (int k = 0; k < nbCol; k++)
				if (mazePanel.getWall(row, k, Side.UP) == 0)
					mazePanel.setCell(row, k, mazePanel.getCell(row - 1, k));
				else
					mazePanel.setCell(row, k, inc++);
	}

	/**
	 * Calcule le mur à droite de la cellule courante pour déterminer s'il est
	 * nécessaire de le retirer.
	 * 
	 * @param row Numéro de ligne.
	 * @param col Numéro de colonne.
	 */
	private void eastAction(int row, int col) {
		if (col < nbCol - 1) {
			boolean rightWallToRemove = false;

			int val = mazePanel.getCell(row, col);
			int nextVal = mazePanel.getCell(row, col + 1);

			if (val != nextVal)
				if (row == nbRow - 1) {
					// S'il s'agit de la dernière ligne de la grille, le placement des murs est
					// complètement déterminé.
					rightWallToRemove = true;
				} else if (rand.nextBoolean() == false) {
					// Dans le cas contraire, les murs sont retirés aléatoirement.
					rightWallToRemove = true;
				}

			if (rightWallToRemove) {
				mazePanel.setWall(row, col, Side.RIGHT, 0);

				ArrayList<Integer> family = findCellsByValue(row, nextVal);
				for (int c : family)
					mazePanel.setCell(row, c, val);
			}
		}
	}

	/**
	 * Calcule le mur du bas de la cellule courante pour déterminer s'il est
	 * nécessaire de le retirer.
	 * 
	 * @param row Numéro de ligne.
	 * @param col Numéro de colonne.
	 */
	private void southAction(int row, int col) {
		if (row < nbRow - 1) {
			boolean downWallToRemove = false;

			int val = mazePanel.getCell(row, col);
			ArrayList<Integer> family = findCellsByValue(row, val);

			if (family.size() == 1) { // Situation 1
				downWallToRemove = true;
			} else if (col == family.get(family.size() - 1)) { // Situation 2
				// Cette situation est traitée lorsque la case courante correspond à la dernière
				// du groupe. Si aucune cellule du groupe n'a été percée par le bas, il faut
				// forcément briser le mur sous la cellule courante pour ne pas créer d'ilôt.
				boolean isIslet = true;
				for (int c : family)
					if (mazePanel.getWall(row, c, Side.DOWN) == 0) {
						isIslet = false;
						break;
					}

				if (isIslet)
					downWallToRemove = true;
			} else if (rand.nextBoolean() == false) {
				// Ces deux cas mis à part, l'algorithme choisit de retirer le mur du bas de
				// façon purement aléatoire.
				downWallToRemove = true;
			}

			if (downWallToRemove) {
				mazePanel.setWall(row, col, Side.DOWN, 0);
				mazePanel.setCell(row + 1, col, val);
			}
		}
	}

	/**
	 * Fournit la liste des cases possédant une valeur spécifique sur la ligne
	 * donnée.
	 * 
	 * @param row   Numéro de ligne.
	 * @param value Valeur des cases à rechercher.
	 * @return Liste d'entiers représentant le numéro de colonne de la case.
	 */
	protected ArrayList<Integer> findCellsByValue(int row, int value) {
		ArrayList<Integer> group = new ArrayList<Integer>();
		for (int c = 0; c < nbCol; c++)
			if (mazePanel.getCell(row, c) == value)
				group.add(c);

		return group;
	}

	/**
	 * Enumération des processus appliqués dans cet algorithme.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	private enum Mode {
		/** Balayage des murs latéraux */
		EAST_PROCESS,
		/** Balayage des murs du dessous */
		SOUTH_PROCESS;
	}
}