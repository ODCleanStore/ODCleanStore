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

	private String description;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param description
	 */
	public DNRule(Integer id, Integer groupId, String description) 
	{
		super(id, groupId);
		
		this.description = description;
	}
	
	public DNRule(DataNormalizationRule rule) {
		super(rule.getId(), rule.getGroupId());
		
		this.description = rule.getDescription();
	}
	
	/**
	 * 
	 * @param groupId
	 * @param description
	 */
	public DNRule(Integer groupId, String description)
	{
		super(groupId);
		
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
	public String getDescription() 
	{
		return description;
	}
}
