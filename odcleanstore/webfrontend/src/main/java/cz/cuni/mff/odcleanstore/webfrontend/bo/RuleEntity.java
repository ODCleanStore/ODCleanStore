package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * Base class for transformer rules.
 * 
 * @author Jan Michelfeit
 */
public class RuleEntity extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 */
	public RuleEntity(Integer id, Integer groupId)
	{
		super(id);
		this.groupId = groupId;
	}
	
	/**
	 * 
	 * @param groupId
	 */
	public RuleEntity(Integer groupId)
	{
		this.groupId = groupId;
	}
	
	/**
	 * 
	 */
	public RuleEntity()
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
}
