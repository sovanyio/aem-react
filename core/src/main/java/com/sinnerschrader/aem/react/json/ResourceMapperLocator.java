package com.sinnerschrader.aem.react.json;

public class ResourceMapperLocator {

	public static ThreadLocal<ResourceMapper> mapperHolder = new ThreadLocal<>();

	public static ResourceMapper getInstance() {
		return mapperHolder.get();
	}

	public static void setInstance(ResourceMapper mapper) {
		mapperHolder.set(mapper);
	}

	public static void clearInstance() {
		mapperHolder.remove();
	}

}
