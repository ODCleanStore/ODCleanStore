package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class QARuleDao extends DaoForEntityWithSurrogateKey<QARule>
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
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<QARule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(QARule item) throws Exception
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

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("filter: " + item.getFilter());
		logger.debug("description: " + item.getDescription());
		logger.debug("coefficient: " + item.getCoefficient());
		
		jdbcUpdate(query, params);
	}
	
	public void update(QARule item) throws Exception
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET filter = ?, description = ?, coefficient = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getFilter(),
			item.getDescription(),
			item.getCoefficient(),
			item.getId()
		};
		
		logger.debug("filter: " + item.getFilter());
		logger.debug("description: " + item.getDescription());
		logger.debug("coefficient: " + item.getCoefficient());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
}
