package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Dusan
 *
 */
public class OIOutput extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;

	private Long ruleId;
	private Long outputTypeId;
	private Double minConfidence;
	private Double maxConfidence;
	private String filename;
	private OIFileFormat fileFormat;
	
	/**
	 * 
	 * @param id
	 * @param ruleId
	 * @param outputType
	 * @param minConfidence
	 * @param maxConfidence
	 * @param filename
	 * @param fileFormat
	 */
	public OIOutput(Long id, Long ruleId, Long outputTypeId, Double minConfidence, 
		Double maxConfidence, String filename, OIFileFormat fileFormat) 
	{
		super(id);
		
		this.ruleId = ruleId;
		this.outputTypeId = outputTypeId;
		this.minConfidence = minConfidence;
		this.maxConfidence = maxConfidence;
		this.filename = filename;
		this.fileFormat = fileFormat;
	}
	
	/**
	 * 
	 */
	public OIOutput()
	{
	}

	/**
	 * 
	 * @return
	 */
	public Long getRuleId() 
	{
		return ruleId;
	}

	/**
	 * 
	 * @return
	 */
	public Long getOutputTypeId() 
	{
		return outputTypeId;
	}

	/**
	 * 
	 * @return
	 */
	public Double getMinConfidence() 
	{
		return minConfidence;
	}

	/**
	 * 
	 * @return
	 */
	public Double getMaxConfidence() 
	{
		return maxConfidence;
	}

	/**
	 * 
	 * @return
	 */
	public String getFilename() 
	{
		return filename;
	}

	/**
	 * 
	 * @return
	 */
	public OIFileFormat getFileFormat() 
	{
		return fileFormat;
	}

	/**
	 * 
	 * @param ruleId
	 */
	public void setRuleId(Long ruleId) 
	{
		this.ruleId = ruleId;
	}
	
	/**
	 * 
	 * @param outputTypeId
	 */
	public void setOutputTypeId(Long outputTypeId)
	{
		this.outputTypeId = outputTypeId;
	}
}
