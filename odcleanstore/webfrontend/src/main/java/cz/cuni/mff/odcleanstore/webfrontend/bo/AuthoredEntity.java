package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * Abstract parent for business objects that track their author.
 * 
 * @author Jan Michelfeit
 */
public abstract class AuthoredEntity extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param id
	 */
	public AuthoredEntity(Integer id)
	{
		super(id);
	}
	
	/**
	 * 
	 */
	public AuthoredEntity()
	{
		super();
	}
	
	/**
	 * Returns ID of the creator.
	 * @return user id
	 */
	public abstract Integer getAuthorId();
}
