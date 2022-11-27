package com.rosty.maze.model.algorithm.solving;

import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class AStarAlgorithm extends MazeSolvingAlgorithm {
	public AStarAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return "a_star";
	}

	@Override
	public void init() {
		;
	}

	@Override
	public boolean isComplete() {
		return false;
	}

	@Override
	public void step() {
		;
	}
}