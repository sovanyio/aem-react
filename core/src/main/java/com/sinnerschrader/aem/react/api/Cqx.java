package com.sinnerschrader.aem.react.api;

import com.adobe.granite.xss.XSSAPI;

/**
 *
 * An instance of this class is made available to the js script engine.
 *
 * @author stemey
 *
 */
public class Cqx {

	private Sling sling;
	private OsgiServiceFinder finder;
	private ModelFactory modelFactory;
	private XSSAPI xssApi;

	public Cqx(Sling sling, OsgiServiceFinder finder, ModelFactory modelFactory, XSSAPI xssApi) {
		super();
		this.sling = sling;
		this.finder = finder;
		this.modelFactory = modelFactory;
		this.xssApi = xssApi;
	}

	/**
	 * get an osgi service
	 *
	 * @param name
	 *            fully qualified class name
	 * @return
	 */
	public JsProxy getOsgiService(String name) {
		return finder.get(name);
	}

	/**
	 * get a request sling model
	 *
	 * @param name
	 *            fully qualified class name
	 * @return
	 */
	public JsProxy getRequestModel(String path, String name) {
		return modelFactory.createRequestModel(path, name);
	}

	/**
	 * get a resource sling model
	 *
	 * @param name
	 *            fully qualified class name
	 * @return
	 */
	public JsProxy getResourceModel(String path, String name) {
		return modelFactory.createResourceModel(path, name);
	}

	/**
	 * get access to resource via the sling objects
	 *
	 * @return
	 */
	public Sling getSling() {
		return sling;
	}

	public XSSAPI getXssApi() {
		return xssApi;
	}

}
