package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class DNRule extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Long groupId;
	private String description;
	
	public DNRule(Long id, Long groupId, String description) 
	{
		super(id);
		
		this.groupId = groupId;
		this.description = description;
	}
	
	public DNRule()
	{
	}

	public Long getGroupId()
	{
		return groupId;
	}
	
	public void setGroupId(Long groupId)
	{
		this.groupId = groupId;
	}

	public String getDescription() 
	{
		return description;
	}
}
