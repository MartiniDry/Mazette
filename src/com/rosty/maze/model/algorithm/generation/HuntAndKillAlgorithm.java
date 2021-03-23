package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class HuntAndKillAlgorithm extends MazeGenerationAlgorithm {
	private int x /* ligne */, y /* colonne */;
	private Mode mode = Mode.KILL;
	private int lineLevel = 0;
	private boolean vertical = true;
	private final Random rand = new Random();

	public HuntAndKillAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 0);

		x = rand.nextInt(nbRow - 1);
		y = rand.nextInt(nbCol - 1);

		mazePanel.getMaze().setCell(x, y, 1);
	}

	@Override
	public boolean isComplete() {
		return mode == Mode.HUNT && ((vertical && lineLevel == nbRow) || (!vertical && lineLevel == nbCol));
	}

	@Override
	public void step() {
		switch (mode) {
		case HUNT:
			boolean candidateLine = false;
			int index = -1;
			ArrayList<WallCoord> visitedSides = null;

			if (vertical)
				for (int j = 0; j < nbCol; j++) {
					boolean unvisitedCell = (mazePanel.getCell(lineLevel, j) == 0);
					visitedSides = getSides(lineLevel, j);
					visitedSides.removeIf(wc -> {
						int neighbour = mazePanel.getNeighbourCell(wc);
						return neighbour != 2 && neighbour != 1;
					});

					if (unvisitedCell && !visitedSides.isEmpty()) {
						candidateLine = true;
						index = j;
						break;
					}
				}
			else
				for (int i = 0; i < nbRow; i++) {
					boolean unvisitedCell = (mazePanel.getCell(i, lineLevel) == 0);
					visitedSides = getSides(i, lineLevel);
					visitedSides.removeIf(wc -> {
						int neighbour = mazePanel.getNeighbourCell(wc);
						return neighbour != 2 && neighbour != 1;
					});

					if (unvisitedCell && !visitedSides.isEmpty()) {
						candidateLine = true;
						index = i;
						break;
					}
				}

			if (candidateLine) {
				mode = Mode.KILL;
				
				if (vertical) {
					x = lineLevel;
					y = index;
				} else {
					x = index;
					y = lineLevel;
				}

				mazePanel.setCell(x, y, 1);
				if (visitedSides != null) { // Simple precaution.
					WallCoord wall = visitedSides.get(rand.nextInt(visitedSides.size()));
					mazePanel.setWall(wall.x, wall.y, wall.side, 0);
				}

				lineLevel = 0;
			} else
				lineLevel++;

			break;
		case KILL:
			// Repérage des directions à explorer
			List<WallCoord> unexplored = new ArrayList<>();

			List<WallCoord> sides = getSides(x, y);
			for (WallCoord side : sides) {
				int sideValue = mazePanel.getNeighbourCell(side);
				if (sideValue != -1 /* hors-grille, c'est une simple sécurité */
						&& sideValue != 1 /* case explorée */
						&& sideValue != 2 /* case explorée mais non coloriée */)
					unexplored.add(side);
			}

			// Phase d'exploration (ou de rembobinage)
			mazePanel.setCell(x, y, 2); // Case courante marquée comme explorée
			if (!unexplored.isEmpty()) {
				WallCoord selectedSide = unexplored.get(rand.nextInt(unexplored.size()));
				move(selectedSide.side);

				mazePanel.setCell(x, y, 1); // Nouvelle case explorée
				// Brisage du mur
				mazePanel.setWall(selectedSide.x, selectedSide.y, selectedSide.side, 0);
			} else {
				mode = Mode.HUNT;
			}

			break;
		default:
			break;
		}
	}

	private ArrayList<WallCoord> getSides(int i, int j) {
		ArrayList<WallCoord> sides = new ArrayList<>();
		if (i > 0)
			sides.add(new WallCoord(i, j, Side.UP));

		if (j > 0)
			sides.add(new WallCoord(i, j, Side.LEFT));

		if (i < nbRow - 1)
			sides.add(new WallCoord(i, j, Side.DOWN));

		if (j < nbCol - 1)
			sides.add(new WallCoord(i, j, Side.RIGHT));

		return sides;
	}
	
	private void move(Side direction) {
		switch (direction) {
		case UP:
			x--;
			break;
		case LEFT:
			y--;
			break;
		case DOWN:
			x++;
			break;
		case RIGHT:
			y++;
			break;
		default:
			break;
		}
	}

	private enum Mode {
		HUNT, KILL;
	}
}