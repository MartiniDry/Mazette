package com.rosty.test;

import com.rosty.maze.model.Maze;
import com.rosty.maze.model.algorithm.generation.PersonalAlgorithm;
import com.rosty.maze.widgets.MazePanel;
import com.rosty.util.maze.MazeUtils;

public class TestPerfo {
	public static void main(String[] args) {
		int row = 400;
		int col = 600;
		int tries = 5;

		Maze maze = new Maze(row, col);
		MazePanel panel = new MazePanel();
		panel.setMaze(maze);

		PersonalAlgorithm algo = new PersonalAlgorithm(panel);
		algo.init();
		while (!algo.isComplete())
			algo.step();

		System.out.println("ENCLOS");
		long tic = System.nanoTime();

		int enc = -1;
		for (int i = 0; i < tries; i++)
			enc = MazeUtils.enclosures(maze);
		
		System.out.println("Il y a " + enc + " enclos !");

		long toc = System.nanoTime();
		System.out.println("Temps écoulé : " + (toc - tic) / (tries * 1000000000.) + " ms");

		System.out.println();
		System.out.println("ENCLOS (LIGHT)");
		long tic2 = System.nanoTime();

		int enc2 = -1;
		for (int i = 0; i < tries; i++)
			enc2 = MazeUtils.enclosures_light(maze);
		
		System.out.println("Il y a " + enc2 + " enclos !");

		long toc2 = System.nanoTime();
		System.out.println("Temps écoulé : " + (toc2 - tic2) / (tries * 1000000000.) + " ms");
	}
}