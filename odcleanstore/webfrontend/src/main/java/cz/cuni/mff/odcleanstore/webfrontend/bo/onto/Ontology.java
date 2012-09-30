package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RDFGraphEntity;

/**
 * Business entity representing a ontology.
 * 
 * @author Tomas Soukup
 */
public class Ontology extends RDFGraphEntity implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String description;
	private Integer authorId;
	private String authorName;
	
	public Ontology(Integer id, String label, String description, String graphName, Integer authorId, String authorName) 
	{
		super(id, graphName);
		this.label = label;
		this.description = description;
		this.authorId = authorId;
		this.setAuthorName(authorName);
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
	
	public void setAuthorId(Integer authorId) 
	{
		this.authorId = authorId;
	}

	public String getAuthorName()
	{
		return authorName;
	}

	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}
}
