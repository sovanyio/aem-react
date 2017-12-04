package com.sinnerschrader.aem.react.tsgenerator.generator.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImportModel implements Comparable<ImportModel> {
	private String name;
	private String path;

	@Override
	public int compareTo(ImportModel o) {
		return name.compareTo(o.name);
	}
}
