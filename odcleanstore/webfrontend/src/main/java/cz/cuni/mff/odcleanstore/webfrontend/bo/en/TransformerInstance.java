package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent an instance of a transformer.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class TransformerInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	private Integer transformerId;
	private Integer pipelineId;
	private String label;
	private String configuration;
	private Boolean runOnCleanDB;
	private Integer priority;
	
	/**
	 * 
	 * @param id
	 * @param transformerId
	 * @param pipelineId
	 * @param label
	 * @param configuration
	 * @param runOnCleanDB
	 * @param priority
	 */
	public TransformerInstance(Integer id, Integer transformerId, Integer pipelineId, String label,
		String configuration, Boolean runOnCleanDB, Integer priority) 
	{
		super(id);
		
		this.transformerId = transformerId;
		this.pipelineId = pipelineId;
		this.label = label;
		this.configuration = configuration;
		this.runOnCleanDB = runOnCleanDB;
		this.priority = priority;
	}
	
	/**
	 * 
	 * @param transformerId
	 * @param pipelineId
	 * @param label
	 * @param configuration
	 * @param runOnCleanDB
	 * @param priority
	 */
	public TransformerInstance(Integer transformerId, Integer pipelineId, String label, 
		String configuration, Boolean runOnCleanDB, Integer priority) 
	{
		this.transformerId = transformerId;
		this.pipelineId = pipelineId;
		this.label = label;
		this.configuration = configuration;
		this.runOnCleanDB = runOnCleanDB;
		this.priority = priority;
	}
	

	/**
	 * 
	 * @param transformerId
	 * @param pipelineId
	 * @param configuration
	 * @param runOnCleanDB
	 * @param priority
	 */
	public TransformerInstance(Integer transformerId, Integer pipelineId,  
		String configuration, Boolean runOnCleanDB, Integer priority) 
	{
		this.transformerId = transformerId;
		this.pipelineId = pipelineId;
		this.configuration = configuration;
		this.runOnCleanDB = runOnCleanDB;
		this.priority = priority;
	}
	
	/**
	 * 
	 */
	public TransformerInstance()
	{
	}

	/**
	 * 
	 * @return
	 */
	public Integer getTransformerId()
	{
		return transformerId;
	}
	
	/**
	 * 
	 * @param transformerId
	 */
	public void setTransformerId(Integer transformerId)
	{
		this.transformerId = transformerId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getPipelineId()
	{
		return pipelineId;
	}
	
	/**
	 * 
	 * @param pipelineId
	 */
	public void setPipelineId(Integer pipelineId)
	{
		this.pipelineId = pipelineId;
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
	public String getConfiguration() 
	{
		return configuration;
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
	public Integer getPriority() 
	{
		return priority;
	}
	
	/**
	 * 
	 * @return
	 */
	public void setPriority(Integer priority) 
	{
		this.priority = priority;
	}
}
