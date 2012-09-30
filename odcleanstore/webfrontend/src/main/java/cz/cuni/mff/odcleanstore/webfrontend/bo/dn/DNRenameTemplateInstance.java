package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class DNRenameTemplateInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private String sourcePropertyName;
	private String targetPropertyName;
	
	public DNRenameTemplateInstance(Integer id, Integer groupId, String sourcePropertyName, String targetPropertyName)
	{
		super(id);
		
		this.groupId = groupId;
		this.sourcePropertyName = sourcePropertyName;
		this.targetPropertyName = targetPropertyName;
	}
	
	public DNRenameTemplateInstance()
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

	public String getSourcePropertyName() 
	{
		return sourcePropertyName;
	}

	public String getTargetPropertyName() 
	{
		return targetPropertyName;
	}
}
