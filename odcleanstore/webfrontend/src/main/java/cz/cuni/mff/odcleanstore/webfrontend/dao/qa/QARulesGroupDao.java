package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class QARulesGroupDao extends DaoForEntityWithSurrogateKey<QARulesGroup>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES_GROUPS";

	private static final long serialVersionUID = 1L;
	
	private QARulesGroupRowMapper rowMapper;
	
	public QARulesGroupDao()
	{
		rowMapper = new QARulesGroupRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<QARulesGroup> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public QARulesGroup load(Long id) 
	{
		QARulesGroup group = loadRaw(id);
		group.setRules(loadAllRules(id));
		return group;
	}
	
	private List<QARule> loadAllRules(Long groupId)
	{
		String query = "SELECT * FROM " + QARuleDao.TABLE_NAME + " WHERE groupId = ?";
		Object[] params = { groupId };
		
		return getJdbcTemplate().query(query, params, new QARuleRowMapper());
	}
	
	@Override
	public void save(QARulesGroup item)
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
