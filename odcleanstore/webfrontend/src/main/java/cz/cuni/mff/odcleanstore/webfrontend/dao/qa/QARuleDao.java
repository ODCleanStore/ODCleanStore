package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.util.HashSet;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class QARuleDao extends Dao<QARule>
{
	private static final String TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES";
	private static final String RESTRICTIONS_TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES_TO_PUBLISHERS_RESTRICTIONS";
	
	@Override
	public void delete(QARule item) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(QARule item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (filter, description, coefficient) " +
			"VALUES (?, ?, ?)";
		
		Object[] params =
		{
			item.getFilter(),
			item.getDescription(),
			item.getCoefficient()
		};
		
		jdbcTemplate.update(query, params);
	}

	@Override
	public void update(QARule item) 
	{
		updateRuleProperties(item);
		clearPublisherRestrictions(item);
		insertCurrentPublisherRestrictions(item);
	}
	
	private void updateRuleProperties(QARule item)
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET " +
			"filter = ?, description = ?, coefficient = ? " +
			"WHERE id = ?";
		
		Object[] params =
		{
			item.getFilter(),
			item.getDescription(),
			item.getCoefficient(),
			item.getId()
		};
		
		jdbcTemplate.update(query, params);
	}
	
	private void clearPublisherRestrictions(QARule item)
	{
		String query =
			"DELETE FROM " + RESTRICTIONS_TABLE_NAME + " " +
			"WHERE ruleId = ?";
		
		Object[] params = 
		{
			item.getId()
		};
		
		jdbcTemplate.update(query, params);
	}

	private void insertCurrentPublisherRestrictions(QARule item)
	{
		String query = "INSERT INTO " + RESTRICTIONS_TABLE_NAME + " VALUES (?, ?)";
		
		Object[] params =
		{
			item.getId(),
			null
		};
		
		for (Publisher publisher : item.getPublisherRestrictions())
		{
			params[1] = publisher.getId();
			jdbcTemplate.update(query, params);
		}
	}
	
	@Override
	public List<QARule> loadAll() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QARule load(Long id) 
	{
		QARule rule = loadRawRule(id);
		
		List<Publisher> publisherRescrictions = loadPublisherRestrictionsForRule(rule.getId());
		rule.setPublisherRestrictions(new HashSet<Publisher>(publisherRescrictions));
		
		return rule;
	}
	
	private QARule loadRawRule(Long id)
	{
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
		Object[] params = { id };
		
		return (QARule) jdbcTemplate.queryForObject
		(
			query, 
			params, 
			new QARuleRowMapper()
		);
	}
	
	private List<Publisher> loadPublisherRestrictionsForRule(Long id)
	{
		String query = 
			"SELECT P.* " +
			"FROM " + RESTRICTIONS_TABLE_NAME + " as PR " +
			"JOIN " + PublisherDao.TABLE_NAME + " as P " +
			"ON P.id = PR.publisherId " +
			"WHERE PR.ruleId = ?";
		
		Object[] params = { id };
		
		return jdbcTemplate.query(query, params, new PublisherRowMapper());
	}
}
