package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RulesGroupDao;

public class OIOutputUncommittedDao extends OIOutputDao
{
	public static final String TABLE_NAME = OIOutputDao.TABLE_NAME + RulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected String getRuleTableName()
	{
		return OIRuleUncommittedDao.TABLE_NAME;
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}
	
	@Override
	public void save(OIOutput output)  throws Exception
	{
		String query = 
			"INSERT INTO " + getTableName() + " " +
			"(ruleId, outputTypeId, minConfidence, maxConfidence, fileName, fileFormatId) " +
			"VALUES (?, ?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			output.getRuleId(),
			output.getOutputTypeId(),
			output.getMinConfidence(),
			output.getMaxConfidence(),
			output.getFilename(),
			(output.getFileFormat() == null ? null : output.getFileFormat().getId())
		};
		
		logger.debug("ruleId: " + output.getRuleId());
		logger.debug("outputTypeId: " + output.getOutputTypeId());
		logger.debug("minConfidence: " + output.getMinConfidence());
		logger.debug("maxConfidence: " + output.getMaxConfidence());
		
		jdbcUpdate(query, params);
	}
	
	public void update(OIOutput output) throws Exception
	{
		String query = 
			"UPDATE " + getTableName() + " " +
			"SET outputTypeId = ?, minConfidence = ?, maxConfidence = ?, fileName = ?, fileFormatId = ?" +
			"WHERE id = ?";
		
		Object[] params =
		{
			output.getOutputTypeId(),
			output.getMinConfidence(),
			output.getMaxConfidence(),
			output.getFilename(),
			(output.getFileFormat() == null ? null : output.getFileFormat().getId()),
			output.getId()
		};

		logger.debug("outputTypeId: " + output.getOutputTypeId());
		logger.debug("minConfidence: " + output.getMinConfidence());
		logger.debug("maxConfidence: " + output.getMaxConfidence());
		logger.debug("id: " + output.getId());
		
		jdbcUpdate(query, params);
	}
}
