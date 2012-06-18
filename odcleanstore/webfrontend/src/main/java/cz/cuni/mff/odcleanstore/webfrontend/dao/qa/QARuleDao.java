package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.util.Pair;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class QARuleDao extends Dao<QARule>
{
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
	public void delete(QARule item) 
	{
		clearPublisherRestrictions(item);
		deleteRaw(item.getId());
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
		
		getJdbcTemplate().update(query, params);
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
		
		getJdbcTemplate().update(query, params);
	}
	
	private void clearPublisherRestrictions(QARule item)
	{
		String query = "DELETE FROM " + RESTRICTIONS_TABLE_NAME + " WHERE ruleId = ?";
		Object[] params = { item.getId() };
		
		getJdbcTemplate().update(query, params);
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
			getJdbcTemplate().update(query, params);
		}
	}
	
	@Override
	public List<QARule> loadAll() 
	{
		Map<Long, QARule> rules = loadAllRawRules();
		Map<Long, Publisher> publishers = loadAllRawPublishers();
		
		addRestrictionsToRules(rules, publishers);
		
		return new LinkedList<QARule>(rules.values());
	}

	private Map<Long, QARule> loadAllRawRules()
	{
		String query = 
			"SELECT " +
				"id, coefficient, " +
				"blob_to_string(description) as description, "  +
				"blob_to_string(filter) as filter " +
			"FROM " + 
				TABLE_NAME;
		
		List<QARule> rules = getJdbcTemplate().query(query, new QARuleRowMapper());
		
		Map<Long, QARule> result = new HashMap<Long, QARule>();
		for (QARule rule : rules)
			result.put(rule.getId(), rule);
		
		return result;
	}
	
	private Map<Long, Publisher> loadAllRawPublishers()
	{
		String query = "SELECT * FROM " + PublisherDao.TABLE_NAME;
		List<Publisher> publishers = getJdbcTemplate().query(query, new PublisherRowMapper());
		
		Map<Long, Publisher> result = new HashMap<Long, Publisher>();
		for (Publisher publisher : publishers)
			result.put(publisher.getId(), publisher);
		
		return result;
	}
	
	private void addRestrictionsToRules(Map<Long, QARule> rules, Map<Long, Publisher> publishers)
	{
		String query = "SELECT * FROM " + RESTRICTIONS_TABLE_NAME;
		
		List<Pair<Long, Long>> mapping = getJdbcTemplate().query
		(
			query, 
			new RulesToPublishersRestrictionsRowMapper()
		);
		
		for (Pair<Long, Long> pair : mapping)
		{
			QARule rule = rules.get(pair.getFirst());
			Publisher publisher = publishers.get(pair.getSecond());
			
			rule.addPublisherRestriction(publisher);
		}
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
		String query =
			"SELECT " +
				"id, coefficient, " +
				"blob_to_string(description) as description, "  +
				"blob_to_string(filter) as filter " +
			"FROM " + 
				TABLE_NAME + " " +
			"WHERE " +
				"id = ?";
		
		Object[] params = { id };
		
		return (QARule) getJdbcTemplate().queryForObject
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
		
		return getJdbcTemplate().query(query, params, new PublisherRowMapper());
	}
}
