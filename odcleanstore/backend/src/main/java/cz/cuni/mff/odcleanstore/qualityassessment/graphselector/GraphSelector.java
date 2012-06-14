package cz.cuni.mff.odcleanstore.qualityassessment.graphselector;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;

public class GraphSelector {

	public GraphSelector(JDBCConnectionCredentials endpoint) {
	}

	public Collection<String> getGraphAffectedByRuleChange(Rule former, Rule current) {
		//TODO: SELECT ALL GRAPHS THAT MATCH THE FORMER RULE OR THE CURRENT RULE
		return new ArrayList<String>();
	}

}
