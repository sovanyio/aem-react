package com.sinnerschrader.aem.react;

import java.util.HashMap;
import java.util.Map;

public class Cqx {

  public Map<String, Object> objects = new HashMap<>();
  public Sling sling;
  public OsgiServiceFinder finder;

  public Cqx(Map<String, Object> objects, Sling sling, OsgiServiceFinder finder) {
    super();
    this.objects = objects;
    this.sling = sling;
    this.finder = finder;
  }

  public Object getOsgiService(String name) {
    return finder.get(name);
  }

}
