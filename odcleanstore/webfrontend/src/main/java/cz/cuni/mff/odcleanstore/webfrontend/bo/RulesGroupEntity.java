package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * Base class for transformer rule groups.
 * 
 * @author Jan Michelfeit
 */
public abstract class RulesGroupEntity extends EntityWithSurrogateKey implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String description;
	private Integer authorId;
	private String authorName;
	private boolean isUncommitted;
	
	protected RulesGroupEntity(Integer id)
	{
		super(id);
	}
	
	public RulesGroupEntity(Integer id, String label, String description, Integer authorId, boolean isUncommitted, String authorName)
	{
		super(id);
		this.label = label;
		this.description = description;
		this.authorId = authorId;
		this.setUncommitted(isUncommitted);
		this.authorName = authorName;
	}
	
	/**
	 * 
	 */
	public RulesGroupEntity()
	{
		
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Integer getAuthorId()
	{
		return authorId;
	}

	public void setAuthorId(Integer authorId)
	{
		this.authorId = authorId;
	}

	public String getAuthorName()
	{
		return authorName;
	}

	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}

	public boolean isUncommitted()
	{
		return isUncommitted;
	}

	public void setUncommitted(boolean isUncommitted)
	{
		this.isUncommitted = isUncommitted;
	}
}
