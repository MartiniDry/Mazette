package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h2>Algorithme de Kruskal non-trié</h2>
 * 
 * <p>
 * <h1>Principe</h1>L'algorithme reprend le principe l'algorithme de Kruskal
 * standard (cf. la classe {@link KruskalAlgorithm}). La différence se trouve
 * dans l'analyse des murs intérieurs du terrain, pour déterminer ou non si
 * celui-ci peut être enlevé :
 * <ul>
 * <li>Dans l'algorithme standard, on analyse les murs de façon aléatoire, et on
 * le retire de la liste lorsque celui-ci a été visité.</li>
 * <li>Dans l'algorithme non-trié, la liste des murs est préalablement mélangée
 * et on analyse les murs en balayant la liste de gauche à droite.</li>
 * </ul>
 * La différence avec l'autre algorithme se fait donc sur les performances lors
 * de l'exécution ; alors que l'algorithme standard sélectionne puis enlève un
 * élément aléatoire dans la liste des murs, cet algorithme traite en permanence
 * le premier élément de la liste. Si l'accès aux données permet de gagner du
 * temps de calcul, il y a une perte de performances à cause du fait que l'on
 * retire le premier élément de la liste.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class ShuffledKruskalAlgorithm extends MazeGenerationAlgorithm {
	List<WallCoord> walls = new ArrayList<>();

	/**
	 * Constructeur de la classe {@link ShuffledKruskalAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public ShuffledKruskalAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		walls.clear();
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 2 + i * nbCol + j);

		for (int i = 0, lenI = nbRow - 1; i < lenI; i++) {
			for (int j = 0, lenJ = nbCol - 1; j < lenJ; j++)
				walls.add(new WallCoord(i, j, Side.RIGHT));

			for (int j = 0; j < nbCol; j++)
				walls.add(new WallCoord(i, j, Side.DOWN));
		}

		for (int j = 0, lenJ = nbCol - 1; j < lenJ; j++)
			walls.add(new WallCoord(nbRow - 1, j, Side.RIGHT));

		Collections.shuffle(walls);
	}

	@Override
	public boolean isComplete() {
		return walls.isEmpty();
	}

	@Override
	public void step() {
		WallCoord wall = walls.get(0);

		int cell = mazePanel.getCell(wall.x, wall.y);
		int neighbour = mazePanel.getNeighbourCell(wall);

		while (cell == neighbour) {
			walls.remove(0);
			if (walls.isEmpty())
				return;

			wall = walls.get(0);

			cell = mazePanel.getCell(wall.x, wall.y);
			neighbour = mazePanel.getNeighbourCell(wall);
		}

		mazePanel.setWall(wall.x, wall.y, wall.side, 0);

		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) == neighbour)
					mazePanel.getMaze().setCell(i, j, cell);
	}
}
