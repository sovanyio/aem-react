package com.sinnerschrader.aem.react.tsgenerator.generator;

import java.util.Set;


public class ReferenceEntry
{
	private String entry;

	private Set<String> secondaries;

	public String getEntry()
	{
		return entry;
	}

	public Set<String> getSecondaries()
	{
		return secondaries;
	}

	public void setEntry(String entry)
	{
		this.entry = entry;
	}

	public void setSecondaries(Set<String> secondaries)
	{
		this.secondaries = secondaries;
	}

}
