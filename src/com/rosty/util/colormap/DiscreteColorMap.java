package com.rosty.util.colormap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;

public class DiscreteColorMap {
	private final NavigableMap<Integer, Color> map = new TreeMap<>();

	public Color get(int key) {
		return map.get(key);
	}

	public Color put(int key, Color value) {
		return map.put(key, value);
	}

	public void putAll(Map<Integer, Color> map) {
		map.putAll(map);
	}

	public Color remove(int key) {
		return map.remove(key);
	}

	public boolean remove(int key, Color value) {
		return map.remove(key, value);
	}

	public Set<Integer> keySet() {
		return map.keySet();
	}

	public Collection<Color> values() {
		return map.values();
	}

	public boolean containsKey(int key) {
		return map.containsKey(key);
	}

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
			map.put(key, value);
		}

		return map;
	}

	@Override
	public final String toString() {
		List<String> items = new ArrayList<>();
		for (Integer k : map.keySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(k.intValue());
			sb.append("; ");
			sb.append(map.get(k).toString());
			sb.append("]");

			items.add(sb.toString());
		}

		return String.join(" ", items);
	}
}