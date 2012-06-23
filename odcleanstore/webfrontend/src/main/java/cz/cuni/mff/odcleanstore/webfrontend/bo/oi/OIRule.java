package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class OIRule extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Long groupId;
	private String definition;
	
	public OIRule(Long id, Long groupId, String definition) 
	{
		super(id);
		
		this.groupId = groupId;
		this.definition = definition;
	}

	public OIRule() 
	{
	}
	
	public String getDefinition()
	{
		return definition;
	}
	
	public Long getGroupId()
	{
		return groupId;
	}
	
	public void setGroupId(Long groupId)
	{
		this.groupId = groupId;
	}
}
