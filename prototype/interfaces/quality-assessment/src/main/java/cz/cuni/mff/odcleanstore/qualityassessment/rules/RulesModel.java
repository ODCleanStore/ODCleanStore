package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.data.SqlEndpoint;

public class RulesModel {
	
	public RulesModel (SqlEndpoint endpoint) {
		//TODO: ESTABLISH CONNECTION
	}
	
	public Collection<Rule> getAllRules() {
		//TODO: FETCH REAL RULES
		return new ArrayList<Rule>();
	}
}
