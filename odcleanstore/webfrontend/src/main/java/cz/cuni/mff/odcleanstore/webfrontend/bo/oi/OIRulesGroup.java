package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

public class OIRulesGroup extends RulesGroupEntity implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;

	private List<OIRule> rules;
	
	public OIRulesGroup(Integer id, String label, String description, Integer authorId, String authorName) 
	{
		super(id, label, description, authorId, authorName);
		this.rules = new LinkedList<OIRule>();
	}

	public OIRulesGroup()
	{
	}

	public void setRules(List<OIRule> rules)
	{
		this.rules = rules;
	}
	
	public List<OIRule> getRules()
	{
		return rules;
	}
}
