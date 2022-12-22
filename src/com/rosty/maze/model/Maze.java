package com.rosty.maze.model;

/**
 * Classe représentant un labyrinthe (<i>maze</i> en anglais) de forme
 * rectangulaire.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class Maze {
	private int nbRow, nbCol; // Nombre de lignes et de colonnes du labyrinthe
	private int[][] table; // Tableau des cases du labyrinthe

	/**
	 * Constructeur de la classe {@link Maze}.
	 * 
	 * @param nbRow Nombre de lignes du terrain.
	 * @param nbCol Nombre de colonnes du terrain.
	 */
	public Maze(int nbRow, int nbCol) {
		this.nbRow = nbRow;
		this.nbCol = nbCol;
		init();
	}

	/**
	 * Constructeur par copie de la classe {@link Maze}.
	 * 
	 * @param old Matrice d'origine.
	 */
	public Maze(Maze old) {
		this.nbRow = old.nbRow;
		this.nbCol = old.nbCol;

		int r = 2 * nbRow + 1, c = 2 * nbCol + 1;
		this.table = new int[r][c];
		for (int i = 0; i < r; i++)
			for (int j = 0; j < c; j++)
				this.table[i][j] = old.table[i][j];
	}

	/**
	 * Charge le terrain sur lequel sera construit le labyrinthe. Par défaut, les
	 * murs ont pour valeur 1 et les cases ont pour valeur 0.
	 */
	private void init() {
		table = new int[2 * nbRow + 1][];
		clear();
	}

	/** Indique le nombre de lignes du labyrinthe. */
	public int getNbRows() {
		return nbRow;
	}

	/** Indique le nombre de colonnes du labyrinthe. */
	public int getNbColumns() {
		return nbCol;
	}

	/**
	 * Définit la valeur de la cellule à la ligne et à la colonne indiquée.
	 * 
	 * @param row   Numéro de ligne.
	 * @param col   Numéro de colonne.
	 * @param value Valeur de la case.
	 */
	public void setCell(int row, int col, int value) {
		if (row < nbRow && col < nbCol)
			table[2 * row + 1][2 * col + 1] = value;
	}

	/** Fournit la valeur de la cellule à la ligne et à la colonne indiquée. */
	public int getCell(int row, int col) {
		if (row < nbRow && col < nbCol)
			return table[2 * row + 1][2 * col + 1];
		else
			return -1;
	}

	/**
	 * Définit la valeur d'un mur du terrain en se référant à une case adjacente.
	 * 
	 * @param row   Numéro de ligne de la case adjacente.
	 * @param col   Numéro de colonne de la case adjacente.
	 * @param side  Direction du mur par rapport à la case adjacente.
	 * @param value Valeur associée au mur.
	 */
	public void setWall(int row, int col, Side side, int value) {
		if (col >= 0 && col < nbCol && row >= 0 && row < nbRow) {
			switch (side) {
				case UP:
					if (row > 0)
						table[2 * row][2 * col + 1] = value;
					break;
				case DOWN:
					if (row < nbRow - 1)
						table[2 * row + 2][2 * col + 1] = value;
					break;
				case LEFT:
					if (col > 0)
						table[2 * row + 1][2 * col] = value;
					break;
				case RIGHT:
					if (col < nbCol - 1)
						table[2 * row + 1][2 * col + 2] = value;
					break;
			}
		}
	}

	/**
	 * Fournit la valeur d'un mur en se référant à une case adjacente, où <b>-1</b>
	 * si ce mur n'est pas présent sur le terrain.
	 * 
	 * @param row  Numéro de ligne de la case adjacente.
	 * @param col  Numéro de colonne de la case adjacente.
	 * @param side Direction du mur par rapport à la case adjacente.
	 */
	public int getWall(int row, int col, Side side) {
		if (col >= 0 && col < nbCol && row >= 0 && row < nbRow)
			switch (side) {
				case UP:
					return table[2 * row][2 * col + 1];
				case DOWN:
					return table[2 * row + 2][2 * col + 1];
				case LEFT:
					return table[2 * row + 1][2 * col];
				case RIGHT:
					return table[2 * row + 1][2 * col + 2];
				default:
					return -1;
			}
		else
			return -1;
	}

	/**
	 * Définit la valeur d'une case du terrain.
	 * 
	 * @param row   Numéro de ligne de la case.
	 * @param col   Numéro de colonne de la case.
	 * @param value Valeur de la case.
	 */
	public void set(int row, int col, int value) {
		if (row >= 0 && row <= 2 * nbRow && col >= 0 && col <= 2 * nbCol)
			table[row][col] = value;
	}

	/**
	 * Fournit la valeur de la case du terrain aux coordonnées indiquées.
	 * 
	 * @param row Numéro de ligne de la case.
	 * @param col Numéro de colonne de la case.
	 */
	public int get(int row, int col) {
		if (row >= 0 && row <= 2 * nbRow && col >= 0 && col <= 2 * nbCol)
			return table[row][col];
		else
			return -1;
	}

	/**
	 * Fournit la valeur d'une case voisine à une case donnée dans une direction
	 * donnée (haut, bas, gauche, droite). Les coordonnées de la case et la
	 * direction sont contenues dans l'instance {@link WallCoord}.
	 * 
	 * @param wall Mur localisé sur le terrain.
	 */
	public int getNeighbourCell(WallCoord wall) {
		switch (wall.side) {
			case LEFT:
				return getCell(wall.x, wall.y - 1);
			case RIGHT:
				return getCell(wall.x, wall.y + 1);
			case UP:
				return getCell(wall.x - 1, wall.y);
			case DOWN:
				return getCell(wall.x + 1, wall.y);
			default:
				return -1;
		}
	}

	/**
	 * Affiche le labyrinthe sous forme d'un tableau textuel affichant la valeur de
	 * ses cases et de ses murs.
	 */
	public void display() {
		int N = String.valueOf((2 * nbRow + 1) * (2 * nbCol + 1)).length();
		String format = "%" + N + "d";
		for (int i = 0, lenI = 2 * nbRow + 1; i < lenI; i++) {
			for (int j = 0, lenJ = 2 * nbCol + 1; j < lenJ; j++)
				System.out.print(String.format(format, table[i][j]) + " ");

			System.out.println();
		}
	}

	/**
	 * Efface les valeurs du labyrinthe en apposant la valeur 0 aux cases et la
	 * valeur 1 aux murs.
	 */
	public void clear() {
		for (int i = 0, lenI = table.length; i < lenI; i++) {
			table[i] = new int[2 * nbCol + 1];
			for (int j = 0, lenJ = table[i].length; j < lenJ; j++)
				table[i][j] = (i % 2 == 0) || (j % 2 == 0) ? 1 : 0;
		}
	}

	/**
	 * Enumération des directions possibles d'un mur par rapport à une case du
	 * labyrinthe.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	public enum Side {
		/** Mur situé à gauche de la case. */
		UP,

		/** Mur situé à droite de la case. */
		RIGHT,

		/** Mur situé en bas de la case. */
		DOWN,

		/** Mur situé en haut de la case. */
		LEFT;
	}

	/**
	 * Classe identifiant un mur du terrain. On identifie un mur selon sa position
	 * par rapport à une case adjacente : deux coordonnées localisent la case et une
	 * constante indique si le mur se situe à gauche, à droite, en bas ou en haut de
	 * la case.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	public static class WallCoord {
		public final int x, y; // Coordonnées de la case voisine
		public final Side side; // Position du mur par rapport à la case voisine

		/**
		 * Constructeur de la classe {@link WallCoord}.
		 * 
		 * @param x    Ligne de la case adjacente.
		 * @param y    Colonne de la case adjacente.
		 * @param side Localisation du mur par rapport à la case.
		 */
		public WallCoord(int x, int y, Side side) {
			this.x = x;
			this.y = y;
			this.side = side;
		}

		@Override
		public String toString() {
			return "[" + x + ", " + y + ", " + side.name() + "]";
		}
	}
}