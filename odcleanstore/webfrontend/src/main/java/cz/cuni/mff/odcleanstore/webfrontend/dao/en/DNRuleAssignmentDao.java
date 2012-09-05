package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;

public class DNRuleAssignmentDao extends DaoForEntityWithSurrogateKey<RuleAssignment>
{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULES_ASSIGNMENT";
	
	private ParameterizedRowMapper<RuleAssignment> rowMapper;
	
	public DNRuleAssignmentDao()
	{
		this.rowMapper = new RuleAssignmentRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<RuleAssignment> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public List<RuleAssignment> loadAllBy(QueryCriteria criteria)
	{
		String query =
			"SELECT A.id, transformerInstanceId, groupId, G.label as groupLabel, G.description as groupDescription " +
			"FROM " + getTableName() + " AS A " +
			"JOIN " + DNRulesGroupDao.TABLE_NAME + " AS G ON (A.groupId = G.id) " +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();
		
		Object[] params = criteria.buildWhereClauseParams();
		
		return getJdbcTemplate().query(query, params, getRowMapper());
	}
	
	@Override
	public RuleAssignment loadRawBy(String columnName, Object value)
	{
		String query = 
			"SELECT A.id, transformerInstanceId, groupId, G.label as groupLabel, G.description as groupDescription " +
			"FROM " + getTableName() + " AS A " +
			"JOIN " + DNRulesGroupDao.TABLE_NAME + " AS G ON (A.groupId = G.id) " +
			"WHERE " + columnName + " = ?";
		
		Object[] params = { value };
		
		return getJdbcTemplate().queryForObject(query, params, getRowMapper());
	}
	
	@Override
	public void save(RuleAssignment item)
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (transformerInstanceId, groupId) " +
			"VALUES (?, ?)";
		
		Object[] params =
		{
			item.getTransformerInstanceId(),
			item.getGroupId()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
