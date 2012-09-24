package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class RuleAssignment extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;

	private Integer transformerInstanceId;
	private Integer groupId;
	private String groupLabel;
	private String groupDescription;
	
	/**
	 * 
	 * @param id
	 * @param transformerInstanceId
	 * @param groupId
	 */
	public RuleAssignment(Integer id, Integer transformerInstanceId, Integer groupId, String groupLabel, String groupDescription) 
	{
		super(id);
		
		this.transformerInstanceId = transformerInstanceId;
		this.groupId = groupId;
		this.groupLabel = groupLabel;
		this.groupDescription = groupDescription;
	}
	
	/**
	 * 
	 * @param transformerInstanceId
	 * @param groupId
	 */
	public RuleAssignment(Integer transformerInstanceId, Integer groupId) 
	{
		this.transformerInstanceId = transformerInstanceId;
		this.groupId = groupId;
	}
	
	/**
	 * 
	 */
	public RuleAssignment()
	{
		super();
	}

	/**
	 * 
	 * @return
	 */
	public Integer getTransformerInstanceId() 
	{
		return transformerInstanceId;
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
	 * @return
	 */
	public String getGroupLabel() 
	{
		return groupLabel;
	}

	/**
	 * 
	 * @return
	 */
	public String getGroupDescription() 
	{
		return groupDescription;
	}
}
