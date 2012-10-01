package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public abstract class DNTemplateInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer rawRuleId;
	
	/**
	 * 
	 * @param id
	 * @param rawRuleId
	 */
	public DNTemplateInstance(Integer id, Integer rawRuleId)
	{
		super(id);
		
		this.rawRuleId = rawRuleId;
	}
	
	/**
	 * 
	 * @param rawRuleId
	 */
	public DNTemplateInstance(Integer rawRuleId)
	{
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
}
