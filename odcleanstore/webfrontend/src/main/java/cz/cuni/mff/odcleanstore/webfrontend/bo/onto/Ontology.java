package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RDFGraphEntity;

/**
 * Business entity reprezenting a ontology.
 * 
 * @author Tomas Soukup
 */
public class Ontology extends RDFGraphEntity 
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String description;
	
	public Ontology(Long id, String label, String description, String graphName, String rdfData) 
	{
		super(id, graphName, rdfData);
		this.label = label;
		this.description = description;
	}
	
	public Ontology()
	{
	}
	
	public String getLabel() 
	{
		return label;
	}
	
	public String getDescription() 
	{
		return description;
	}
}
