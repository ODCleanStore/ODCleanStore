package cz.cuni.mff.odcleanstore.transformer;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Graph;

public class GraphsAffectedByDNRuleLookupStrategy extends GraphsAffectedByRuleLookupStrategy<DNRule>
{
	@Override
	public List<Graph> getAffectedGraphs(DNRule rule) 
	{
		// TODO: return a list of all graphs affected by the given rule
		return null;
	}
}
