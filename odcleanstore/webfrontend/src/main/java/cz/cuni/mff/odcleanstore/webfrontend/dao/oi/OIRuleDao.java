package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

public class OIRuleDao extends DaoForAuthorableEntity<OIRule>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_RULES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<OIRule> rowMapper;
	
	public OIRuleDao()
	{
		this.rowMapper = new OIRuleRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<OIRule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(OIRule item) throws Exception
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

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("label: " + item.getLabel());
		logger.debug("linkType: " + item.getLinkType());
		logger.debug("sourceRestriction: " + item.getSourceRestriction());
		logger.debug("targetRestriction: " + item.getTargetRestriction());
		logger.debug("linkageRule: " + item.getLinkageRule());
		logger.debug("filterThreshold: " + item.getFilterThreshold());
		logger.debug("filterLimit: " + item.getFilterLimit());
		
		jdbcUpdate(query, params);
	}
	
	public void update(OIRule item) throws Exception
	{
		String query =
			"UPDATE " + TABLE_NAME + 
			" SET label = ?, linkType = ?, sourceRestriction = ?, targetRestriction = ?, linkageRule = ?, filterThreshold = ?, filterLimit = ? " +
			" WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getLinkType(),
			item.getSourceRestriction(),
			item.getTargetRestriction(),
			item.getLinkageRule(),
			item.getFilterThreshold(),
			item.getFilterLimit(),
			item.getId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("linkType: " + item.getLinkType());
		logger.debug("sourceRestriction: " + item.getSourceRestriction());
		logger.debug("targetRestriction: " + item.getTargetRestriction());
		logger.debug("linkageRule: " + item.getLinkageRule());
		logger.debug("filterThreshold: " + item.getFilterThreshold());
		logger.debug("filterLimit: " + item.getFilterLimit());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}

	@Override
	public int getAuthorId(Integer entityId)
	{
		String query = "SELECT g.authorId " +
				"\n FROM " + TABLE_NAME + " AS r JOIN " + OIRulesGroupDao.TABLE_NAME + " AS g ON (g.id = r.groupId)" +
				"\n WHERE r.id = ?";
		return jdbcQueryForInt(query, entityId);
	}
}
