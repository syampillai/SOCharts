package com.storedobject.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InRange implements ComponentProperty {

	private List<String> colors = new ArrayList<>();

	@Override
	public void encodeJSON(StringBuilder sb) {
		if (!colors.isEmpty()) {
			sb.append("\"color\": [");
			var first = new AtomicBoolean(true);
			colors.forEach(c -> {
				if (first.get()) {
					first.set(false);
				} else {
					sb.append(',');
				}
				sb.append(ComponentPart.escape(c));
			});
			sb.append("]");
		}
	}

	public void addColors(String... colorsParam) {
		Arrays.asList(colorsParam).stream().forEach(c -> colors.add(c));
	}

	public List<String> getColors() {
		return colors;
	}
}
