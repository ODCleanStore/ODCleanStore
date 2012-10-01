package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;

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
	protected String getSelectAndFromClause()
	{
		String query =
			"SELECT A.id, transformerInstanceId, groupId, G.label as groupLabel, G.description as groupDescription " +
			"FROM " + getTableName() + " AS A " +
			"JOIN " + DNRulesGroupDao.TABLE_NAME + " AS G ON (A.groupId = G.id) ";
		return query;
	}
	
	@Override
	public RuleAssignment load(Integer id)
	{
		return loadBy("A.id", id);
	}

	@Override
	public void save(RuleAssignment item) throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (transformerInstanceId, groupId) " +
			"VALUES (?, ?)";
		
		Object[] params =
		{
			item.getTransformerInstanceId(),
			item.getGroupId()
		};
		
		jdbcUpdate(query, params);
	}
}
