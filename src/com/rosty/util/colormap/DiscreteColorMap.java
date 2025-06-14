package com.rosty.util.colormap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 */
public class DiscreteColorMap<T extends Number> implements ColorMap<T> {
	/** Plage des couleurs triée par ordre croissant des indices. */
	private final SortedMap<T, Color> colorSet = new TreeMap<>();

	@Override
	public void add(T key, Color value) {
		colorSet.put(key, value);
	}

	@Override
	public void delete(T key) {
		colorSet.remove(key);
	}

	@Override
	public Color get(T position) {
		return colorSet.get(position);
	}

	@Override
	public Color getOrDefault(T position, Color defColor) {
		return colorSet.getOrDefault(position, defColor);
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
	public static final <T extends Number> DiscreteColorMap<T> fromString(String str, Class<T> type) {
		DiscreteColorMap<T> map = new DiscreteColorMap<>();

		String regItem = "\\[[^\\;]+;[^\\]]+\\]"; // Chaîne de la forme "[xxx; xxx]"

		String regList = regItem + "(\\s" + regItem + ")*"; // Chaîne de la forme "[xxx; xxx] [yyy; yyy] ..."
		if (!Pattern.matches(regList, str))
			return null;

		Matcher match = Pattern.compile(regItem).matcher(str);
		while (match.find()) {
			String item = match.group();
			String inside = item.substring(1, item.length() - 1);
			String[] keyAndValue = inside.split(";\\s*");

			T key = parse(keyAndValue[0], type);
			Color value = Color.web(keyAndValue[1]);
			map.add(key, value);
		}

		return map;
	}

	@Override
	public final String toString() {
		List<String> items = new ArrayList<>();
		for (Entry<T, Color> k : colorSet.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(k.getKey().toString());
			sb.append("; ");
			sb.append(k.getValue().toString());
			sb.append("]");

			items.add(sb.toString());
		}

		return String.join(" ", items);
	}

	/**
	 * Décode le nombre affiché dans la chaîne de caractères selon le type spécifié.
	 * 
	 * @param s    Chaîne de caractères représentant le nombre.
	 * @param type Classe de destination, héritée de {@link java.lang.Number}.
	 * @return Instance T du nombre.
	 * @throws NumberFormatException Lorsque la lecture du nombre est impossible.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T parse(String s, Class<T> type) throws NumberFormatException {
		if (type == Integer.class)
			return (T) new Integer(Integer.parseInt(s));
		else if (type == Short.class)
			return (T) new Short(Short.parseShort(s));
		else if (type == Long.class)
			return (T) new Long(Long.parseLong(s));
		else if (type == Double.class)
			return (T) new Double(Double.parseDouble(s));
		else
			throw new NumberFormatException("Cannot parse '" + s + "' to any number.");
	}
}