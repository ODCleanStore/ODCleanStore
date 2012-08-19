package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class TransformerInstance extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	private Long transformerId;
	private Long pipelineId;
	private String label;
	private String workDirPath;
	private String configuration;
	private Boolean runOnCleanDB;
	private Integer priority;
	
	public TransformerInstance(Long id, Long transformerId, Long pipelineId, String label,
		String workDirPath, String configuration, Boolean runOnCleanDB, Integer priority) 
	{
		super(id);
		
		this.transformerId = transformerId;
		this.pipelineId = pipelineId;
		this.label = label;
		this.workDirPath = workDirPath;
		this.configuration = configuration;
		this.runOnCleanDB = runOnCleanDB;
		this.priority = priority;
	}
	
	/**
	 * 
	 * @param transformerId
	 * @param pipelineId
	 * @param label
	 * @param workDirPath
	 * @param configuration
	 * @param priority
	 */
	public TransformerInstance(Long transformerId, Long pipelineId, String label, 
		String workDirPath, String configuration, Boolean runOnCleanDB, Integer priority) 
	{
		this.transformerId = transformerId;
		this.pipelineId = pipelineId;
		this.label = label;
		this.workDirPath = workDirPath;
		this.configuration = configuration;
		this.runOnCleanDB = runOnCleanDB;
		this.priority = priority;
	}
	

	/**
	 * 
	 * @param transformerId
	 * @param pipelineId
	 * @param workDirPath
	 * @param configuration
	 * @param priority
	 */
	public TransformerInstance(Long transformerId, Long pipelineId, String workDirPath, 
		String configuration, Boolean runOnCleanDB, Integer priority) 
	{
		this.transformerId = transformerId;
		this.pipelineId = pipelineId;
		this.label = label;
		this.workDirPath = workDirPath;
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
	public Long getTransformerId()
	{
		return transformerId;
	}
	
	/**
	 * 
	 * @param transformerId
	 */
	public void setTransformerId(Long transformerId)
	{
		this.transformerId = transformerId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getPipelineId()
	{
		return pipelineId;
	}
	
	/**
	 * 
	 * @param pipelineId
	 */
	public void setPipelineId(Long pipelineId)
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
	public String getWorkDirPath() 
	{
		return workDirPath;
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
}
