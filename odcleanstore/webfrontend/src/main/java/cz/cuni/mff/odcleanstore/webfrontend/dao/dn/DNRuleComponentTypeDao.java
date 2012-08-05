package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * 
 * @author Dusan
 *
 */
public class DNRuleComponentTypeDao extends DaoForEntityWithSurrogateKey<DNRuleComponentType>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULE_COMPONENT_TYPES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<DNRuleComponentType> rowMapper;
	
	public DNRuleComponentTypeDao()
	{
		this.rowMapper = new DNRuleComponentTypeRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNRuleComponentType> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void deleteRaw(Long id)
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
}