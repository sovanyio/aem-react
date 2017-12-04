package com.sinnerschrader.aem.react.tsgenerator.generator;

import com.sinnerschrader.aem.react.typescript.Element;
import com.sinnerschrader.aem.react.typescript.ExportTs;

import lombok.Getter;

@Getter
@ExportTs
public class TestArrayModel {
	@Element(TestModel.class)
	private TestModel[] models;
}
