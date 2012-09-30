package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class DNFilterTemplateInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private String propertyName;
	private String pattern;
	private Boolean keep;
	
	public DNFilterTemplateInstance(Integer id, Integer groupId, String propertyName, String pattern, Boolean keep) 
	{
		super(id);
		
		this.groupId = groupId;
		this.propertyName = propertyName;
		this.pattern = pattern;
		this.keep = keep;
	}
	
	public DNFilterTemplateInstance()
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

	public String getPropertyName() 
	{
		return propertyName;
	}

	public String getPattern() 
	{
		return pattern;
	}

	public Boolean getKeep() 
	{
		return keep;
	}
}
