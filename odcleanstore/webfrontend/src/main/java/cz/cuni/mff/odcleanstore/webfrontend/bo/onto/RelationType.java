package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * Business entity representing a relation type between ontology entities.
 * 
 * @author Tomáš Soukup
 */
public class RelationType extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	private String uri;
	
	/**
	 * 
	 * @param id
	 * @param uri
	 */
	public RelationType(Integer id, String uri)
	{	
		super(id);
		this.uri = uri;
	}
	
	/**
	 * 
	 */
	public RelationType()
	{
		super();
	}

	/**
	 * 
	 * @return
	 */
	public String getUri() 
	{
		return uri;
	}
}
