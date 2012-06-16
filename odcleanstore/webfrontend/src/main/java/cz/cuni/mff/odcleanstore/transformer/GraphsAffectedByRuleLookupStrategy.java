package cz.cuni.mff.odcleanstore.transformer;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Graph;

public abstract class GraphsAffectedByRuleLookupStrategy<RuleBO> 
{
	public abstract List<Graph> getAffectedGraphs(RuleBO rule);
}
