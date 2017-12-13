package com.sinnerschrader.aem.react.tsgenerator.descriptor;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Discriminator {
	private String value;
	private String field;
	private Class type;
}
