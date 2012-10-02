package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent an instance of the filter template.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNFilterTemplateInstance extends DNTemplateInstance
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private String propertyName;
	private String pattern;
	private Boolean keep;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param propertyName
	 * @param pattern
	 * @param keep
	 */
	public DNFilterTemplateInstance(Integer id, Integer rawRuleId, Integer groupId, String propertyName, String pattern, Boolean keep) 
	{
		super(id, rawRuleId);
		
		this.groupId = groupId;
		this.propertyName = propertyName;
		this.pattern = pattern;
		this.keep = keep;
	}
	
	/**
	 * 
	 */
	public DNFilterTemplateInstance()
	{
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

	/**
	 * 
	 * @return
	 */
	public String getPropertyName() 
	{
		return propertyName;
	}

	/**
	 * 
	 * @return
	 */
	public String getPattern() 
	{
		return pattern;
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getKeep() 
	{
		return keep;
	}
}
