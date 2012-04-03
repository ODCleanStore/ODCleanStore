package cz.cuni.mff.odcleanstore.qualityassessment;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Rule container
 */
class RuleSet
{
	private HashSet<Rule> rules = new HashSet<Rule>();
	
	public Iterator<Rule> iterator()
	{
		return rules.iterator();
	}
}