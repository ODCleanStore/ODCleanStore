package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * A generic parent of all classes which represent business entities with
 * a surrogate key.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class EntityWithSurrogateKey extends BusinessEntity implements IdentifiedEntity
{
	private static final long serialVersionUID = 1L;

	/** the surrogate key */
	protected Integer id;

	/**
	 * 
	 * @param id
	 */
	public EntityWithSurrogateKey(Integer id)
	{
		this.id = id;
	}
	
	/**
	 * 
	 */
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
