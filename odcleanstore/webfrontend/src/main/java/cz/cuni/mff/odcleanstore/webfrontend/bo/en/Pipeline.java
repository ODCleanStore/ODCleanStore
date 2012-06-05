package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class Pipeline extends BusinessObject
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private Boolean runOnCleanDB;
	private List<TransformerInstance> transformers;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param runOnCleanDB
	 */
	public Pipeline(Long id, String label, String description, Boolean runOnCleanDB) 
	{
		this();
		
		this.id = id;
		this.label = label;
		this.description = description;
		this.runOnCleanDB = runOnCleanDB;
	}
	
	/**
	 * 
	 */
	public Pipeline()
	{
		transformers = new LinkedList<TransformerInstance>();
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

	/**
	 * 
	 * @return
	 */
	public Boolean getRunOnCleanDB() 
	{
		return runOnCleanDB;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<TransformerInstance> getTransformers()
	{
		return transformers;
	}
	
	/**
	 * 
	 * @param transformers
	 */
	public void setTransformers(List<TransformerInstance> transformers)
	{
		this.transformers = transformers;
	}
}
