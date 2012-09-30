package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class DNRule extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private String description;
	
	public DNRule(Integer id, Integer groupId, String description) 
	{
		super(id);
		
		this.groupId = groupId;
		this.description = description;
	}
	
	public DNRule()
	{
	}

	public Integer getGroupId()
	{
		return groupId;
	}
	
	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
	}

	public String getDescription() 
	{
		return description;
	}
}
