package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import java.io.Serializable;

public class TransformerInstance implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Long transformerId;
	private Long pipelineId;
	private String label;
	private String workDirPath;
	private String configuration;
	private Integer priority;
	
	/**
	 * 
	 * @param transformerId
	 * @param pipelineId
	 * @param label
	 * @param workDirPath
	 * @param configuration
	 * @param priority
	 */
	public TransformerInstance(Long transformerId, Long pipelineId, String label, String workDirPath, 
		String configuration, Integer priority) 
	{
		this.transformerId = transformerId;
		this.pipelineId = pipelineId;
		this.label = label;
		this.workDirPath = workDirPath;
		this.configuration = configuration;
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
	public Integer getPriority() 
	{
		return priority;
	}
}
