package cz.cuni.mff.odcleanstore.linker.impl;

import java.util.List;

import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;

public class DebugResult {
	private SilkRule rule;
	private List<LinkedPair> links;
	
	public DebugResult(SilkRule rule, List<LinkedPair> links) {
		this.rule = rule;
		this.links = links;
	}
	
	public SilkRule getRule() {
		return rule;
	}
	public void setRule(SilkRule rule) {
		this.rule = rule;
	}
	public List<LinkedPair> getLinks() {
		return links;
	}
	public void setLinks(List<LinkedPair> links) {
		this.links = links;
	}
}
