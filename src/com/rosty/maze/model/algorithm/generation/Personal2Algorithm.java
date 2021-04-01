package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.Random;

import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class Personal2Algorithm extends MazeGenerationAlgorithm {
	private ArrayList<WallCoord> startToEndPath, endToStartPath;
	private Mode mode = Mode.DRAFT;

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	public Personal2Algorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 0);

		startToEndPath = new ArrayList<>();
		endToStartPath = new ArrayList<>();
	}

	@Override
	public boolean isComplete() {
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) != 2)
					return false;

		return true;
	}

	@Override
	public void step() {
		switch (mode) {
		case DRAFT:
			// Listage de toutes les cellules non encore explorées
			ArrayList<int[]> unexploredCells = new ArrayList<>();
			for (int i = 0; i < nbRow; i++)
				for (int j = 0; j < nbCol; j++)
					if (mazePanel.getCell(i, j) == 0)
						unexploredCells.add(new int[] { i, j });

			if (unexploredCells.size() == 1) {
				;
			} else if (!unexploredCells.isEmpty()) { // Equivalent à "size > 1"
				;
			} else
				return;

			break;
		case WRITING:
			break;
		case LAST_ONE_OUT:
			break;
		case GATHERING:
			break;
		default:
			break;
		}
	}

	enum Mode {
		DRAFT, WRITING, LAST_ONE_OUT, GATHERING
	}
}