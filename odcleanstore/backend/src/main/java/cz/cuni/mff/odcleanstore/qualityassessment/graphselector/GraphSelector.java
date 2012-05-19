package cz.cuni.mff.odcleanstore.qualityassessment.graphselector;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.Rule;

public class GraphSelector {
	
	public GraphSelector(ConnectionCredentials endpoint) {
	}
	
	public Collection<String> getGraphAffectedByRuleChange(Rule former, Rule current) {
		//TODO: SELECT ALL GRAPHS THAT MATCH THE FORMER RULE OR THE CURRENT RULE
		return new ArrayList<String>();
	}

}
