package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRulesGroupDao;

public class DNRulesGroupDao extends AbstractRulesGroupDao<DNRulesGroup>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULES_GROUPS";

	private static final long serialVersionUID = 1L;
	
	private DNRulesGroupRowMapper rowMapper;
	
	public DNRulesGroupDao()
	{
		rowMapper = new DNRulesGroupRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNRulesGroup> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	protected Class<? extends AbstractRuleDao<?>> getDependentRuleDao()
	{
		return DNRuleDao.class;
	}
}
