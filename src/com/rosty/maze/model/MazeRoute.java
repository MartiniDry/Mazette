package com.rosty.maze.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Classe définissant un chemin parcouru dans un labyrinthe. Ce chemin est
 * défini par un point de départ, une série de cases successives et un point
 * d'arrivée.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class MazeRoute {
	/** Position des points de départ et d'arrivée */
	private final int[] start = { 0, 0 }, end = { 0, 0 };

	/** Liste de cellules composant le chemin dans le labyrinthe */
	private final ObservableList<int[]> path = FXCollections.observableArrayList();

	public int[] getStart() {
		return start;
	}

	public void setStart(int i, int j) {
		start[0] = i;
		start[1] = j;
	}

	public int[] getEnd() {
		return end;
	}

	public void setEnd(int i, int j) {
		end[0] = i;
		end[1] = j;
	}

	public ObservableList<int[]> getPath() {
		return path;
	}
}