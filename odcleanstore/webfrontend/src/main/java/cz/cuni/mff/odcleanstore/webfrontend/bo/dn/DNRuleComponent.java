package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent a component of an DN rule.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRuleComponent extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;
	
	private Integer ruleId;
	private DNRuleComponentType type;
	private String modification;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param ruleId
	 * @param typeId
	 * @param modification
	 * @param description
	 */
	public DNRuleComponent(Integer id, Integer ruleId, DNRuleComponentType type, 
		String modification, String description) 
	{
		super(id);
		
		this.ruleId = ruleId;
		this.type = type;
		this.modification = modification;
		this.description = description;
	}
	
	/**
	 * 
	 */
	public DNRuleComponent()
	{
	}

	/**
	 * 
	 * @return
	 */
	public Integer getRuleId() 
	{
		return ruleId;
	}

	/**
	 * 
	 * @param ruleId
	 */
	public void setRuleId(Integer ruleId) 
	{
		this.ruleId = ruleId;
	}

	/**
	 * 
	 * @return
	 */
	public DNRuleComponentType getType() 
	{
		return type;
	}

	/**
	 * 
	 * @return
	 */
	public String getModification() 
	{
		return modification;
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
