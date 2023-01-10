package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Prim-Jarnik</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme parcourt pas à pas tous les chemins possibles
 * à partir du point de départ, en choisissant le ou les noeuds suivants les
 * plus simples à atteindre ; le résultat de ce calcul est un arbre dit "de
 * recouvrement minimal" (en anglais, MST pour <i>minimum spanning tree</i>). On
 * peut illustrer le fonctionnement de l'algorithme comme un explorateur qui
 * avance en ne voyant que les noeuds directement présents. Une fois construit,
 * l'algorithme retrace le chemin de l'arbre allant de l'arrivée vers le départ.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Le coût pour aller d'une cellule du labyrinthe à
 * l'autre est ici la distance entre les deux points. A chaque étape de
 * l'algorithme, on choisit une feuille de l'arbre (i.e. une cellule du
 * labyrinthe qui vient d'être explorée). Parmi tous les noeuds possibles, ceux
 * à explorer n'ont pas encore été visités et ont le trajet le plus faible
 * possible.
 * </p>
 * <p>
 * L'algorithme de Prim-Jarnik (souvent abrégé en "algorithme de Prim") est très
 * similaire à celui de Dijkstra (cf. la classe {@link DijkstraAlgorithm}). La
 * grande différence entre les deux se trouve dans l'exploration des cellules du
 * labyrinthe (en d'autres termes, la génération de l'arbre de recouvrement).
 * L'algorithme de Prim-Jarnik sélectionne les cellules dont le trajet direct
 * est le plus faible ; celui de Dijkstra sélectionne les cellules dont le coût
 * du trajet <b>depuis le départ</b> est minimal.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'arbre de données de l'algorithme se construit étape par
 * étape et peut potentiellement enregistrer toutes les cases du labyrinthe,
 * d'où une complexité temps et mémoire en O(M*N) dans le pire des cas.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class PrimJarnikAlgorithm extends MazeSolvingAlgorithm {
	/** Arbre mémorisant les chemins parcourus via l'heuristique. */
	private HeurTree cellTree;

	/**
	 * Liste des feuilles où l'arbre peut encore s'étendre dans la grille ; quand
	 * une feuille fait face à trois murs, elle n'est pas comptée dans la liste.
	 */
	private ArrayList<HeurTree> freeLeafs = new ArrayList<>();

	/**
	 * Constructeur de la classe {@link PrimJarnikAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public PrimJarnikAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".prim-jarnik";
	}

	@Override
	public void init() {
		/* Etape 1 : remplissage du terrain avec le poids des cellules. */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, Integer.MAX_VALUE);

		/* Etape 2 : lancement de l'heuristique. */
		int[] start = mazePanel.getStart();
		cellTree = new HeurTree(start[0], start[1], 0);

		freeLeafs.add(cellTree);
		mazePanel.setCell(start[0], start[1], (int) cellTree.cost);
	}

	@Override
	public boolean isComplete() {
		return freeLeafs.isEmpty();
	}

	@Override
	public void step() {
		// Etape 1 : détermination du noeud à étudier pour cette étape
		HeurTree nodeToSee = freeLeafs.get(0);

		// Etape 2 : répérage des chemins possibles autour de la cellule courante
		// Le calcul de la distance est inutile à expliciter en soi mais il met en
		// évidence l'aspect important de cet algorithme. Qui plus est, ce sera utile si
		// des labyrinthes plus complexes doivent être implémentés à l'aide d'un graphe.
		ArrayList<HeurTree> nextNodes = new ArrayList<>();
		if (isToExplore(nodeToSee, Side.UP)) {
			HeurTree upCell = new HeurTree(nodeToSee.i - 1, nodeToSee.j, 0);
			upCell.cost = distance(upCell.i, upCell.j, nodeToSee.i, nodeToSee.j);
			nextNodes.add(upCell);
		}

		if (isToExplore(nodeToSee, Side.DOWN)) {
			HeurTree downCell = new HeurTree(nodeToSee.i + 1, nodeToSee.j, 1);
			downCell.cost = distance(downCell.i, downCell.j, nodeToSee.i, nodeToSee.j);
			nextNodes.add(downCell);
		}

		if (isToExplore(nodeToSee, Side.LEFT)) {
			HeurTree leftCell = new HeurTree(nodeToSee.i, nodeToSee.j - 1, 1);
			leftCell.cost = distance(leftCell.i, leftCell.j, nodeToSee.i, nodeToSee.j);
			nextNodes.add(leftCell);
		}

		if (isToExplore(nodeToSee, Side.RIGHT)) {
			HeurTree rightCell = new HeurTree(nodeToSee.i, nodeToSee.j + 1, 1);
			rightCell.cost = distance(rightCell.i, rightCell.j, nodeToSee.i, nodeToSee.j);
			nextNodes.add(rightCell);
		}

		for (HeurTree node : nextNodes) {
			nodeToSee.add(node);
			mazePanel.setCell(node.i, node.j, (int) node.cost);
		}

		// Etape 3 : sélection des noeuds à coût minimal et mise à jour de la liste
		// 'freeLeafs'.
		freeLeafs.remove(nodeToSee);

		if (!nextNodes.isEmpty()) {
			double minimum = nextNodes.stream().mapToDouble(node -> node.cost).min().getAsDouble();
			for (HeurTree node : nextNodes)
				if (node.cost == minimum)
					freeLeafs.add(node);
		}
	}

	@Override
	public void finish() {
		super.finish();

		ArrayList<HeurTree> endings = find(cellTree, mazePanel.getEnd()); // Noeud d'arrivée
		if (!endings.isEmpty()) // Si un chemin est terminé, la 1ère arrivée est choisie
			for (HeurTree node : endings.get(0).getAscendance())
				mazePanel.getRoute().getPath().add(new int[] { node.i, node.j });
	}

	/**
	 * Calcule la distance euclidienne entre deux points de la grille.
	 * 
	 * @param i1 Ligne du premier point.
	 * @param j1 Colonne du premier point.
	 * @param i2 Ligne du second point.
	 * @param j2 Colonne du second point.
	 */
	private double distance(int i1, int j1, int i2, int j2) {
		double di = i2 - i1, dj = j2 - j1;

		return Math.sqrt(di * di + dj * dj);
	}

	/**
	 * Recherche tous les noeuds d'un arbre donné, dont les coordonnées sont celles
	 * de la case spécifiée dans le labyrinthe.
	 * 
	 * @param node Arbre quelconque du labyrinthe.
	 * @param cell Coordonnées de la cellule du labyrinthe.
	 * @return Liste de tous les noeuds possédant les bonnes coordonnées.
	 */
	ArrayList<HeurTree> find(HeurTree node, int[] cell) {
		ArrayList<HeurTree> arr = new ArrayList<>();
		if (node.isAt(cell))
			arr.add(node);

		if (!node.isLeaf())
			for (HeurTree child : node.children)
				arr.addAll(find(child, cell));

		return arr;
	}

	/**
	 * Dans le cadre de cet algorithme, indique si le labyrinthe doit être exploré
	 * dans une direction donnée pour une case donnée.
	 * 
	 * @param node Noeud correspondant à une cellule dans la grille.
	 * @param side Direction d'observation.
	 * @return <code>true</code> si la case doit être visitée par l'algorithme de
	 *         Prim, <code>false</code> sinon.
	 */
	boolean isToExplore(HeurTree node, Side side) {
		int i = node.i, j = node.j;
		boolean withSides;
		switch (side) {
			case UP:
				withSides = (i > 0);
				break;
			case DOWN:
				withSides = (i < nbRow - 1);
				break;
			case LEFT:
				withSides = (j > 0);
				break;
			case RIGHT:
				withSides = (j < nbCol - 1);
				break;
			default:
				withSides = false;
				break;
		}

		return withSides && mazePanel.getWall(i, j, side) == 0
				&& mazePanel.getNeighbourCell(new WallCoord(i, j, side)) == Integer.MAX_VALUE;
	}

	/**
	 * Indique si l'on ne peut plus avancer dans le labyrinthe à partir du noeud
	 * d'un arbre donné. La case correspondante représente alors une impasse et
	 * l'algorithme ne s'intéressera plus à ce chemin.
	 * 
	 * @return <code>true</code> si l'algorithme "bloque" à cette case,
	 *         <code>false</code> sinon.
	 */
	boolean isBlocked(HeurTree node) {
		int i = node.i, j = node.j;
		int numberOfWalls = 0;

		// Le noeud est "bloqué" dans le labyrinthe si la case correspondante ne
		// contient qu'un seul point d'accès (celui que l'on a emprunté pour entrer).
		for (Side side : Side.values())
			if (mazePanel.getWall(i, j, side) != 0)
				numberOfWalls++;

		return numberOfWalls + 1 == Side.values().length;
	}

	/**
	 * Classe définissant un arbre de données obtenu par calcul de l'heuristique sur
	 * les cellules du labyrinthe.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	@SuppressWarnings("unused")
	private class HeurTree {
		/** Adresse du noeud parent, nécessaire pour naviguer dans l'arbre. */
		HeurTree parent;

		/** Liste des noeuds attachés au noeud courant. */
		ArrayList<HeurTree> children = new ArrayList<>();

		/** Coordonées de la cellule associée au noeud dans le labyrinthe. */
		int i, j;

		/** Coût de transit vers le noeud courant depuis son parent. */
		double cost;

		/**
		 * Constructeur de la classe {@link HeurTree}.
		 * 
		 * @param i Ligne du labyrinthe.
		 * @param j Colonne du labyrinthe.
		 * @param c Coût du noeud.
		 */
		HeurTree(int i, int j, int c) {
			this.i = i;
			this.j = j;
			this.cost = c;
		}

		/** Indique si le noeud possède un parent. */
		boolean isRoot() {
			return (parent == null);
		}

		/** Indique si un ou plusieurs noeuds sont attachés au noeud courant. */
		boolean isLeaf() {
			return children.isEmpty();
		}

		/** Insère un noeud dans l'arbre. */
		boolean add(HeurTree child) {
			child.parent = this;
			return children.add(child);
		}

		/** Retire un noeud de l'arbre ainsi que tous ses noeuds descendants. */
		boolean delete(HeurTree child) {
			if (children.isEmpty())
				return false;

			if (children.contains(child)) {
				child.parent = null;
				return children.remove(child);
			} else {
				boolean deleted = false;
				for (HeurTree c : children)
					deleted |= c.delete(child);

				return deleted;
			}
		}

		/**
		 * Liste l'ensemble des noeuds de l'arbre précédant un noeud spécifique ; la
		 * liste est classée dans l'ordre, du noeud courant jusqu'à la racine.
		 */
		ArrayList<HeurTree> getAscendance() {
			ArrayList<HeurTree> arr = new ArrayList<>();
			HeurTree node = this; // "Curseur" posé sur le noeud courant
			for (; !node.isRoot(); node = node.parent)
				arr.add(node);

			arr.add(node);

			return arr;
		}

		/** Liste toutes les feuilles de l'arbre. */
		ArrayList<HeurTree> getLeafs() {
			ArrayList<HeurTree> arr = new ArrayList<>();
			if (isLeaf())
				arr.add(this);
			else
				for (HeurTree child : children)
					arr.addAll(child.getLeafs());

			return arr;
		}

		/**
		 * Indique si le noeud de l'arbre se situe à une position spécifique dans le
		 * labyrinthe.
		 */
		boolean isAt(int[] cell) {
			return i == cell[0] && j == cell[1];
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("(" + i + " " + j + " cost=" + cost + ")");
			if (!isLeaf()) {
				sb.append("->[");
				ArrayList<String> arr = new ArrayList<>();
				for (HeurTree child : children)
					arr.add(child.toString());

				sb.append(String.join(",", arr));
				sb.append("]");
			}

			return sb.toString();
		}
	}
}
