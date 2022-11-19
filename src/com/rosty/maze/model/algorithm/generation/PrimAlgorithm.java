package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Prim</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme consiste à retirer pas à pas des murs dans le
 * labyrinthe en partant d'une case choisie aléatoirement dans la grille.
 * L'algorithme visite peu à peu les cases autour d'elles et retire les murs
 * pour les connecter à elle. L'algorithme s'arrête lorsque toutes les cases
 * sont reliées à la case d'origine.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> On définit 3 états pour les cases de la grille :
 * <ul>
 * <li>Visitée (<code>cell = 0</code>)</li>
 * <li>En attente de visite (<code>cell = 1</code>)</li>
 * <li>Non-visitée (<code>cell = 2</code>)</li>
 * </ul>
 * 
 * On commence l'algorithme en choisissant au hasard une case de la grille :
 * celle-ci est marquée "en attente" (valeur 1). A chaque étape, l'algorithme
 * effectue la même procédure :
 * <ol>
 * <li>La case est marquée comme "visitée"</li>
 * <li>La case est reliée au reste de la zone visitée ; pour cela on retire un
 * mur séparant la case d'une autre case de valeur 0.</li>
 * <li>Autour de la case courante, toutes les cases non-visitées sont marquées
 * comme "en attente".</li>
 * </ol>
 * L'algorithme s'arrête lorsqu'il n'y a <b>plus aucune case en attente de
 * visite</b>.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Puisque l'on explore toutes les cases sauf la case
 * initiale et que chacune voit sa valeur modifiée deux fois, la complexité est
 * égale à :<br/>
 * <b>N<sub>steps</sub> = 2(M.N - 1)</b>
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class PrimAlgorithm extends MazeGenerationAlgorithm {
	/**
	 * Liste des cellules indiquées par l'algorithme et à partir desquelles seront
	 * identifiés les prochains murs à retirer.
	 */
	private List<int[]> pendingCells;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link PrimAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public PrimAlgorithm(MazePanel panel) {
		super(panel);
	}
	
	@Override
	public String getLabel() {
		return super.getLabel() + ".prim";
	}

	@Override
	public void init() {
		/* Etape 1 : remplissage du terrain avec la valeur 2. */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 2);

		/* Etape 2 : placement de la toute première cellule à analyser. */
		pendingCells = new ArrayList<>();
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
		/* Etape 1 : choix de la prochaine case à traiter */
		int index = rand.nextInt(pendingCells.size());
		int[] selectedCell = pendingCells.get(index);

		/*
		 * Etape 2 : choix d'une case voisine déjà visitée et suppression du mur entre
		 * les deux
		 */
		int r = selectedCell[0], c = selectedCell[1];
		pendingCells.remove(index);
		mazePanel.setCell(r, c, 0);

		List<Side> removableWalls = new ArrayList<>();

		// Répérage des murs qui peuvent être retirés
		if (r > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.UP)) == 0)
			removableWalls.add(Side.UP);

		if (r < nbRow - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.DOWN)) == 0)
			removableWalls.add(Side.DOWN);

		if (c > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.LEFT)) == 0)
			removableWalls.add(Side.LEFT);

		if (c < nbCol - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.RIGHT)) == 0)
			removableWalls.add(Side.RIGHT);

		// Retrait aléatoire d'un mur
		if (!removableWalls.isEmpty()) {
			Side removedWall = removableWalls.get(rand.nextInt(removableWalls.size()));
			mazePanel.setWall(r, c, removedWall, 0);
		}

		/* Etape 3 : repérage des cases voisines à marquer "en attente" */
		if (r > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.UP)) == 2) {
			pendingCells.add(new int[] { r - 1, c });
			mazePanel.setCell(r - 1, c, 1);
		}

		if (r < nbRow - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.DOWN)) == 2) {
			pendingCells.add(new int[] { r + 1, c });
			mazePanel.setCell(r + 1, c, 1);
		}

		if (c > 0 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.LEFT)) == 2) {
			pendingCells.add(new int[] { r, c - 1 });
			mazePanel.setCell(r, c - 1, 1);
		}

		if (c < nbCol - 1 && mazePanel.getNeighbourCell(new WallCoord(r, c, Side.RIGHT)) == 2) {
			pendingCells.add(new int[] { r, c + 1 });
			mazePanel.setCell(r, c + 1, 1);
		}
	}
}