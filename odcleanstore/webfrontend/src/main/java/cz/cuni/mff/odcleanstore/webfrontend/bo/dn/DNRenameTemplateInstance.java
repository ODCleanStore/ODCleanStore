package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent an instance of the rename template.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRenameTemplateInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private String sourcePropertyName;
	private String targetPropertyName;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param sourcePropertyName
	 * @param targetPropertyName
	 */
	public DNRenameTemplateInstance(Integer id, Integer groupId, String sourcePropertyName, String targetPropertyName)
	{
		super(id);
		
		this.groupId = groupId;
		this.sourcePropertyName = sourcePropertyName;
		this.targetPropertyName = targetPropertyName;
	}
	
	/**
	 * 
	 */
	public DNRenameTemplateInstance()
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
	public String getSourcePropertyName() 
	{
		return sourcePropertyName;
	}

	/**
	 * 
	 * @return
	 */
	public String getTargetPropertyName() 
	{
		return targetPropertyName;
	}
}
