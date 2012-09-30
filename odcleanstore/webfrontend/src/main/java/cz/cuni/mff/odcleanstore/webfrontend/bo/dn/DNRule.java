package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent a DN rule.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRule extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param description
	 */
	public DNRule(Integer id, Integer groupId, String description) 
	{
		super(id);
		
		this.groupId = groupId;
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
	public String getDescription() 
	{
		return description;
	}
}
