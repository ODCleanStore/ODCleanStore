package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class OIRulesGroupDao extends DaoForEntityWithSurrogateKey<OIRulesGroup>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_RULES_GROUPS";

	private static final long serialVersionUID = 1L;
	
	private OIRulesGroupRowMapper rowMapper;
	
	public OIRulesGroupDao()
	{
		rowMapper = new OIRulesGroupRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<OIRulesGroup> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public OIRulesGroup load(Long id) 
	{
		OIRulesGroup group = loadRaw(id);
		group.setRules(loadAllRules(id));
		return group;
	}
	
	private List<OIRule> loadAllRules(Long groupId)
	{
		String query = "SELECT * FROM " + OIRuleDao.TABLE_NAME + " WHERE groupId = ?";
		Object[] params = { groupId };
		
		return getJdbcTemplate().query(query, params, new OIRuleRowMapper());
	}
	
	@Override
	public void save(OIRulesGroup item)
	{
		String query = "INSERT INTO " + TABLE_NAME + " (label, description) VALUES (?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
