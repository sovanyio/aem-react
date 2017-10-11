package com.sinnerschrader.aem.react.json;

import org.apache.sling.api.resource.ResourceResolver;

public class ResourceMapper {

	public static ThreadLocal<ResourceResolver> resourceResolver = new ThreadLocal<>();

	public static ResourceResolver getInstance() {
		return resourceResolver.get();
	}

	public static void setInstance(ResourceResolver resolver) {
		resourceResolver.set(resolver);
	}

	public static void clearInstance() {
		resourceResolver.remove();
	}

}
