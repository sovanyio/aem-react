package com.sinnerschrader.aem.react;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.jcr.RepositoryException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.servlets.ServletResolver;
import org.apache.sling.commons.classloader.DynamicClassLoaderManager;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.scripting.api.AbstractScriptEngineFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinnerschrader.aem.react.api.OsgiServiceFinder;
import com.sinnerschrader.aem.react.exception.TechnicalException;
import com.sinnerschrader.aem.react.json.ObjectMapperFactory;
import com.sinnerschrader.aem.react.loader.HashedScript;
import com.sinnerschrader.aem.react.loader.JcrResourceChangeListener;
import com.sinnerschrader.aem.react.loader.ScriptCollectionLoader;
import com.sinnerschrader.aem.react.loader.ScriptLoader;
import com.sinnerschrader.aem.react.metrics.ComponentMetricsService;
import com.sinnerschrader.aem.react.repo.RepositoryConnectionFactory;

@Component(label = "ReactJs Script Engine Factory", metatype = true)
@Service(ScriptEngineFactory.class)
@Properties({ @Property(name = "service.description", value = "Reactjs Templating Engine"), //
		@Property(name = "compatible.javax.script.name", value = "jsx"),
		@Property(name = ReactScriptEngineFactory.PROPERTY_SCRIPTS_PATHS, label = "the jcr paths to the scripts libraries", value = {}, cardinality = Integer.MAX_VALUE), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_SUBSERVICENAME, label = "the subservicename for accessing the script resources. If it is null then the deprecated system admin will be used.", value = ""), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_POOL_TOTAL_SIZE, label = "total javascript engine pool size", longValue = 20), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_POOL_MAX_IDLE, label = "maximum number of pool objects that can be idle", longValue = 20), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_POOL_MIN_IDLE, label = "minimum number of pool objects to keep idle", longValue = 7), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_POOL_INITIAL_WARM_SIZE, label = "number of pool items to request on bundle start to warm the pool", longValue = 20), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_ROOT_ELEMENT_NAME, label = "the root element name of the", value = "div"), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_ROOT_CLASS_NAME, label = "the root element class name", value = ""), //
		@Property(name = ReactScriptEngineFactory.PROPERTY_DEBUG, label = "enable &debug request param to force exceptions to output to page", boolValue = false), //
		@Property(name = ReactScriptEngineFactory.JSON_RESOURCEMAPPING_INCLUDE_PATTERN, label = "pattern for text properties in sling models that must be mapped by resource resover", value = "^/content"), //
		@Property(name = ReactScriptEngineFactory.JSON_RESOURCEMAPPING_EXCLUDE_PATTERN, label = "pattern to include properties from resource mapping", value = "") //
})
public class ReactScriptEngineFactory extends AbstractScriptEngineFactory {

	public static final String PROPERTY_SCRIPTS_PATHS = "scripts.paths";
	public static final String PROPERTY_SUBSERVICENAME = "subServiceName";
	public static final String PROPERTY_POOL_TOTAL_SIZE = "pool.total.size";
	public static final String PROPERTY_POOL_MAX_IDLE = "pool.idle.max";
	public static final String PROPERTY_POOL_MIN_IDLE = "pool.idle.min";
	public static final String PROPERTY_POOL_INITIAL_WARM_SIZE = "pool.warm.size";
	public static final String PROPERTY_ROOT_ELEMENT_NAME = "root.element.name";
	public static final String PROPERTY_ROOT_CLASS_NAME = "root.element.class.name";
	public static final String PROPERTY_DEBUG = "debug";
	public static final String JSON_RESOURCEMAPPING_INCLUDE_PATTERN = "json.resourcemapping.include.pattern";
	public static final String JSON_RESOURCEMAPPING_EXCLUDE_PATTERN = "json.resourcemapping.exclude.pattern";

	@Reference
	private ServletResolver servletResolver;

	@Reference
	private ModelFactory modelFactory;

	@Reference
	private DynamicClassLoaderManager dynamicClassLoaderManager;

