package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RDFGraphEntity;

/**
 * Business entity representing a ontology.
 * 
 * @author Tomas Soukup
 */
public class Ontology extends RDFGraphEntity 
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String description;
	private Integer authorId;
	
	public Ontology(Integer id, String label, String description, String graphName, Integer authorId) 
	{
		super(id, graphName);
		this.label = label;
		this.description = description;
		this.authorId = authorId;
	}
	
	public Ontology()
	{
		super();
	}
	
	public String getLabel() 
	{
		return label;
	}
	
	public String getDescription() 
	{
		return description;
	}
	
	public Integer getAuthorId() 
	{
		return authorId;
	}
	
	/**
	 * 
	 * @param authorId
	 */
	public void setAuthorId(Integer authorId) 
	{
		this.authorId = authorId;
	}
}
