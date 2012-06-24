package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

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
