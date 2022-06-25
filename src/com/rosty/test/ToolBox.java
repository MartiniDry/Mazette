package com.rosty.test;

import com.rosty.maze.model.Maze;
import com.rosty.maze.model.Maze.Side;

public class ToolBox {
	static Maze parse(int[][] table) {
		if (table.length % 2 == 0)
			return null;

		if (table[0].length % 2 == 0)
			return null;

		int rows = table.length / 2;
		int cols = table[0].length / 2;

		Maze maze = new Maze(rows, cols);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				maze.setCell(i, j, table[2 * i + 1][2 * j + 1]);

		for (int i = 1; i < rows; i++)
			for (int j = 0; j < cols; j++)
				maze.setWall(i, j, Side.UP, table[2 * i][2 * j + 1]);

		for (int i = 0; i < rows; i++)
			for (int j = 1; j < cols; j++)
				maze.setWall(i, j, Side.LEFT, table[2 * i + 1][2 * j]);

		return maze;
	}
}