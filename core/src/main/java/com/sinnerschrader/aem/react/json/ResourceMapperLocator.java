package com.sinnerschrader.aem.react.json;

public class ResourceMapperLocator {

	public static ThreadLocal<ResourceMapper> mapperHolder = new ThreadLocal<>();

	public static ResourceMapper getInstance() {
		return mapperHolder.get();
	}

	public static boolean setInstance(ResourceMapper mapper) {
		if (getInstance()!=null) {
			return false;
		}
		mapperHolder.set(mapper);
		return true;
	}

	public static void clearInstance() {
		mapperHolder.remove();
	}

}
