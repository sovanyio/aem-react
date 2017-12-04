package com.sinnerschrader.aem.react.tsgenerator.generator;

import java.util.List;

import com.sinnerschrader.aem.react.typescript.Element;
import com.sinnerschrader.aem.react.typescript.ExportTs;

import lombok.Getter;

@Getter
@ExportTs
public class TestGetterListModel {
	@Element(TestModel.class)
	public List<TestModel> getModels() {
		return models;
	}

	private List<TestModel> models;
}
