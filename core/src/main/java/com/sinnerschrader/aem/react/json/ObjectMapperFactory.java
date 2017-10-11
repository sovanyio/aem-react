package com.sinnerschrader.aem.react.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperFactory {

	public ObjectMapper create(String includePattern, String excludePattern) {
		SimpleModule module = new SimpleModule();
		module.addSerializer(String.class, new StringSerializer(includePattern, excludePattern));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(module);
		return objectMapper;

	}

}
