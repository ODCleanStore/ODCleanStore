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
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param authorId
	 * @param isUncommitted
	 * @param authorName
	 */
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
	 * @param id
	 */
	protected RulesGroupEntity(Integer id)
	{
		super(id);
	}
	
	/**
	 * 
	 */
	public RulesGroupEntity()
	{
		
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * 
	 */
	public Integer getAuthorId()
	{
		return authorId;
	}

	/**
	 * 
	 * @param authorId
	 */
	public void setAuthorId(Integer authorId)
	{
		this.authorId = authorId;
	}

	/**
	 * 
	 * @return
	 */
	public String getAuthorName()
	{
		return authorName;
	}

	/**
	 * 
	 * @param authorName
	 */
	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUncommitted()
	{
		return isUncommitted;
	}

	/**
	 * 
	 * @param isUncommitted
	 */
	public void setUncommitted(boolean isUncommitted)
	{
		this.isUncommitted = isUncommitted;
	}
}
