package com.sinnerschrader.aem.react.tsgenerator.generator.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FieldModel implements Comparable<FieldModel> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FieldModel other = (FieldModel) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	private String name;
	private String[] types;

	@Override
	public int compareTo(FieldModel o) {
		return name.compareTo(o.name);
	}
}
