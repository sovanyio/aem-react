package com.sinnerschrader.aem.react.tsgenerator.descriptor;

import java.util.List;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class EnumDescriptor
{
	private String name;

	private String fullJavaClassName;

	private List<String> values;


}
