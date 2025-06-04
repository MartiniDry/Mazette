package com.rosty.maze.model.algorithm.solving;

import java.util.ArrayList;
import java.util.Arrays;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeSolvingAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class SoukupAlgorithm extends MazeSolvingAlgorithm {
	/** Coordonnées de l'explorateur dans la grille. */
	private int[] explorer = new int[2];
	/** Chemin sauvegardé pour l'algorithme. */
	ArrayList<PathPoint> path = new ArrayList<PathPoint>();

	/**
	 * Constructeur de la classe {@link SoukupAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public SoukupAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ".soukup";
	}

	@Override
	public void init() {
		explorer = mazePanel.getStart();
		mazePanel.setCell(explorer[0], explorer[1], 2);
		
		PathPoint pathPt = new PathPoint(explorer.clone(), Side.RIGHT, ReachedBy.STRAIGHT);
		path.add(pathPt);
	}

	@Override
	public boolean isComplete() {
		return Arrays.equals(explorer, mazePanel.getEnd());
	}

	@Override
	public void step() {
		doStraight();
	}

	@Override
	public void finish() {
		super.finish();
	}
	
	boolean doStraight() {
		// Choix de la direction où aller
		Side whereToGo = null;
		
		int deltaI = mazePanel.getEnd()[0] - explorer[0];
		if (deltaI > 0)
			whereToGo = Side.RIGHT;
		else if (deltaI < 0)
			whereToGo = Side.LEFT;
		else {
			int deltaJ = mazePanel.getEnd()[1] - explorer[1];
			if (deltaJ > 0)
				whereToGo = Side.DOWN;
			else if (deltaJ < 0)
				whereToGo = Side.UP;
		}
		
		if (whereToGo != null && mazePanel.getWall(explorer[0], explorer[1], whereToGo) != 1) {
			move(whereToGo);
			System.out.println("True");
			return true;
		}

		System.out.println("False");
		return false;
	}
	
	void doLee() {
		;
	}

	/** Déplace l'explorateur d'une case dans la direction spécifiée. */
	private void move(Side side) {
		switch (side) {
			case UP:
				explorer[0]--;
				break;
			case LEFT:
				explorer[1]--;
				break;
			case DOWN:
				explorer[0]++;
				break;
			case RIGHT:
				explorer[1]++;
				break;
			default:
				break;
		}
	}
	
	class PathPoint {
		public int[] point;
		public Side signal;
		public ReachedBy reach;
		
		public PathPoint(int[] pt, Side sig, ReachedBy r) {
			this.point = pt;
			this.signal = sig;
			this.reach = r;
		}
	}
	
	enum ReachedBy {
		NONE,
		LEE, 
		STRAIGHT
	}
}