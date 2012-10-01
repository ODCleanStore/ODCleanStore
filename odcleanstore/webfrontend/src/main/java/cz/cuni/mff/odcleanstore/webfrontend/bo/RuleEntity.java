package cz.cuni.mff.odcleanstore.webfrontend.bo;

public class RuleEntity extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	
	public RuleEntity(Integer id, Integer groupId)
	{
		super(id);
		this.groupId = groupId;
	}
	
	public RuleEntity()
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
}
