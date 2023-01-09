package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Dijkstra</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme part du point de départ et génère
 * progressivement un arbre de recouvrement minimal (en anglais, MST pour
 * <i>minimum spanning tree</i>). Cet arbre considère les chemins dont le coût
 * du trajet est minimal. Aucune cellule de la grille ne peut être présente deux
 * fois dans l'arbre, seul le chemin de coût minimal doit être conservé.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'heuristique définit la façon d'augmenter le coût
 * à mesure que l'on explore un chemin dans le labyrinthe. Dans cet algorithme,
 * le calcul est donné par la formule :
 * 
 * <pre>
 * C(n2) = C(n1) + d(n1, n2)
 * </pre>
 * 
 * , où :
 * <ul>
 * <li>C est la fonction de coût,</li>
 * <li>d est la distance entre deux cellules du labyrinthe,</li>
 * </ul>
 * 
 * L'algorithme fonctionne à la manière de l'algorithme A* (cf. la classe
 * {@link AStarAlgorithm}). Cependant, il y a deux différences :
 * <ol>
 * <li>Comme indiqué dans la formule ci-dessus, le poids des cellules n'est pas
 * considéré.</li>
 * <li>L'arbre des chemins balaie la totalité de la grille du labyrinthe.</li>
 * </ol>
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
public class DijkstraAlgorithm extends MazeSolvingAlgorithm {
	/** Arbre mémorisant les chemins parcourus via l'heuristique. */
	private HeurTree cellTree;

	/**
	 * Liste des feuilles où l'arbre peut encore s'étendre dans la grille ; quand
	 * une feuille fait face à trois murs, elle n'est pas comptée dans la liste.
	 */
	private ArrayList<HeurTree> freeLeafs = new ArrayList<>();

	/**
	 * Constructeur de la classe {@link DijkstraAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public DijkstraAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".dijkstra";
	}

	@Override
	public void init() {
		/* Etape 1 : remplissage du terrain avec une valeur arbitrairement grande. */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.setCell(i, j, Integer.MAX_VALUE);

		/* Etape 2 : lancement de l'heuristique. */
		int[] start = mazePanel.getStart();
		cellTree = new HeurTree(start[0], start[1]);

		freeLeafs.add(cellTree);
		mazePanel.setCell(start[0], start[1], 0);
	}

	@Override
	public boolean isComplete() {
		return freeLeafs.isEmpty();
	}

	@Override
	public void step() {
		// Etape 1 : détermination du noeud à étudier pour cette étape
		HeurTree nodeToSee = freeLeafs.stream() //
				.min(Comparator.comparing(node -> heuristic(node))) //
				.orElseThrow(NoSuchElementException::new);

		// Etape 2 : répérage des chemins possibles autour de la cellule courante
		ArrayList<HeurTree> nextNodes = new ArrayList<>();
		if (isToExplore(nodeToSee, Side.UP))
			nextNodes.add(new HeurTree(nodeToSee.i - 1, nodeToSee.j));

		if (isToExplore(nodeToSee, Side.DOWN))
			nextNodes.add(new HeurTree(nodeToSee.i + 1, nodeToSee.j));

		if (isToExplore(nodeToSee, Side.LEFT))
			nextNodes.add(new HeurTree(nodeToSee.i, nodeToSee.j - 1));

		if (isToExplore(nodeToSee, Side.RIGHT))
			nextNodes.add(new HeurTree(nodeToSee.i, nodeToSee.j + 1));

		for (HeurTree node : nextNodes) {
			nodeToSee.add(node);
			mazePanel.setCell(node.i, node.j, (int) heuristic(node));
		}

		// Etape 3 : mise à jour de la liste 'freeLeafs'
		freeLeafs.remove(nodeToSee);
		freeLeafs.addAll(nextNodes);
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

	/** Calcule le coût d'un noeud de l'arbre grâce à l'heuristique. */
	private double heuristic(HeurTree node) {
		double heur = 0;
		for (HeurTree nd = node; !nd.isRoot(); nd = nd.parent)
			heur += distance(nd.i, nd.j, nd.parent.i, nd.parent.j);

		return heur;
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
	 *         Dijkstra, <code>false</code> sinon.
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

		/**
		 * Constructeur de la classe {@link HeurTree}.
		 * 
		 * @param i Ligne du labyrinthe.
		 * @param j Colonne du labyrinthe.
		 */
		HeurTree(int i, int j) {
			this.i = i;
			this.j = j;
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
			StringBuilder sb = new StringBuilder("(" + i + " " + j + ")");
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
