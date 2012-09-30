package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RulesGroupDao;

public class DNRulesGroupDao extends RulesGroupDao<DNRulesGroup>
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
}
