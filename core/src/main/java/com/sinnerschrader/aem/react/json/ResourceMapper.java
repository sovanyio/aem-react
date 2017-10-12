package com.sinnerschrader.aem.react.json;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

public class ResourceMapper {

	private SlingHttpServletRequest request;
	private ResourceResolver resolver;

	public ResourceMapper(SlingHttpServletRequest request) {
		this.resolver = request.adaptTo(ResourceResolver.class);
		this.request = request;
	}

	public String map(String text) {
		return resolver.map(request, text);
	}

}
