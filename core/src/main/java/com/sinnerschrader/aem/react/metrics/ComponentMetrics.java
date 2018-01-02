package com.sinnerschrader.aem.react.metrics;

import java.util.concurrent.Callable;

public interface ComponentMetrics extends AutoCloseable {

	void timer(String name);

	<T> T timer(String name, Callable<T> callable);

	void timerEnd(String name);

}