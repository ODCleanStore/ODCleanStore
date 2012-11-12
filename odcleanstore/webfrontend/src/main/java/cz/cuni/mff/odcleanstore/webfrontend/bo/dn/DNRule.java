package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RuleEntity;

/**
 * The BO to represent a DN rule.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRule extends RuleEntity
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param label
	 * @param description
	 */
	public DNRule(Integer id, Integer groupId, String label, String description) 
	{
		super(id, groupId);

		this.label = label;
		this.description = description;
	}
	
	/**
	 * 
	 * @param rule
	 */
	public DNRule(DataNormalizationRule rule) 
	{
		super(rule.getId(), rule.getGroupId());

		this.label = rule.getLabel();
		this.description = rule.getDescription();
	}
	
	/**
	 * 
	 * @param groupId
	 * @param label
	 * @param description
	 */
	public DNRule(Integer groupId, String label, String description)
	{
		super(groupId);

		this.label = label;
		this.description = description;
	}
	
	/**
	 * 
	 */
	public DNRule()
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
}
