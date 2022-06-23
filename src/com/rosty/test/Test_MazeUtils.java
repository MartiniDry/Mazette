package com.rosty.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rosty.maze.model.Maze;
import com.rosty.maze.model.Maze.Side;
import com.rosty.util.maze.MazeUtils;

public class Test_MazeUtils {
	public static void main(String[] args) {
		List<Integer> tokens = new ArrayList<>();
		tokens.addAll(Arrays.asList(1, 3, 5, 7, 9));

		/* Tableau d'origine */
		int[][] slots = { //
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
				{ 1, 0, 0, 0, 1, 0, 0, 0, 1 }, //
				{ 1, 1, 1, 0, 1, 1, 1, 0, 1 }, //
				{ 1, 0, 0, 0, 0, 0, 1, 0, 1 }, //
				{ 1, 0, 1, 1, 1, 0, 1, 0, 1 }, //
				{ 1, 0, 1, 0, 0, 0, 0, 0, 1 }, //
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1 } //
		};

		/* Collage du tableau dans le labyrinthe */
		Maze maze = new Maze(3, 4);
		for (int i = 0; i < maze.getNbRows(); i++)
			for (int j = 0; j < maze.getNbColumns(); j++)
				maze.setCell(i, j, slots[2 * i + 1][2 * j + 1]);

		for (int i = 1; i < maze.getNbRows(); i++)
			for (int j = 0; j < maze.getNbColumns(); j++)
				maze.setWall(i, j, Side.UP, slots[2 * i][2 * j + 1]);

		for (int i = 0; i < maze.getNbRows(); i++)
			for (int j = 1; j < maze.getNbColumns(); j++)
				maze.setWall(i, j, Side.LEFT, slots[2 * i + 1][2 * j]);

		/* Là, les choses commencent */
		maze.display();

		System.out.println("enclosures: " + MazeUtils.enclosures(maze));
		System.out.println("islets: " + MazeUtils.islets(maze));
		System.out.println("connected: " + MazeUtils.isConnected(maze));
		System.out.println("perfect: " + MazeUtils.isPerfect(maze));
	}
}