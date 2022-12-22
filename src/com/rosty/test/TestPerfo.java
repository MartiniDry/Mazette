package com.rosty.test;

import com.rosty.maze.model.Maze;
import com.rosty.maze.model.algorithm.generation.PersonalAlgorithm;
import com.rosty.maze.widgets.MazePanel;
import com.rosty.util.maze.MazeUtils;

public class TestPerfo {
	public static void main(String[] args) throws InterruptedException {
		int row = 500;
		int col = 700;
		int tries = 30;

		System.out.println("Instanciation Java...");
		Maze maze = new Maze(row, col);
		System.out.println("Instanciation JavaFX...");
		MazePanel panel = new MazePanel();
		panel.setMaze(maze);

		System.out.println(String.format("Génération d'un labyrinthe (%s,%s)...", row, col));
		PersonalAlgorithm algo = new PersonalAlgorithm(panel);
		algo.init();
		while (!algo.isComplete())
			algo.step();

		System.out.println("Pause de 1s pour assurer une vitesse de calcul CPU assez constante.");
		Thread.sleep(1000);

		System.out.println("ENCLOS (LIGHT)");
		long tic = System.nanoTime();

		int enc = -1;
		for (int i = 0; i < tries; i++)
			enc = MazeUtils.enclosures(maze);

		System.out.println("Il y a " + enc + " enclos !");

		long toc = System.nanoTime();
		System.out.println("Temps écoulé : " + (toc - tic) / (tries * 1E6D) + " ms");

		System.out.println();
		System.out.println("ENCLOS");
		long tic2 = System.nanoTime();

		int enc2 = -1;
		for (int i = 0; i < tries; i++)
			enc2 = MazeUtils.enclosures_light(maze);

		System.out.println("Il y a " + enc2 + " enclos !");

		long toc2 = System.nanoTime();
		System.out.println("Temps écoulé : " + (toc2 - tic2) / (tries * 1E6D) + " ms");

		System.out.println();
		System.out.println("ENCLOS (LIGHTER)");
		long tic3 = System.nanoTime();

		int enc3 = -1;
		for (int i = 0; i < tries; i++)
			enc3 = MazeUtils.enclosures_lighter(maze);

		System.out.println("Il y a " + enc3 + " enclos !");

		long toc3 = System.nanoTime();
		System.out.println("Temps écoulé : " + (toc3 - tic3) / (tries * 1E6D) + " ms");

		System.out.println();
		System.out.println("ENCLOS (ALTERNATIVE LIGHTER)");
		long tic4 = System.nanoTime();

		int enc4 = -1;
		for (int i = 0; i < tries; i++)
			enc4 = MazeUtils.enclosures_alternative_lighter(maze);

		System.out.println("Il y a " + enc4 + " enclos !");

		long toc4 = System.nanoTime();
		System.out.println("Temps écoulé : " + (toc4 - tic4) / (tries * 1E6D) + " ms");

		System.out.println();
		System.out.println("ENCLOS (LIGHTEST)");
		long tic5 = System.nanoTime();

		int enc5 = -1;
		for (int i = 0; i < tries; i++)
			enc5 = MazeUtils.enclosures_lightest(maze);

		System.out.println("Il y a " + enc5 + " enclos !");

		long toc5 = System.nanoTime();
		System.out.println("Temps écoulé : " + (toc5 - tic5) / (tries * 1E6D) + " ms");
	}
}