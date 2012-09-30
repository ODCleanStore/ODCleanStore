package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RDFGraphEntity;

/**
 * Business entity representing a ontology.
 * 
 * @author Tomáš Soukup
 */
public class Ontology extends RDFGraphEntity 
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param graphName
	 */
	public Ontology(Integer id, String label, String description, String graphName) 
	{
		super(id, graphName);
		this.label = label;
		this.description = description;
	}
	
	/**
	 * 
	 */
	public Ontology()
	{
		super();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLabel() 
	{
		return label;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() 
	{
		return description;
	}
}
