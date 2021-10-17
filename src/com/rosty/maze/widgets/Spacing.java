package com.rosty.maze.widgets;

import javafx.beans.NamedArg;
import javafx.geometry.Insets;

public class Spacing extends Insets {
	public Spacing(@NamedArg("topRightBottomLeft") double s) {
		super(s);
	}

	public Spacing(@NamedArg("topBottom") double s1, @NamedArg("leftRight") double s2) {
		super(s1, s2, s1, s2);
	}

	public Spacing(@NamedArg("top") double s1, @NamedArg("right") double s2, @NamedArg("bottom") double s3,
			@NamedArg("left") double s4) {
		super(s1, s2, s3, s4);
	}

	public static Spacing valueOf(String info) {
		String[] data = info.split(" "); // S�paration des donn�es
		// Parsing des donn�es et r�cup�ration
		double[] sides = new double[data.length];
		for (int i = 0; i < data.length; i++)
			try {
				sides[i] = Double.parseDouble(data[i]); // R�cup�ration des donn�es
			} catch (NumberFormatException e) {
				sides[i] = 0;
			}

		switch (sides.length) {
			case 0:
				return new Spacing(0);
			case 1:
				return new Spacing(sides[0]);
			case 2:
				return new Spacing(sides[0], sides[1]);
			case 3:
				return new Spacing(sides[0], sides[1], sides[2], sides[1]);
			case 4:
				return new Spacing(sides[0], sides[1], sides[2], sides[3]);
			default:
				return null;
		}
	}

	/**
	 * Affiche l'espacement sous forme d'une cha�ne de caract�res.
	 * 
	 * @return Repr�sentation de l'espacement.
	 */
	@Override
	public String toString() {
		return "Spacing [top=" + getTop() + ", right=" + getRight() + ", bottom=" + getBottom() + ", left=" + getLeft()
				+ "]";
	}
}