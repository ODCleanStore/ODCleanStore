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
	private OIOutputType outputType;
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
	public OIOutput(Long id, Long ruleId, OIOutputType outputType, Double minConfidence, 
		Double maxConfidence, String filename, OIFileFormat fileFormat) 
	{
		super(id);
		
		this.ruleId = ruleId;
		this.outputType = outputType;
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
	public OIOutputType getOutputType() 
	{
		return outputType;
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
}
