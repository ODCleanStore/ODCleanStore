package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class OIRuleDao extends Dao<OIRule>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_RULES";
	
	private ParameterizedRowMapper<OIRule> rowMapper;
	
	public OIRuleDao()
	{
		this.rowMapper = new OIRuleRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<OIRule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public List<OIRule> loadAll() 
	{
		return loadAllRaw();
	}

	@Override
	public OIRule load(Long id) 
	{
		return loadRaw(id);
	}
	
	@Override
	public void save(OIRule item)
	{
		String query = "INSERT INTO " + TABLE_NAME + " (groupId, definition) VALUES (?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getDefinition()
		};
		
		jdbcTemplate.update(query, params);
	}
}
