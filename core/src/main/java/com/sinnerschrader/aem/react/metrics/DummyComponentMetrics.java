package com.sinnerschrader.aem.react.metrics;

import java.util.concurrent.Callable;

import com.sinnerschrader.aem.react.exception.TechnicalException;

public class DummyComponentMetrics implements ComponentMetrics {

	@Override
	public void close() throws Exception {

	}

	@Override
	public void timer(String name) {

	}

	@Override
	public <T> T timer(String name, Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new TechnicalException("cannot call callable ", e);
		}
	}

	@Override
	public void timerEnd(String name) {
	}

}
