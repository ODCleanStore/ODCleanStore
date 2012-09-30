package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * The parent of all classes representing entities related to
 * transformer rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class RulesGroupEntity extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param id
	 */
	public RulesGroupEntity(Integer id)
	{
		super(id);
	}
	
	/**
	 * 
	 */
	public RulesGroupEntity()
	{
		super();
	}
}
