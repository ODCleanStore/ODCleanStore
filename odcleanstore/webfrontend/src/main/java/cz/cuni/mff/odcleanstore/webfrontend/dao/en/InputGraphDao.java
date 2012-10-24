package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.io.File;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.data.GraphLoaderUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraph;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class InputGraphDao extends DaoForEntityWithSurrogateKey<InputGraph>
{

	private static final long serialVersionUID = 1L;
	public static final String GRAPHS_IN_ERROR_TABLE_NAME = TABLE_NAME_PREFIX + "EN_GRAPHS_IN_ERROR";
	public static final String INPUT_GRAPHS_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS";
	public static final String INPUT_GRAPHS_STATES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";
	public static final String PIPELINE_ERROR_TYPES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_PIPELINE_ERROR_TYPES";
	public static final String PIPELINES_TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";
	public static final String ATTACHED_ENGINES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_ATTACHED_ENGINES";

	private ParameterizedRowMapper<InputGraph> rowMapper;

	public InputGraphDao()
	{
		rowMapper = new InputGraphRowMapper();
	}

	@Override
	public InputGraph load(Integer id)
	{
		return loadBy("iGraph.id", id);
	}

	@Override
	public String getTableName()
	{
		return INPUT_GRAPHS_TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<InputGraph> getRowMapper()
	{
		return rowMapper;
	}

	@Override
	protected String getSelectAndFromClause()
	{
		String query =
			"SELECT " +
				"iGraph.id AS id, " +
				"iGraph.engineId AS engineId, " +
				"iGraph.pipelineId AS pipelineId, " +
				"iGraph.uuid AS uuid, " +
				"iGraph.stateId AS stateId, " +
				"iGraph.isInCleanDB AS isInCleanDB, " +
				"iGraph.updated AS updated, " +
				"engine.uuid AS engineUuid, " +
				"pipeline.label AS pipelineLabel, " +
				"iState.label AS stateLabel " +
				"FROM " +
				INPUT_GRAPHS_TABLE_NAME + " AS iGraph JOIN " +
				INPUT_GRAPHS_STATES_TABLE_NAME + " AS iState ON iGraph.stateId = iState.id JOIN " +
				PIPELINES_TABLE_NAME + " AS pipeline ON pipeline.id = iGraph.pipelineId JOIN " +
				ATTACHED_ENGINES_TABLE_NAME + " AS engine ON engine.id = iGraph.engineId ";
		return query;
	}

	public File getContentFile(Integer graphId) throws Exception
	{
		InputGraph inputGraph = load(graphId);
		String graphName = ODCSInternal.dataGraphUriPrefix + inputGraph.UUID;

		VirtuosoConnectionWrapper connection = null;
		File tmpFile = null;
		try
		{

			connection = createVirtuosoConnectionWrapper(EnumDatabaseInstance.CLEAN);
			tmpFile = GraphLoaderUtils.getImportExportTmpFile(connection, EnumDatabaseInstance.CLEAN);
			connection.exportToTTL(tmpFile, graphName);
		}
		finally
		{
			if (connection != null)
			{
				connection.closeQuietly();
			}
		}

		return tmpFile;
	}
}
