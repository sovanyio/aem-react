package com.sinnerschrader.aem.react;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.servlet.RequestDispatcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.sling.JsonObjectCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.components.IncludeOptions;
import com.day.cq.wcm.commons.WCMUtils;
import com.sinnerschrader.aem.react.exception.TechnicalException;

public class Sling {

  private ScriptContext context;

  public Sling(ScriptContext context) {
    super();
    this.context = context;
  }

  private static final Logger LOG = LoggerFactory.getLogger(Sling.class);

  public String getResource(String path, Integer depth) {
    SlingHttpServletRequest request = (SlingHttpServletRequest) context.getBindings(ScriptContext.ENGINE_SCOPE).get(SlingBindings.REQUEST);

    try {
      if (depth == null) {
        depth = -1;
      }
      Resource resource = request.getResourceResolver().getResource(path);
      if (resource == null) {
        return null;
      } else {
        return JsonObjectCreator.create(resource, depth).toString();
      }
    } catch (JSONException e) {
      throw new TechnicalException("could not get current resource", e);
    }

  }

  public String renderDialogScript(String path, String resourceType) {
    return this._includeResource(path, resourceType, true);
  }

  public String includeResource(String path, String resourceType) {
    return this._includeResource(path, resourceType, false);
  }

  public String _includeResource(String path, String resourceType, boolean dialog) {
    StringWriter out = new StringWriter();
    Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
    if (StringUtils.isEmpty(path)) {
      LOG.error("Script path cannot be empty");
    } else {
      SlingHttpServletResponse customResponse = new PrintWriterResponseWrapper(new PrintWriter(out),
          (SlingHttpServletResponse) bindings.get(SlingBindings.RESPONSE));
      SlingHttpServletRequest request = (SlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);

      String script = normalizePath(request, path);
      ComponentContext componentContext = WCMUtils.getComponentContext(request);
      EditContext editContext = componentContext.getEditContext();

      Resource includeRes = request.getResourceResolver().resolve(script);
      if (includeRes instanceof NonExistingResource || includeRes.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
        includeRes = new SyntheticResource(request.getResourceResolver(), script, resourceType);
      } else if (!includeRes.getPath().equals(script)) {
        includeRes = new SyntheticResource(request.getResourceResolver(), script, resourceType);
      }
      try {
        RequestDispatcherOptions opts = new RequestDispatcherOptions(null);
        if (StringUtils.isNotEmpty(resourceType)) {
          opts.setForceResourceType(resourceType);
        }
        if (dialog) {
          opts.setAddSelectors("dialog");
        }
        IncludeOptions options = IncludeOptions.getOptions(request, true);
        if (editContext == null) {
          // this is the editable.refresh() case where the root should not be
          // decorated but all others.
          // TODO better move this code up to the eval method
          options.forceEditContext(true);
          options.setDecorationTagName("");
          opts.setReplaceSelectors("");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(includeRes, opts);
        dispatcher.include(request, customResponse);
      } catch (Exception e) {
        LOG.error("Failed to include resource {}", script, e);
      }
    }
    if (dialog) {
      Document document = Jsoup.parseBodyFragment(out.toString());
      Elements script = document.getElementsByTag("script");
      return script.html();
    }
    return out.toString();
  }

  private String normalizePath(SlingHttpServletRequest request, String path) {
    if (!path.startsWith("/")) {
      path = request.getResource().getPath() + "/" + path;
    }
    return ResourceUtil.normalize(path);
  }

  public String currentResource(int depth) {
    SlingHttpServletRequest request = (SlingHttpServletRequest) context.getBindings(ScriptContext.ENGINE_SCOPE).get(SlingBindings.REQUEST);

    try {
      return JsonObjectCreator.create(request.getResource(), depth).toString();
    } catch (JSONException e) {
      throw new TechnicalException("could not get current resource", e);
    }
  }

  public String getUrl() {
    SlingHttpServletRequest request = (SlingHttpServletRequest) context.getBindings(ScriptContext.ENGINE_SCOPE).get(SlingBindings.REQUEST);
    return request.getRequestURI();

  }

  public String getPagePath() {
    SlingHttpServletRequest request = (SlingHttpServletRequest) context.getBindings(ScriptContext.ENGINE_SCOPE).get(SlingBindings.REQUEST);
    String pathInfo = request.getPathInfo();
    return pathInfo;

  }

}
