package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme A-étoile (A*)</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme parcourt l'ensemble des chemins possibles à
 * partir du point de départ, jusqu'à atteindre le point d'arrivée ; ces chemins
 * sont enregistrés dans un arbre de données (cf. {@link HeurTree}). Un coût est
 * peu-à-peu calculé pour chaque chemin ; celui avec le coûe le plus faible sera
 * exploré en priorité.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> L'heuristique définit la façon d'augmenter le coût
 * à mesure que l'on explore un chemin dans le labyrinthe. Dans cet algorithme,
 * le calcul est donné par la formule :
 * 
 * <pre>
 * C(n2) = C(n1) + d(n1, n2) + W(n2)
 * </pre>
 * 
 * , où :
 * <ul>
 * <li>C est la fonction de coût,</li>
 * <li>d est la distance entre deux cellules du labyrinthe,</li>
 * <li>W est la fonction de poids i.e. le coût que représente la visite d'une
 * cellule du labyrinthe ; par convention, W est constante ici.</li>
 * </ul>
 * 
 * Il ne s'agit pas d'un algorithme de recherche globale. A* n'explore pas la
 * totalité des chemins disponibles, mais ne s'intéresse qu'à celui qui minimise
 * le coût ; de fait, l'algorithme ne garantit pas de trouver le chemin le plus
 * court entre le départ et l'arrivée mais fournit une solution très
 * satisfaisante en un temps raisonnable.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'arbre de données de l'algorithme se construit étape par
 * étape et peut potentiellement enregistrer toutes les cases du labyrinthe,
 * d'où une complexité temps et mémoire en O(M*N) dans le pire des cas.
 * </p>
 * 
 * <p>
 * A titre d'information, un algorithme de recherche globale a une complexité
 * temporelle exponentielle :
 * <code>O(2<sup>a</sup>.3<sup>b</sup>.4<sup>c</sup>)</code>, où <code>a</code>,
 * <code>b</code> et <code>c</code> sont le nombre de cases par lesquelles on
 * peut sortir dans resp. 2, 3 ou 4 directions. Plus il y a de chemins possibles
 * pour atteindre l'arrivée et plus ces nombres sont grands.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class AStarAlgorithm extends MazeSolvingAlgorithm {
	/** Valeur des cellules pour le calcul de l'heuristique. */
	private static final int WEIGHT = 1;

	/** Arbre mémorisant les chemins parcourus via l'heuristique. */
	private HeurTree cellTree;

	/**
	 * Liste des feuilles où l'arbre peut encore s'étendre dans la grille ; quand
	 * une feuille fait face à trois murs, elle n'est pas comptée dans la liste.
	 */
	private ArrayList<HeurTree> freeLeafs = new ArrayList<>();

	/**
	 * Constructeur de la classe {@link AStarAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public AStarAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".a_star";
	}

	@Override
	public void init() {
		/* Etape 1 : remplissage du terrain avec le poids des cellules. */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 0);

		/* Etape 2 : lancement de l'heuristique. */
		int[] start = mazePanel.getStart();
		cellTree = new HeurTree(start[0], start[1], WEIGHT);

		freeLeafs.add(cellTree);
		mazePanel.setCell(start[0], start[1], WEIGHT);
	}

	@Override
	public boolean isComplete() {
		List<HeurTree> endings = freeLeafs.stream() //
				.filter(node -> node.isAt(mazePanel.getEnd())) //
				.collect(Collectors.toList());

		return !endings.isEmpty();
	}

	@Override
	public void step() {
		// Etape 1 : détermination du noeud à étudier pour cette étape
		HeurTree nodeToSee = freeLeafs.stream() //
				.min(Comparator.comparing(HeurTree::heuristic)) //
				.orElseThrow(NoSuchElementException::new);

		// Etape 2 : répérage des chemins possibles autour de la cellule courante
		ArrayList<HeurTree> nextNodes = new ArrayList<>();
		if (isToExplore(nodeToSee, Side.UP))
			nextNodes.add(new HeurTree(nodeToSee.i - 1, nodeToSee.j, WEIGHT));

		if (isToExplore(nodeToSee, Side.DOWN))
			nextNodes.add(new HeurTree(nodeToSee.i + 1, nodeToSee.j, WEIGHT));

		if (isToExplore(nodeToSee, Side.LEFT))
			nextNodes.add(new HeurTree(nodeToSee.i, nodeToSee.j - 1, WEIGHT));

		if (isToExplore(nodeToSee, Side.RIGHT))
			nextNodes.add(new HeurTree(nodeToSee.i, nodeToSee.j + 1, WEIGHT));

		for (HeurTree node : nextNodes) {
			nodeToSee.add(node);
			mazePanel.setCell(node.i, node.j, (int) node.heuristic());
		}

		// Etape 3 : mise à jour de la liste 'freeLeafs'
		freeLeafs = cellTree.getLeafs().stream() //
				.filter(node -> !isBlocked(node) || node.isAt(mazePanel.getEnd())) //
				.collect(Collectors.toCollection(ArrayList::new));
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
	 * Recherche tous les noeuds d'un arbre donné, dont les coordonnées
	 * sont celles de la case spécifiée dans le labyrinthe.
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
	 * @return <code>true</code> si la case doit être visitée par l'algorithme A*,
	 *         <code>false</code> sinon.
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
				&& mazePanel.getNeighbourCell(new WallCoord(i, j, side)) == 0;
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
	private class HeurTree {
		/** Adresse du noeud parent, nécessaire pour naviguer dans l'arbre. */
		HeurTree parent;

		/** Liste des noeuds attachés au noeud courant. */
		ArrayList<HeurTree> children = new ArrayList<>();

		/** Coordonées de la cellule associée au noeud dans le labyrinthe. */
		int i, j;

		/** Poids associé au noeud. */
		double weight;

		/**
		 * Constructeur de la classe {@link HeurTree}.
		 * 
		 * @param i  Ligne du labyrinthe.
		 * @param j  Colonne du labyrinthe.
		 * @param wt Poids du noeud.
		 */
		HeurTree(int i, int j, double wt) {
			this.i = i;
			this.j = j;
			this.weight = wt;
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
		@SuppressWarnings("unused")
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

		/** Calcule le coût d'un noeud de l'arbre grâce à l'heuristique. */
		double heuristic() {
			double heur = weight;
			for (HeurTree node = this; !node.isRoot(); node = node.parent) {
				double di = node.i - node.parent.i;
				double dj = node.j - node.parent.j;
				heur += (Math.sqrt(di * di + dj * dj) + node.parent.weight); // distance + weight
			}

			return heur;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("(" + i + " " + j + " w=" + weight + ")");
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