	@Reference
	private OsgiServiceFinder finder;

	@Reference
	private ScriptLoader scriptLoader;

	@Reference
	private AdapterManager adapterManager;

	@Reference
	private ComponentMetricsService metricsService;

	private static final String NASHORN_POLYFILL_JS = "nashorn-polyfill.js";

	private ClassLoader dynamicClassLoader;

	private ReactScriptEngine engine;

	private List<HashedScript> scripts;
	private String[] scriptResources;
	private JcrResourceChangeListener listener;
	private String subServiceName;

	private static Logger LOGGER = LoggerFactory.getLogger(ReactScriptEngineFactory.class);

	@Reference
	private RepositoryConnectionFactory repositoryConnectionFactory;

	public synchronized void createScripts() {
		List<HashedScript> newScripts = new LinkedList<>();
		// we need to add the nashorn polyfill for console, global and AemGlobal
		String polyFillName = this.getClass().getPackage().getName().replace(".", "/") + "/" + NASHORN_POLYFILL_JS;

		URL polyFillUrl = this.dynamicClassLoader.getResource(polyFillName);
		if (polyFillUrl == null) {
			throw new TechnicalException("cannot find initial script " + polyFillName);
		}
		try {
			newScripts.add(createHashedScript("polyFillUrl", new InputStreamReader(polyFillUrl.openStream(), "UTF-8")));
		} catch (IOException | TechnicalException e) {
			throw new TechnicalException("cannot open stream to " + polyFillUrl, e);
		}

		for (String scriptResource : scriptResources) {
			try (Reader reader = scriptLoader.loadJcrScript(scriptResource, subServiceName)) {
				newScripts.add(createHashedScript(scriptResource, reader));
			} catch (TechnicalException | IOException e) {
				LOGGER.error("cannot load script resources", e);
			}
		}
		this.scripts = newScripts;
	}

	private HashedScript createHashedScript(String id, Reader reader) {
		String script;
		try {
			script = IOUtils.toString(reader);
			byte[] checksum = MessageDigest.getInstance("MD5").digest(script.getBytes("UTF-8"));
			return new HashedScript(new String(Base64.getEncoder().encode(checksum), "UTF-8"), script, id);
		} catch (IOException | NoSuchAlgorithmException e) {
			throw new TechnicalException("cannot created hashed script " + id, e);
		}
	}

	protected ScriptCollectionLoader createLoader(final String[] scriptResources) {

		return new ScriptCollectionLoader() {

			@Override
			public Iterator<HashedScript> iterator() {
				return scripts.iterator();
			}
		};

	}

	public ReactScriptEngineFactory() {
		super();
		setNames("reactjs");
		setExtensions("jsx");
	}

	@Override
	public String getLanguageName() {
		return "jsx";
	}

	@Override
	public String getLanguageVersion() {
		return "1.0.0";
	}

