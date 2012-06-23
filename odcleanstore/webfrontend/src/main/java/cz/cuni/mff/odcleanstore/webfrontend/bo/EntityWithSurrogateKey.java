package cz.cuni.mff.odcleanstore.webfrontend.bo;

public abstract class EntityWithSurrogateKey extends BusinessEntity
{
	private static final long serialVersionUID = 1L;

	protected Long id;

	/**
	 * 
	 * @param id
	 */
	public EntityWithSurrogateKey(Long id)
	{
		this.id = id;
	}
	
	public EntityWithSurrogateKey()
	{
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getId() 
	{
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) 
	{
		this.id = id;
	}
}
