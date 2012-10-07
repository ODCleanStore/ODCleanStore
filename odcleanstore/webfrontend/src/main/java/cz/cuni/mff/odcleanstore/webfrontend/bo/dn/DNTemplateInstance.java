package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public abstract class DNTemplateInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private Integer rawRuleId;
	
	/**
	 * 
	 * @param id
	 * @param rawRuleId
	 */
	public DNTemplateInstance(Integer id, Integer groupId, Integer rawRuleId)
	{
		super(id);
		
		this.groupId = groupId;
		this.rawRuleId = rawRuleId;
	}
	
	/**
	 * 
	 * @param rawRuleId
	 */
	public DNTemplateInstance(Integer groupId, Integer rawRuleId)
	{
		this.groupId = groupId;
		this.rawRuleId = rawRuleId;
	}
	
	/**
	 * 
	 */
	public DNTemplateInstance()
	{
		
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getRawRuleId()
	{
		return rawRuleId;
	}
	
	/**
	 * 
	 * @param rawRuleId
	 */
	public void setRawRuleId(Integer rawRuleId)
	{
		this.rawRuleId = rawRuleId;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getGroupId() 
	{
		return groupId;
	}

	/**
	 * 
	 * @param groupId
	 */
	public void setGroupId(Integer groupId) 
	{
		this.groupId = groupId;
	}
}
