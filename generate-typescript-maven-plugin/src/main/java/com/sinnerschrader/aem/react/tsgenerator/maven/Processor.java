package com.sinnerschrader.aem.react.tsgenerator.maven;

import com.sinnerschrader.aem.react.tsgenerator.descriptor.ClassDescriptor;

public interface Processor {
	public void apply(ClassDescriptor cd);
}
