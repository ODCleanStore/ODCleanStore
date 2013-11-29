package cz.cuni.mff.odcleanstore.linker.impl;

import java.io.Serializable;
import java.util.List;

public class DebugResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private String ruleLabel;
	private List<LinkedPair> links;

	public DebugResult(String ruleLabel, List<LinkedPair> links) {
		this.ruleLabel = ruleLabel;
		this.links = links;
	}

	public String getRuleLabel() {
		return ruleLabel;
	}
	public void setRuleLabel(String ruleLabel) {
		this.ruleLabel = ruleLabel;
	}
	public List<LinkedPair> getLinks() {
		return links;
	}
	public void setLinks(List<LinkedPair> links) {
		this.links = links;
	}
}
