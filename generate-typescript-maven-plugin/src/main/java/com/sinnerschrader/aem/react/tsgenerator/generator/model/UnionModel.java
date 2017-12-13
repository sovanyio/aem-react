package com.sinnerschrader.aem.react.tsgenerator.generator.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UnionModel {
	private String name;
	private String field;
	private List<String> types;
}
