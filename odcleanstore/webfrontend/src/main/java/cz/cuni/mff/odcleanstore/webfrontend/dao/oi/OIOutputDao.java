package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class OIOutputDao extends DaoForEntityWithSurrogateKey<OIOutput>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_OUTPUTS";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<OIOutput> rowMapper;
	
	public OIOutputDao()
	{
		this.rowMapper = new OIOutputRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<OIOutput> getRowMapper() 
	{
		return this.rowMapper;
	}

	@Override
	public OIOutput loadBy(String column, Object value)
	{
		String query = 
			"SELECT " +
			"id, ruleId, minConfidence, maxConfidence, fileName, " +
			"OT.id as otid, OT.label as otlbl, OT.description as otdescr " +
			"FF.id as ffid, FF.label as fflbl, FF.description as ffdescr " +
			"FROM " + TABLE_NAME + " AS O" +
			"JOIN " + OIOutputTypeDao.TABLE_NAME + " AS OT ON (O.outputTypeId = OT.id) " +
			"LEFT OUTER JOIN " + OIFileFormatDao.TABLE_NAME + " AS FF ON (O.fileFormatId = FF.id) " +
			"WHERE column = ?";
		
		Object[] params = { value };
		
		return getJdbcTemplate().queryForObject(query, params, getRowMapper());
	}
	
	@Override
	public void save(OIOutput output)
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " " +
			"(ruleId, outputTypeId, minConfidence, maxConfidence, fileName, fileFormatId) " +
			"VALUES (?, ?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			output.getRuleId(),
			output.getOutputType().getId(),
			output.getMinConfidence(),
			output.getMaxConfidence(),
			output.getFilename(),
			(output.getFileFormat() == null ? null : output.getFileFormat().getId())
		};
		
		getJdbcTemplate().update(query, params);
	}
}
