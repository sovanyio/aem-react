package com.sinnerschrader.aem.react.json;

import org.apache.sling.api.SlingHttpServletRequest;

public class ResourceMapperLocator {

	public static ThreadLocal<ResourceMapper> mapperHolder = new ThreadLocal<>();

	public static ResourceMapper getInstance() {
		return mapperHolder.get();
	}

	public static boolean setInstance(ResourceMapper mapper) {
		if (getInstance() != null) {
			return false;
		}
		mapperHolder.set(mapper);
		return true;
	}

	public static boolean setInstance(SlingHttpServletRequest request) {
		return setInstance(new ResourceMapper(request));
	}

	public static void clearInstance() {
		mapperHolder.remove();
	}

}
