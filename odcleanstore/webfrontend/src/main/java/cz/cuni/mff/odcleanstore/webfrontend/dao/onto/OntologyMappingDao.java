package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class OntologyMappingDao extends DaoForEntityWithSurrogateKey<RelationType> 
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "RELATION_TYPES";
	
	private ParameterizedRowMapper<RelationType> rowMapper;
	
	public OntologyMappingDao()
	{
		this.rowMapper = new OntologyMappingRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<RelationType> getRowMapper() 
	{
		return rowMapper;
	}

}
