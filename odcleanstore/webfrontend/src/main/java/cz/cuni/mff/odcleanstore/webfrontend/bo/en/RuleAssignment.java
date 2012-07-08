package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class RuleAssignment extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;

	private Long transformerInstanceId;
	private Long groupId;
	private String groupLabel;
	private String groupDescription;
	
	/**
	 * 
	 * @param id
	 * @param transformerInstanceId
	 * @param groupId
	 */
	public RuleAssignment(Long id, Long transformerInstanceId, Long groupId, String groupLabel, String groupDescription) 
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
	public RuleAssignment(Long transformerInstanceId, Long groupId) 
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
	public Long getTransformerInstanceId() 
	{
		return transformerInstanceId;
	}

	/**
	 * 
	 * @return
	 */
	public Long getGroupId() 
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
