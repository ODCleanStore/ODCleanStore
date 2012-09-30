package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RulesGroupDao;

public class QARulesGroupDao extends RulesGroupDao<QARulesGroup>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES_GROUPS";

	private static final long serialVersionUID = 1L;
	
	private QARulesGroupRowMapper rowMapper;
	
	public QARulesGroupDao()
	{
		rowMapper = new QARulesGroupRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<QARulesGroup> getRowMapper() 
	{
		return rowMapper;
	}
}
