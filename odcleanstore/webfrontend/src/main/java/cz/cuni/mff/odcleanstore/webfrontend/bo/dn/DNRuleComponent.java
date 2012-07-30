package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Dusan
 *
 */
public class DNRuleComponent extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;
	
	private Long ruleId;
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
	public DNRuleComponent(Long id, Long ruleId, DNRuleComponentType type, 
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
	public Long getRuleId() 
	{
		return ruleId;
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
