package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import java.math.BigDecimal;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO which represents an OI output.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIOutput extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;

	private Integer ruleId;
	private Integer outputTypeId;
	private BigDecimal minConfidence;
	private BigDecimal maxConfidence;
	private String filename;
	private OIFileFormat fileFormat;
	
	/**
	 * 
	 * @param id
	 * @param ruleId
	 * @param outputTypeId
	 * @param minConfidence
	 * @param maxConfidence
	 * @param filename
	 * @param fileFormat
	 */
	public OIOutput(Integer id, Integer ruleId, Integer outputTypeId, BigDecimal minConfidence, 
		BigDecimal maxConfidence, String filename, OIFileFormat fileFormat) 
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
	public Integer getRuleId() 
	{
		return ruleId;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getOutputTypeId() 
	{
		return outputTypeId;
	}

	/**
	 * 
	 * @return
	 */
	public BigDecimal getMinConfidence() 
	{
		return minConfidence;
	}

	/**
	 * 
	 * @return
	 */
	public BigDecimal getMaxConfidence() 
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
	public void setRuleId(Integer ruleId) 
	{
		this.ruleId = ruleId;
	}
	
	/**
	 * 
	 * @param outputTypeId
	 */
	public void setOutputTypeId(Integer outputTypeId)
	{
		this.outputTypeId = outputTypeId;
	}
}
