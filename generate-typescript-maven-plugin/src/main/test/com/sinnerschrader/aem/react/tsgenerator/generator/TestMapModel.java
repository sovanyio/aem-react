package com.sinnerschrader.aem.react.tsgenerator.generator;

import java.util.Map;

import com.sinnerschrader.aem.react.typescript.Element;
import com.sinnerschrader.aem.react.typescript.ExportTs;

import lombok.Getter;

@Getter
@ExportTs
public class TestMapModel {
	@Element(TestModel.class)
	private Map<String, TestModel> models;
}
