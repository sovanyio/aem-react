package com.sinnerschrader.aem.react.tsgenerator.descriptor;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class ClassDescriptor
{
	private String name;

	private String fullJavaClassName;

	private TypeDescriptor superClass;

	private final Map<String, PropertyDescriptor> properties = new LinkedHashMap<String, PropertyDescriptor>();

	private final Map<String, EnumDescriptor> enums = new LinkedHashMap<String, EnumDescriptor>();

}
