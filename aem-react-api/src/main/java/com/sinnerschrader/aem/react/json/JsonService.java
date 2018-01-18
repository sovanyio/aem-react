package com.sinnerschrader.aem.react.json;

import org.apache.sling.api.SlingHttpServletRequest;

public interface JsonService {
	public String write(Object value, SlingHttpServletRequest servletRequest);
}
