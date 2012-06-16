package cz.cuni.mff.odcleanstore.transformer;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Graph;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;

public class GraphsAffectedByOIRuleLookupStrategy extends GraphsAffectedByRuleLookupStrategy<OIRule>
{
	@Override
	public List<Graph> getAffectedGraphs(OIRule rule) 
	{
		// TODO: return a list of all graphs affected by the given rule
		return null;
	}
}
