package cz.cuni.mff.odcleanstore.webfrontend.bo;

public abstract class EntityWithSurrogateKey extends BusinessEntity implements IdentifiedEntity
{
	private static final long serialVersionUID = 1L;

	protected Integer id;

	/**
	 * 
	 * @param id
	 */
	public EntityWithSurrogateKey(Integer id)
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
	public Integer getId() 
	{
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Integer id) 
	{
		this.id = id;
	}
}
