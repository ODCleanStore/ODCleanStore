package cz.cuni.mff.odcleanstore.transformer;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Graph;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;

public class GraphsAffectedByQARuleLookupStrategy extends GraphsAffectedByRuleLookupStrategy<QARule>
{
	@Override
	public List<Graph> getAffectedGraphs(QARule rule) 
	{
		// TODO: return a list of all graphs affected by the given rule
		return null;
	}
}
