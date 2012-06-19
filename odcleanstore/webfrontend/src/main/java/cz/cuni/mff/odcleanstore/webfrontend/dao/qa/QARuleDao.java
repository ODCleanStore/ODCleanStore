package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class QARuleDao extends Dao<QARule>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES";
	public static final String RESTRICTIONS_TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES_TO_PUBLISHERS_RESTRICTIONS";
	
	private ParameterizedRowMapper<QARule> rowMapper;
	
	public QARuleDao()
	{
		this.rowMapper = new QARuleRowMapper();
	}

	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<QARule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(QARule item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, filter, description, coefficient) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getFilter(),
			item.getDescription(),
			item.getCoefficient()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
