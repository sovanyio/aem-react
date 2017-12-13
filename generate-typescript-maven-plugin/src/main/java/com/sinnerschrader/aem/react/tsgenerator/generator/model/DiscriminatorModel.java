package com.sinnerschrader.aem.react.tsgenerator.generator.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DiscriminatorModel {
	private String field;
	private String value;
}
