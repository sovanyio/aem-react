package com.sinnerschrader.aem.react.metrics;

import com.sinnerschrader.aem.react.DefaultComponentMetrics;

public class MetricsHelper {
	public static ComponentMetrics getCurrent() {
		ComponentMetrics current = DefaultComponentMetrics.getCurrent();
		if (current == null) {
			return ComponentMetricsService.NO_OP;
		}
		return current;
	}
}
