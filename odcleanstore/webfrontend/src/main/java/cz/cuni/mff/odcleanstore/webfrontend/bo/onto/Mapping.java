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

	public String getSourceUri() 
	{
		return sourceUri;
	}

	public String getTargetUri() 
	{
		return targetUri;
	}

	public String getRelationType() 
	{
		return relationType;
	}	
}
