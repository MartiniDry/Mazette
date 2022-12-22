package com.rosty.test;

import java.util.function.Function;

import com.rosty.maze.model.Maze;
import com.rosty.maze.model.algorithm.generation.PersonalAlgorithm;
import com.rosty.maze.widgets.MazePanel;
import com.rosty.util.maze.MazeUtils;

public class TestPerfo {
	private static final int TRIES = 30;

	public static void main(String[] args) throws InterruptedException {
		int row = 500;
		int col = 700;

		System.out.println("Instanciation Java");
		Maze maze = new Maze(row, col);
		System.out.println("Instanciation JavaFX");
		MazePanel panel = new MazePanel();
		panel.setMaze(maze);

		System.out.println(String.format("Génération d'un labyrinthe (%d,%d)", row, col));
		PersonalAlgorithm algo = new PersonalAlgorithm(panel);
		algo.init();
		while (!algo.isComplete())
			algo.step();
		
		System.out.println();
		
		enclosureBench("ENCLOS", MazeUtils::enclosures, maze);
		enclosureBench("ENCLOS (LIGHT)", MazeUtils::enclosures_light, maze);
		enclosureBench("ENCLOS (LIGHTER)", MazeUtils::enclosures_lighter, maze);
		enclosureBench("ENCLOS (ALTERNATIVE LIGHTER)", MazeUtils::enclosures_alternative_lighter, maze);
		enclosureBench("ENCLOS (LIGHTEST)", MazeUtils::enclosures_lightest, maze);
	}

	private static void enclosureBench(String title, Function<Maze, Integer> counter, Maze data) {
		System.out.print(title);
		long tic = System.nanoTime();

		int enc = -1;
		for (int i = 0; i < TRIES; i++)
			enc = counter.apply(data);

		System.out.println(" : il y a " + enc + " enclos !");

		long toc = System.nanoTime();
		System.out.println("    -> temps écoulé : " + (toc - tic) / (TRIES * 1E6D) + " ms");
	}
}