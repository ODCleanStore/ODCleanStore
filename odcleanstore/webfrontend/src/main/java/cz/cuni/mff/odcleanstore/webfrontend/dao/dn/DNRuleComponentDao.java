package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.AggregationTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.MultivalueTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.PropertySettingsDao;

/**
 * 
 * @author Dusan
 *
 */
public class DNRuleComponentDao extends DaoForEntityWithSurrogateKey<DNRuleComponent>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULE_COMPONENTS";

	private static final long serialVersionUID = 1L;
	
	private DNRuleComponentRowMapper rowMapper;
	
	public DNRuleComponentDao()
	{
		rowMapper = new DNRuleComponentRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNRuleComponent> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public List<DNRuleComponent> loadAll() 
	{
		String query = 
			"SELECT C.id as id, ruleId, modification, C.description as description, " +
			"CT.id as typeId, CT.label as typeLbl, CT.description as typeDescr " +
			"FROM " + TABLE_NAME + " as C " +
			"JOIN " + DNRuleComponentTypeDao.TABLE_NAME + " as CT " +
			"ON CT.id = C.typeId ";
		
		return getJdbcTemplate().query(query, getRowMapper());
	}
	
	@Override
	public List<DNRuleComponent> loadAllRawBy(String columnName, Object value)
	{
		String query = 
			"SELECT C.id as id, ruleId, modification, C.description as description, " +
			"CT.id as typeId, CT.label as typeLbl, CT.description as typeDescr " +
			"FROM " + TABLE_NAME + " as C " +
			"JOIN " + DNRuleComponentTypeDao.TABLE_NAME + " as CT " +
			"ON CT.id = C.typeId " +
			"WHERE " + columnName + " = ?";
		
		Object[] params = { value };
		
		return getJdbcTemplate().query(query, params, getRowMapper());
	}
	
	@Override
	public DNRuleComponent load(Long id)
	{
		String query = 
			"SELECT C.id as id, ruleId, modification, C.description as description, " +
			"CT.id as typeId, CT.label as typeLbl, CT.description as typeDescr " +
			"FROM " + TABLE_NAME + " as C " +
			"JOIN " + DNRuleComponentTypeDao.TABLE_NAME + " as CT " +
			"ON CT.id = C.typeId " +
			"WHERE C.id = ?";
		
		Object[] params = { id };
			
		return getJdbcTemplate().queryForObject(query, params, getRowMapper());
	}
	
	@Override
	public void save(DNRuleComponent item)
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (ruleId, typeId, modification, description) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getRuleId(),
			item.getType().getId(),
			item.getModification(),
			item.getDescription()
		};
		
		logger.debug("ruleId: " + item.getRuleId());
		logger.debug("typeId: " + item.getType().getId());
		logger.debug("modification: " + item.getModification());
		logger.debug("description: " + item.getDescription());
		
		getJdbcTemplate().update(query, params);
	}

	@Override
	public void update(DNRuleComponent item)
	{
		String query = 
			"UPDATE " + TABLE_NAME + " " +
			"SET typeId = ?, modification = ?, description = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getType().getId(),
			item.getModification(),
			item.getDescription(),
			item.getId()
		};
		
		logger.debug("typeId: " + item.getType().getId());
		logger.debug("description: " + item.getModification());
		logger.debug("description: " + item.getDescription());
		logger.debug("id: " + item.getId());
		
		getJdbcTemplate().update(query, params);
	}
}
