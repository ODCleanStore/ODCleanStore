package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRulesGroupDao;

public class OIOutputUncommittedDao extends OIOutputDao
{
	public static final String TABLE_NAME = OIOutputDao.TABLE_NAME + AbstractRulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

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
	
	private int getGroupId(Integer ruleId)
	{
		return getLookupFactory().getDao(OIRuleDao.class, true).load(ruleId).getGroupId();
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(load(id).getRuleId());
		getLookupFactory().getDao(OIRulesGroupDao.class).markUncommitted(groupId);
		
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}
	
	@Override
	public void save(OIOutput output)  throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(output.getRuleId());
		getLookupFactory().getDao(OIRulesGroupDao.class).markUncommitted(groupId);
		
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
		// Mark the group as dirty
		Integer groupId = getGroupId(output.getRuleId());
		getLookupFactory().getDao(OIRulesGroupDao.class).markUncommitted(groupId);
		
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
	
	/*package*/void copyToOfficialTable(Integer groupId) throws Exception
	{
		String insertQuery = "INSERT INTO " + super.getTableName() +
			" SELECT o.* " + 
			" FROM " + this.getTableName() + " AS o JOIN " + OIRuleUncommittedDao.TABLE_NAME + " AS r ON (o.ruleId = r.id)" +
			" WHERE r.groupId = ?";
		jdbcUpdate(insertQuery, groupId);
	}
}
