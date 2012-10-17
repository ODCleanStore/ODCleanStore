package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * A generic parent of all classes which represent business entities with
 * surrogate keys (e.g. which are stored to a db table with a primary key
 * called 'id').
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class EntityWithSurrogateKey extends BusinessEntity implements IdentifiedEntity
{
	private static final long serialVersionUID = 1L;

	/** the surrogate key value */
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
