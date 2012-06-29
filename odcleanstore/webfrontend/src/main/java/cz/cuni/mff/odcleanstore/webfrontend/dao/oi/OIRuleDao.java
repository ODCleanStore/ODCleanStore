package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class OIRuleDao extends DaoForEntityWithSurrogateKey<OIRule>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_RULES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<OIRule> rowMapper;
	
	public OIRuleDao()
	{
		this.rowMapper = new OIRuleRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<OIRule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(OIRule item)
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " " +
			"(groupId, label, linkType, sourceRestriction, targetRestriction, linkageRule, filterThreshold, filterLimit) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getLabel(),
			item.getLinkType(),
			item.getSourceRestriction(),
			item.getTargetRestriction(),
			item.getLinkageRule(),
			item.getFilterThreshold(),
			item.getFilterLimit()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
