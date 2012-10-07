package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

/**
 * The BO which represents a group of OI rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIRulesGroup extends RulesGroupEntity implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;

	private List<OIRule> rules;

	public OIRulesGroup(Integer id, String label, String description, Integer authorId, boolean isUncommitted, String authorName) 
	{
		super(id, label, description, authorId, isUncommitted, authorName);
		this.rules = new LinkedList<OIRule>();
	}

	public OIRulesGroup()
	{
	}

	/**
	 * 
	 * @param rules
	 */
	public void setRules(List<OIRule> rules)
	{
		this.rules = rules;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<OIRule> getRules()
	{
		return rules;
	}
}
