package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.AttachedEngine;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class AttachedEngineDao extends DaoForEntityWithSurrogateKey<AttachedEngine>
{
	// private static Logger logger = Logger.getLogger(OfficialPipelinesDao.class);

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "EN_ATTACHED_ENGINES";

	private ParameterizedRowMapper<AttachedEngine> rowMapper;

	public AttachedEngineDao()
	{
		rowMapper = new AttachedEngineRowMapper();
	}

	@Override
	public String getTableName()
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<AttachedEngine> getRowMapper()
	{
		return rowMapper;
	}
}
