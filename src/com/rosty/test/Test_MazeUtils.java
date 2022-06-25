package com.rosty.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.rosty.maze.model.Maze;
import com.rosty.util.maze.MazeUtils;

class Test_MazeUtils {
	@Test
	void test1() {
		int[][] slots = { //
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
				{ 1, 0, 0, 0, 1, 0, 0, 0, 1 }, //
				{ 1, 1, 1, 0, 1, 1, 1, 0, 1 }, //
				{ 1, 0, 0, 0, 0, 0, 1, 0, 1 }, //
				{ 1, 0, 1, 1, 1, 0, 1, 0, 1 }, //
				{ 1, 0, 1, 0, 0, 0, 0, 0, 1 }, //
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1 } //
		};

		Maze maze = ToolBox.parse(slots);
		maze.display();

		int enc = MazeUtils.enclosures(maze);
		assertEquals(enc, 1, "enclosures: " + enc);

		int isl = MazeUtils.islets(maze);
		assertEquals(isl, 0, "islets: " + isl);

		boolean conn = MazeUtils.isConnected(maze);
		assertEquals(conn, true, "connected: " + conn);

		boolean perf = MazeUtils.isPerfect(maze);
		assertEquals(perf, true, "perfect: " + perf);
	}

	@Test
	void test2() {
		int[][] slots = { //
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
				{ 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 }, //
				{ 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1 }, //
				{ 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 }, //
				{ 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1 }, //
				{ 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1 }, //
				{ 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1 }, //
				{ 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1 }, //
				{ 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1 }, //
				{ 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1 }, //
				{ 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1 }, //
				{ 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1 }, //
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } //
		};

		Maze maze = ToolBox.parse(slots);
		maze.display();

		int enc = MazeUtils.enclosures(maze);
		assertEquals(enc, 6, "enclosures: " + enc);

		int isl = MazeUtils.islets(maze);
		assertEquals(isl, 1, "islets: " + isl);

		boolean conn = MazeUtils.isConnected(maze);
		assertEquals(conn, false, "connected: " + conn);

		boolean perf = MazeUtils.isPerfect(maze);
		assertEquals(perf, false, "perfect: " + perf);
	}
}