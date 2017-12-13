package com.sinnerschrader.aem.react.tsgenerator.descriptor;

import java.util.HashMap;
import java.util.Map;

public class ScanContext {
	public Map<Class<?>, Discriminator> discriminators = new HashMap<>();
	public Map<Class<?>, UnionType> unionTypes = new HashMap<>();
}
