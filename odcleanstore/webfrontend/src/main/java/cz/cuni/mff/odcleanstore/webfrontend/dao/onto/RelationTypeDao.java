package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

/**
 * DAO for relation type entity.
 * @author Tomas Soukup
 *
 */
public class RelationTypeDao extends DaoForEntityWithSurrogateKey<RelationType> 
{
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(RelationTypeDao.class);
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "RELATION_TYPES";
	
	private ParameterizedRowMapper<RelationType> rowMapper;
	
	public RelationTypeDao()
	{
		this.rowMapper = new RelationTypeRowMapper();
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
