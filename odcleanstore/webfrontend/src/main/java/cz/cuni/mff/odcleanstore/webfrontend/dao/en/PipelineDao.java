package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class PipelineDao extends Dao<Pipeline>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";
	public static final String MAPPING_TABLE_NAME = TABLE_NAME_PREFIX + "TRANSFORMERS_TO_PIPELINES_ASSIGNMENT";
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<Pipeline> getRowMapper() {
		// TODO Auto-generated method stub
		return null;
	}

}
