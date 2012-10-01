package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class OIOutputDao extends DaoForAuthorableEntity<OIOutput>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_OUTPUTS";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<OIOutput> rowMapper;
	
	public OIOutputDao()
	{
		this.rowMapper = new OIOutputRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<OIOutput> getRowMapper() 
	{
		return this.rowMapper;
	}

	@Override
	public List<OIOutput> loadAllBy(QueryCriteria criteria)
	{
		String query = 
			"SELECT " +
			"O.id as oid, ruleId, minConfidence, maxConfidence, fileName, " +
			"OT.id as otid, OT.label as otlbl, OT.description as otdescr, " +
			"FF.id as ffid, FF.label as fflbl, FF.description as ffdescr " +
			"FROM " + TABLE_NAME + " AS O " +
			"JOIN " + OIOutputTypeDao.TABLE_NAME + " AS OT ON (O.outputTypeId = OT.id) " +
			"LEFT OUTER JOIN " + OIFileFormatDao.TABLE_NAME + " AS FF ON (O.fileFormatId = FF.id) " +
			criteria.buildWhereClause();
		
		Object[] params = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, params, getRowMapper());
	}
	
	@Override
	public OIOutput load(Integer id)
	{
		String query = 
				"SELECT " +
				"O.id as oid, ruleId, minConfidence, maxConfidence, fileName, " +
				"OT.id as otid, OT.label as otlbl, OT.description as otdescr, " +
				"FF.id as ffid, FF.label as fflbl, FF.description as ffdescr " +
				"FROM " + TABLE_NAME + " AS O " +
				"JOIN " + OIOutputTypeDao.TABLE_NAME + " AS OT ON (O.outputTypeId = OT.id) " +
				"LEFT OUTER JOIN " + OIFileFormatDao.TABLE_NAME + " AS FF ON (O.fileFormatId = FF.id) " +
				"WHERE O.id = ?";
			
		Object[] params = { id };
		
		return jdbcQueryForObject(query, params, getRowMapper());
	}
	
	@Override
	public void save(OIOutput output)  throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " " +
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
			"UPDATE " + TABLE_NAME + " " +
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

	@Override
	public int getAuthorId(Integer entityId)
	{
		String query = "SELECT g.authorId " +
			"\n FROM " + OIRuleDao.TABLE_NAME + " AS r JOIN " + OIRulesGroupDao.TABLE_NAME + " AS g ON (g.id = r.groupId)" +
			"\n   JOIN " + TABLE_NAME + " AS o ON (o.ruleId = r.id) " +
			"\n WHERE o.id = ?";
		return jdbcQueryForInt(query, entityId);
	}
}
