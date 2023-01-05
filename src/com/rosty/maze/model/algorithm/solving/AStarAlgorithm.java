package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

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

		// TOERASE
		HeurTree a = new HeurTree(0, 0, 2);
		a.add(new HeurTree(2, 1, 14));
		a.add(new HeurTree(5, 6, 5));
		a.children.get(0).add(new HeurTree(78, 4, 23));
		HeurTree ch = a.children.get(1);
		ch.add(new HeurTree(4, 4, 4));
		ch.add(new HeurTree(5, 5, 5));

		System.out.println("Arbre : " + a);

		ArrayList<HeurTree> b = a.children.get(0).children.get(0).getAscendance();
		for (HeurTree c : b)
			System.out.println("." + c.i + " " + c.j + ".");

		System.out.println("Nouvel arbre : " + a);

		System.out.println("Calcul heuristique : " + a.children.get(0).children.get(0).heuristic());

		System.out.println("---");
		ArrayList<HeurTree> d = a.getLeafs();
		System.out.println("size: " + d.size());
		System.out.println(Arrays.toString(d.toArray()));

		System.out.println("AAA: " + a);
		a.delete(ch);
		System.out.println("ZZZ: " + a);
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
		mazePanel.setCell(start[0], start[1], WEIGHT);

		freeLeafs.add(cellTree);
	}

	@Override
	public boolean isComplete() {
		int[] end = mazePanel.getEnd();
		List<HeurTree> candidates = freeLeafs.stream() //
				.filter(node -> node.i == end[0] && node.j == end[1]) //
				.collect(Collectors.toList());

		return !candidates.isEmpty();
	}

	@Override
	public void step() {
		// Etape 1 : détermination du noeud à étudier pour cette étape
		HeurTree nodeToSee = freeLeafs.stream() //
				.min(Comparator.comparing(HeurTree::heuristic)) //
				.orElseThrow(NoSuchElementException::new);

		// Etape 2 : répérage des chemins possibles autour de la cellule courante
		if (isToExplore(nodeToSee.i, nodeToSee.j, Side.UP)) {
			HeurTree upCell = new HeurTree(nodeToSee.i - 1, nodeToSee.j, WEIGHT);
			nodeToSee.add(upCell);
			mazePanel.setCell(upCell.i, upCell.j, (int) upCell.heuristic());
		}

		if (isToExplore(nodeToSee.i, nodeToSee.j, Side.DOWN)) {
			HeurTree downCell = new HeurTree(nodeToSee.i + 1, nodeToSee.j, WEIGHT);
			nodeToSee.add(downCell);
			mazePanel.setCell(downCell.i, downCell.j, (int) downCell.heuristic());
		}

		if (isToExplore(nodeToSee.i, nodeToSee.j, Side.LEFT)) {
			HeurTree leftCell = new HeurTree(nodeToSee.i, nodeToSee.j - 1, WEIGHT);
			nodeToSee.add(leftCell);
			mazePanel.setCell(leftCell.i, leftCell.j, (int) leftCell.heuristic());
		}

		if (isToExplore(nodeToSee.i, nodeToSee.j, Side.RIGHT)) {
			HeurTree rightCell = new HeurTree(nodeToSee.i, nodeToSee.j + 1, WEIGHT);
			nodeToSee.add(rightCell);
			mazePanel.setCell(rightCell.i, rightCell.j, (int) rightCell.heuristic());
		}

		// Etape 3 : mise à jour de la liste 'freeLeafs'
		freeLeafs = cellTree.getLeafs().stream() //
				.filter(node -> !isBlocked(node.i, node.j) || isTheEnd(node.i, node.j)) //
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void finish() {
		super.finish();

		int[] end = mazePanel.getEnd();
		ArrayList<HeurTree> endings = find(cellTree, end[0], end[1]); // Noeud d'arrivée
		System.out.println("ending: " + endings);
		if (!endings.isEmpty()) {
			HeurTree endNode = endings.get(0);
			for (HeurTree node : endNode.getAscendance())
				mazePanel.getRoute().getPath().add(new int[] { node.i, node.j });
		}
	}

	ArrayList<HeurTree> find(HeurTree node, int i, int j) {
		ArrayList<HeurTree> arr = new ArrayList<>();
		if (node.i == i && node.j == j)
			arr.add(node);

		if (!node.isLeaf())
			for (HeurTree child : node.children)
				arr.addAll(find(child, i, j));

		return arr;
	}

	boolean isToExplore(int i, int j, Side side) {
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

	boolean isBlocked(int i, int j) {
		// Le noeud est "bloqué" dans le labyrinthe si la case correspondante ne
		// contient qu'un seul point d'accès (celui que l'on a emprunté pour entrer).
		int numberOfWalls = 0;
		for (Side side : Side.values())
			if (mazePanel.getWall(i, j, side) != 0)
				numberOfWalls++;

		return numberOfWalls == (Side.values().length - 1);
	}

	boolean isTheEnd(int i, int j) {
		int[] end = mazePanel.getEnd();

		return i == end[0] && j == end[1];
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

		boolean add(HeurTree child) {
			child.parent = this;
			return children.add(child);
		}

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

		ArrayList<HeurTree> getAscendance() {
			ArrayList<HeurTree> arr = new ArrayList<>();
			HeurTree node = this; // "Curseur" posé sur le noeud courant
			for (; !node.isRoot(); node = node.parent)
				arr.add(node);

			arr.add(node);

			return arr;
		}

		ArrayList<HeurTree> getLeafs() {
			ArrayList<HeurTree> arr = new ArrayList<>();
			if (isLeaf())
				arr.add(this);
			else
				for (HeurTree child : children)
					arr.addAll(child.getLeafs());

			return arr;
		}

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