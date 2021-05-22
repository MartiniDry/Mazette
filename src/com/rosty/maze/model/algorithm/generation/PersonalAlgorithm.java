package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme personnel</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme consiste à balayer les cases de gauche à
 * droite et de haut en bas, en retirant au hasard un mur de la grille (sauf la
 * dernière case). Plus précisément, le processus détermine les murs à enlever
 * en vérifiant que la cellule adjacente correspondante n'est pas connectée à la
 * première cellule de la grille (en haut à gauche).
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Un incrémenteur est créé et initialisé à 2.
 * Considérons la première ligne du labyrinthe : on attribue un numéro unique à
 * chaque case de gauche à droite via l'incrémenteur.<br/>
 * <table border="1">
 * <tr>
 * <th>2</th>
 * <th>3</th>
 * <th>4</th>
 * <th>5</th>
 * <th>6</th>
 * <th>7</th>
 * </tr>
 * <tr>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * </tr>
 * <tr>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * </tr>
 * </table>
 * Du premier au dernier élément de la ligne, on retire un mur au hasard. Chaque
 * fois qu'un mur est enlevé, une phase de fusion est lancée :
 * <ol>
 * <li>Si la cellule adjacente est sans valeur, on lui attribue la valeur de la
 * cellule courante.</li>
 * <li>Si la cellule adjacente possède une valeur, alors on lit toute la ligne
 * courante ; on assigne aux cellules de valeur la plus élevée la valeur la
 * moins élevée.</li>
 * </ol>
 * <table border="1">
 * <tr>
 * <th>2</th>
 * <th>2</th>
 * <th>2</th>
 * <th>2</th>
 * <th>6</th>
 * <th>6</th>
 * </tr>
 * <tr>
 * <th>0</th>
 * <th>2</th>
 * <th>0</th>
 * <th>0</th>
 * <th>6</th>
 * <th>0</th>
 * </tr>
 * <tr>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * </tr>
 * </table>
 * Une fois la première ligne balayée, on passe à la suivante. De gauche à
 * droite, on attribue aux cases sans valeur un numéro en utilisant
 * l'incrémenteur du départ.<br/>
 * <table border="1">
 * <tr>
 * <th>2</th>
 * <th>2</th>
 * <th>2</th>
 * <th>2</th>
 * <th>6</th>
 * <th>6</th>
 * </tr>
 * <tr>
 * <th>8</th>
 * <th>2</th>
 * <th>9</th>
 * <th>10</th>
 * <th>6</th>
 * <th>11</th>
 * </tr>
 * <tr>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * <th>0</th>
 * </tr>
 * </table>
 * A nouveau, toutes les cases sont lues de gauche à droite et on retire un mur
 * au hasard. Mais cette fois, la phase de fusion prend en compte la ligne
 * précédente en plus de la ligne courante (cas n°2 listé précédemment).
 * </p>
 * <p>
 * L'algorithme s'arrête lorsque l'on atteint la dernière case de la grille en
 * bas à droite. C'est la seule case pour laquelle on ne retire aucun mur.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> L'algorithme a une complexité temporelle en O(M.N) car il
 * balaie la grille de gauche à droite et de haut en bas. Il a une complexité
 * mémoire en O(M) ; à chaque étape, seule la ligne courante et celle du dessus
 * sont nécessaires pour fonctionner. La sélection et la suppression du mur est
 * très proche de celle de l'algorithme d'Eller mais évite le phénomène de
 * "couloir latéral" nécessaire au Eller ; de fait, la solution proposée par cet
 * algorithme est plus performante car moins contraignante.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class PersonalAlgorithm extends MazeGenerationAlgorithm {
	/** Numéro qui s'incrémente chaque fois qu'une case de la grille est traitée. */
	private int cellId = 0;
	/**
	 * Numéro identifiant une nouvelle cellule de la grille, pour laquelle aucune
	 * valeur n'est attribuée.
	 */
	private int cellValue = 2;

	/** Générateur de nombres aléatoires. */
	private static final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link PersonalAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public PersonalAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		for (int i = 0; i < nbRow - 1; i++) {
			for (int j = 0; j < nbCol - 1; j++) {
				mazePanel.setWall(i, j, Side.DOWN, 0);
				mazePanel.setWall(i, j, Side.RIGHT, 0);
			}

			mazePanel.setWall(i, nbCol - 1, Side.DOWN, 0);
		}

		for (int j = 0; j < nbCol - 1; j++)
			mazePanel.setWall(nbRow - 1, j, Side.RIGHT, 0);
	}

	@Override
	public boolean isComplete() {
		return cellId >= nbRow * nbCol - 1;
	}

	@Override
	public void step() {
		int i = cellId / nbCol;
		int j = cellId % nbCol;

		/*
		 * Après un saut de ligne, on balaye les cases de la ligne pour effectuer deux
		 * actions :
		 * 
		 * - On attribue un identifiant à chaque case qui ne possède pas d'ID
		 * 
		 * - On ajoute les murs latéraux et les murs du bas.
		 */
		if (j == 0) {
			for (int id = 0; id < nbCol; id++)
				if (mazePanel.getCell(i, id) == 0)
					mazePanel.setCell(i, id, cellValue++);

			for (int id = 0; id < nbCol - 1; id++) {
				mazePanel.setWall(i, id, Side.RIGHT, 1);
				if (i < nbRow)
					mazePanel.setWall(i, id, Side.DOWN, 1);
			}

			if (i < nbRow)
				mazePanel.setWall(i, nbCol - 1, Side.DOWN, 1);
		}

		// Détermination des murs qui peuvent être détruits à cette étape de
		// l'algorithme

		List<Side> availableWalls = new ArrayList<>();

		if (i < nbRow - 1)
			availableWalls.add(Side.DOWN);

		int value = mazePanel.getCell(i, j);
		int nValue = -1; // Valeur de la case voisine distante de la case actuelle du mur que l'on
							// retirera (wow, je ne sais même pas si c'est français :O)

		if (i > 0)
			if (value != mazePanel.getNeighbourCell(new WallCoord(i, j, Side.UP)))
				availableWalls.add(Side.UP);

		if (j > 0)
			if (value != mazePanel.getNeighbourCell(new WallCoord(i, j, Side.LEFT)))
				availableWalls.add(Side.LEFT);

		if (j < nbCol - 1)
			if (value != mazePanel.getNeighbourCell(new WallCoord(i, j, Side.RIGHT)))
				availableWalls.add(Side.RIGHT);

		int r = rand.nextInt(availableWalls.size());
		Side direction = availableWalls.get(r);
		mazePanel.setWall(i, j, direction, 0);

		// Mise à jour des ID : pour chaque case de la ligne courante et la ligne du
		// dessus ayant l'ID le plus haut, l'ID le plus bas sera assigné. Il est inutile
		// d'effectuer cela si le mur du bas a été retiré.

		switch (direction) {
		case DOWN:
			mazePanel.setCell(i + 1, j, value);
			break;
		case LEFT:
		case RIGHT:
		case UP:
			nValue = mazePanel.getNeighbourCell(new WallCoord(i, j, direction));
		default:
			if (nValue != -1) {
				int min = Math.min(value, nValue);
				int max = Math.max(value, nValue);
				for (int id = 0; id < nbCol; id++)
					if (mazePanel.getCell(i, id) == max)
						mazePanel.setCell(i, id, min);

				if (i > 0)
					for (int id = 0; id < nbCol; id++)
						if (mazePanel.getCell(i - 1, id) == max)
							mazePanel.setCell(i - 1, id, min);

				if (i < nbRow - 1)
					for (int id = 0; id < nbCol; id++)
						if (mazePanel.getCell(i + 1, id) == max)
							mazePanel.setCell(i + 1, id, min);
			}

			break;
		}

		cellId++;
	}
}