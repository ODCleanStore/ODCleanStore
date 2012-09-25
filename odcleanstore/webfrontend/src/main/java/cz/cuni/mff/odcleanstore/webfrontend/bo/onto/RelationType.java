package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * Business entity representing a relation type between ontology entities.
 * 
 * @author Tomas Soukup
 */
public class RelationType extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	private String uri;
	
	public RelationType(Integer id, String uri)
	{	
		super(id);
		this.uri = uri;
	}
	
	public RelationType()
	{
		super();
	}

	public String getUri() 
	{
		return uri;
	}
}
