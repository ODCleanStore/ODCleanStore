package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

/**
 * The BO which represents a group of OI rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIRulesGroup extends RulesGroupEntity 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private List<OIRule> rules;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 */
	public OIRulesGroup(Integer id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
		
		this.rules = new LinkedList<OIRule>();
	}

	/**
	 * 
	 */
	public OIRulesGroup() 
	{
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() 
	{
		return label;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() 
	{
		return description;
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
