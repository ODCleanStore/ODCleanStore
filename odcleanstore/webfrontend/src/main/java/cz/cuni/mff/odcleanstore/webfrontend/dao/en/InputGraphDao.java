package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.EngineConfig;
import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraph;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class InputGraphDao extends DaoForEntityWithSurrogateKey<InputGraph> {

	private static final long serialVersionUID = 1L;
	public static final String GRAPHS_IN_ERROR_TABLE_NAME = TABLE_NAME_PREFIX + "EN_GRAPHS_IN_ERROR";
	public static final String INPUT_GRAPHS_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS";
	public static final String INPUT_GRAPHS_STATES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";
	public static final String PIPELINE_ERROR_TYPES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_PIPELINE_ERROR_TYPES";
	public static final String PIPELINES_TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";
	public static final String ATTACHED_ENGINES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_ATTACHED_ENGINES";

	@Override
	public String getTableName() {
		return INPUT_GRAPHS_TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<InputGraph> getRowMapper() {
		return new InputGraphRowMapper();
	}
	
	@Override
	public InputGraph load(Integer id) {
		List<InputGraph> graphs = loadAllBy("iGraph.id", id);

		if (graphs.size() != 1) return null;
		
		return graphs.get(0);
	}
	
	@Override
	public List<InputGraph> loadAllBy (String column, Object value) {
		QueryCriteria criteria = new QueryCriteria();
		
		criteria.addWhereClause(column, value);
		
		return loadAllBy(criteria);
	}

	@Override
	public List<InputGraph> loadAllBy (QueryCriteria criteria) {

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
			ATTACHED_ENGINES_TABLE_NAME + " AS engine ON engine.id = iGraph.engineId " +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, param, getRowMapper());
	}
	
	private String getDirName(EnumDatabaseInstance instance) {
		EngineConfig config = ConfigLoader.getConfig().getEngineGroup();

		return instance == EnumDatabaseInstance.CLEAN ? config.getCleanImportExportDir() : config.getDirtyImportExportDir();
	}
	
	private File getDumpFile(InputGraph inputGraph) {
		File file = new File(getDirName(inputGraph.isInCleanDB ? EnumDatabaseInstance.CLEAN : EnumDatabaseInstance.DIRTY), inputGraph.UUID + "-" + UUID.randomUUID());

		file.deleteOnExit();

		return file;
	}

	public String getContent(InputGraph inputGraph) throws Exception {
		String query =
				"CALL dump_graph_ttl(?, ?)";
		
		File file = getDumpFile(inputGraph);

		String filename = file.getAbsolutePath().replace("\\", "/");
		
		try {
			Object[] param = { ODCSInternal.dataGraphUriPrefix + inputGraph.UUID, filename };
		
			jdbcUpdate(query, param);
			
			StringBuilder content = new StringBuilder();
			
			for (String line : Files.readAllLines(file.toPath(), Charset.defaultCharset())) {
				content.append(line);
				content.append("\n");
			}
			
			return content.toString();
		} finally {
			file.delete();
		}
		
		
	}
}