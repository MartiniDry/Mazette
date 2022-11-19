package com.rosty.maze.model.algorithm.solving;

import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class WallFollowingAlgorithm extends MazeSolvingAlgorithm {
	public WallFollowingAlgorithm(MazePanel panel) {
		super(panel);
	}
	
	@Override
	public String getLabel() {
		return "wall_following";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
	}
}