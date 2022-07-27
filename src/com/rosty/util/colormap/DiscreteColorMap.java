package com.rosty.util.colormap;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rosty.util.javafx.ColorUtils;

import javafx.scene.paint.Color;

/**
 * Classe représentant une plage de couleurs discrète : à chaque clé (entier
 * 32bits) est associée une valeur bien définie (instance {@link Color}). Cette
 * plage est définie en plaçant les couleurs une par une dans une <b>carte</b>.
 * Exemple :
 * 
 * <pre>
 * ColorMap<Integer> palette = new DiscreteColorMap();
 * palette.add(2, Color.GREEN);
 * palette.add(1, Color.GREEN);
 * palette.add(0, Color.RED);
 * </pre>
 * <p>
 * La carte est triée par ordre croissant des clés.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 * @see {@link ColorUtils}
 */
public class DiscreteColorMap implements ColorMap<Integer> {
	/** Plage des couleurs triée par ordre croissant des indices. */
	private final SortedMap<Integer, Color> colorSet = new TreeMap<>();

	@Override
	public void add(Integer key, Color value) {
		colorSet.put(key, value);
	}

	@Override
	public void delete(Integer key) {
		colorSet.remove(key);
	}

	@Override
	public Color get(Integer position) {
		return colorSet.get(position);
	}

	@Override
	public void clear() {
		colorSet.clear();
	}

	/**
	 * Construit une instance {@link DiscreteColorMap} à partir d'une chaîne de
	 * caractères (ou la valeur <code>null</code> si le <i>parsing</i> a échoué).
	 * <p>
	 * Les règles de syntaxe sont les suivantes :
	 * <ul>
	 * <li>Pour remplir la carte des couleurs, on insère des éléments de la façon
	 * suivante : <code>[N; COLOR]</code>.</li>
	 * </ul>
	 * <ul>
	 * <li>Les éléments de la carte sont espacés entre eux.<br/>
	 * Exemple : <code>[0; black] [2; rgb(42,21,68)] [5; #0FF]</code>.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param str Chaîne de caractères représentant une instance
	 *            {@link DiscreteColorMap}.
	 */
	public static final DiscreteColorMap fromString(String str) {
		DiscreteColorMap map = new DiscreteColorMap();

		String regex = "\\[-?\\d+;\\s*(\\w+|#[a-fA-F0-9]+)\\]";
		Matcher match = Pattern.compile(regex).matcher(str);
		while (match.find()) {
			String item = match.group();
			String inside = item.substring(1, item.length() - 1);
			String[] keyAndValue = inside.split(";\\s*");

			int key = Integer.parseInt(keyAndValue[0]);
			Color value = Color.web(keyAndValue[1]);
			map.add(key, value);
		}

		return map;
	}

	@Override
	public final String toString() {
		List<String> items = new ArrayList<>();
		for (Integer k : colorSet.keySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(k.intValue());
			sb.append("; ");
			sb.append(colorSet.get(k).toString());
			sb.append("]");

			items.add(sb.toString());
		}

		return String.join(" ", items);
	}
}