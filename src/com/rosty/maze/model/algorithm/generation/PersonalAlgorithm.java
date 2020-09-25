package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class PersonalAlgorithm extends MazeGenerationAlgorithm {
	private int cellId = 0;
	private int cellValue = 2;

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

//		System.out.println(i + " " + j);

		/*
		 * Après un saut de ligne, on balaye les cases de la ligne pour effectuer deux
		 * actions :
		 * 
		 * * On attribue un identifiant à chaque case qui ne possède pas d'ID
		 * 
		 * * On ajoute les murs latéraux et les murs du bas.
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

		if (i > 0) {
			if (value != mazePanel.getNeighbourCell(new WallCoord(i, j, Side.UP)))
				availableWalls.add(Side.UP);
		}

		if (j > 0) {
			if (value != mazePanel.getNeighbourCell(new WallCoord(i, j, Side.LEFT)))
				availableWalls.add(Side.LEFT);
		}

		if (j < nbCol - 1) {
			if (value != mazePanel.getNeighbourCell(new WallCoord(i, j, Side.RIGHT)))
				availableWalls.add(Side.RIGHT);
		}

//		System.out.println(Arrays.toString(availableWalls.toArray()));

		int r = rand.nextInt(availableWalls.size());
		Side direction = availableWalls.get(r);
		mazePanel.setWall(i, j, direction, 0);

//		System.out.println(direction + " enlevé !");

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

//		mazePanel.display();

		cellId++;
//		System.out.println();
	}
}