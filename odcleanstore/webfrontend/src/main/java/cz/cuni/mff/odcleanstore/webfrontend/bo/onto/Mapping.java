package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

/**
 * Business entity representing a mapping between ontologies' entities.
 * 
 * @author Tomas Soukup
 */
public class Mapping extends BusinessEntity 
{
	private static final long serialVersionUID = 1L;
	
	private String sourceUri;
	private String targetUri;
	private String relationType;
	
	/**
	 * @param sourceUri first mapped URI
	 * @param targetUri second mapped URI
	 * @param relationType type of mapping between URIs
	 */
	public Mapping(String sourceUri, String targetUri, String relationType) 
	{
		super();
		this.sourceUri = sourceUri;
		this.targetUri = targetUri;
		this.relationType = relationType;
	}
	
	public Mapping()
	{
		super();
	}

	/**
	 * @return source URI
	 */
	public String getSourceUri() 
	{
		return sourceUri;
	}

	/**
	 * @return target URI
	 */
	public String getTargetUri() 
	{
		return targetUri;
	}

	/**
	 * @return relation type
	 */
	public String getRelationType() 
	{
		return relationType;
	}	
}
