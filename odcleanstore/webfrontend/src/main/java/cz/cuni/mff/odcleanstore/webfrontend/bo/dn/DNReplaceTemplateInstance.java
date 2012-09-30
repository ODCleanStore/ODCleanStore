package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class DNReplaceTemplateInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	private Integer groupId;
	private String propertyName;
	private String pattern;
	private String replacement;
	
	public DNReplaceTemplateInstance(Integer id, Integer groupId, String propertyName, String pattern, String replacement) 
	{
		super(id);
		
		this.groupId = groupId;
		this.propertyName = propertyName;
		this.pattern = pattern;
		this.replacement = replacement;
	}
	
	public DNReplaceTemplateInstance()
	{
	}
	
	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
	}

	public Integer getGroupId() 
	{
		return groupId;
	}

	public String getPropertyName() 
	{
		return propertyName;
	}

	public String getPattern() 
	{
		return pattern;
	}

	public String getReplacement() 
	{
		return replacement;
	}
}