	@Activate
	public void initialize(final ComponentContext context, Map<String, Object> properties) {

		this.subServiceName = PropertiesUtil.toString(context.getProperties().get(PROPERTY_SUBSERVICENAME), "");
		scriptResources = PropertiesUtil.toStringArray(context.getProperties().get(PROPERTY_SCRIPTS_PATHS),
				new String[0]);
		int poolTotalSize = PropertiesUtil.toInteger(context.getProperties().get(PROPERTY_POOL_TOTAL_SIZE), 20);
		int maxIdle = PropertiesUtil.toInteger(context.getProperties().get(PROPERTY_POOL_MAX_IDLE), poolTotalSize);
		int minIdle = PropertiesUtil.toInteger(context.getProperties().get(PROPERTY_POOL_MIN_IDLE), poolTotalSize / 3);
		int initialWarmEngineCount = PropertiesUtil.toInteger(context.getProperties().get(PROPERTY_POOL_INITIAL_WARM_SIZE), poolTotalSize);

		boolean isDebug = PropertiesUtil.toBoolean(context.getProperties().get(PROPERTY_DEBUG), false);

		String rootElementName = PropertiesUtil.toString(context.getProperties().get(PROPERTY_ROOT_ELEMENT_NAME),
				"div");
		String rootElementClassName = PropertiesUtil.toString(context.getProperties().get(PROPERTY_ROOT_CLASS_NAME),
				"");
		JavacriptEnginePoolFactory javacriptEnginePoolFactory = new JavacriptEnginePoolFactory(
				createLoader(scriptResources), null);

		String includePattern = PropertiesUtil
				.toString(context.getProperties().get(JSON_RESOURCEMAPPING_INCLUDE_PATTERN), "^/content");
		String excludePattern = PropertiesUtil
				.toString(context.getProperties().get(JSON_RESOURCEMAPPING_EXCLUDE_PATTERN), null);

		ObjectMapper mapper = new ObjectMapperFactory().create(includePattern, excludePattern);

		if (initialWarmEngineCount > poolTotalSize) {
			initialWarmEngineCount = poolTotalSize;
		}
		if (maxIdle > poolTotalSize) {
			maxIdle = poolTotalSize;
		}
		if (minIdle > poolTotalSize) {
			minIdle = poolTotalSize;
		}
		if (minIdle > maxIdle) {
			minIdle = maxIdle;
		}

		ObjectPool<JavascriptEngine> pool = createPool(poolTotalSize, minIdle, maxIdle, javacriptEnginePoolFactory);
		this.engine = new ReactScriptEngine(this, pool, finder, dynamicClassLoaderManager, rootElementName,
				rootElementClassName, modelFactory, adapterManager, mapper, metricsService, isDebug);
		this.createScripts();
		this.poolWarmup(pool, initialWarmEngineCount);

		this.listener = new JcrResourceChangeListener(repositoryConnectionFactory,
				new JcrResourceChangeListener.Listener() {
					@Override
					public void changed(String script) {
						createScripts();
					}

				}, subServiceName);
		this.listener.activate(scriptResources);

	}

	@Modified
	public void reconfigure(final ComponentContext context, Map<String, Object> properties) throws RepositoryException {
		stop();
		initialize(context, properties);
	}

	@Deactivate
	public void stop() throws RepositoryException {
		this.engine.stop();
		this.listener.deactivate();
	}

	protected ObjectPool<JavascriptEngine> createPool(int poolTotalSize, int minIdle, int maxIdle,
			JavacriptEnginePoolFactory javacriptEnginePoolFactory) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(poolTotalSize);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		return new GenericObjectPool<JavascriptEngine>(javacriptEnginePoolFactory, config);
	}

	private void poolWarmup(ObjectPool<JavascriptEngine> pool, int size) {
		for (int i = 0; i < size; i++) {
			final int k = i;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					LOGGER.debug("Warming pool item " + String.valueOf(k));
					JavascriptEngine tEngine = null;
					try {
						tEngine = pool.borrowObject();
					} catch (Exception e) {
						LOGGER.error("Failed to warm pool object " + e.toString());
					} finally {
						try {
							if(tEngine != null)
								pool.returnObject(tEngine);
						} catch (Exception e) {
							LOGGER.error("Failed to return pool object " + e.toString());
						}
					}
					LOGGER.debug("Warmed pool item " + String.valueOf(k));
				}
			});
			t.start();
		}
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return engine;
	}

	protected void bindDynamicClassLoaderManager(final DynamicClassLoaderManager dclm) {
		if (this.dynamicClassLoader != null) {
			this.dynamicClassLoader = null;
			this.dynamicClassLoaderManager = null;
		}
		this.dynamicClassLoaderManager = dclm;
		dynamicClassLoader = dclm.getDynamicClassLoader();
	}

	protected void unbindDynamicClassLoaderManager(final DynamicClassLoaderManager dclm) {
		if (this.dynamicClassLoaderManager == dclm) {
			this.dynamicClassLoader = null;
			this.dynamicClassLoaderManager = null;
		}
	}

	protected ClassLoader getClassLoader() {
		return dynamicClassLoader;
	}

}
