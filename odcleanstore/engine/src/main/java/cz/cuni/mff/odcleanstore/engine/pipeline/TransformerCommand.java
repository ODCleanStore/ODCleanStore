package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;

/**
 *  @author Petr Jerman
 */
public final class TransformerCommand {
	
	private String _jarPath;
	private String _fullClassName;
	private String _workDirPath;
	private String _configuration;

	String getJarPath() {
		return _jarPath;
	}

	String getFullClassName() {
		return _fullClassName;
	}

	String getWorkDirPath() {
		return _workDirPath;
	}

	String getConfiguration() {
		return _configuration;
	}

	private TransformerCommand() {
	}

	static Collection<TransformerCommand> getActualPlan(String dbSchemaPrefix, int pipelineId)
			throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			final ArrayList<TransformerCommand> commands = new ArrayList<TransformerCommand>();

			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			
			String sqlStatement = String
					.format("Select t.jarPath, t.fullClassName, ti.workDirPath, ti.configuration \n" +
							"FROM %s.TRANSFORMERS t, %s.PIPELINES p, %s.TRANSFORMER_INSTANCES ti \n" +
							"WHERE t.id = ti.transformerId AND ti.pipelineId = p.id \n" +
							"AND p.id='%s' \n" +
							"ORDER BY ti.priority",
							dbSchemaPrefix, dbSchemaPrefix, dbSchemaPrefix, pipelineId);
			
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			while(resultSet.next()) {
				TransformerCommand tc = new TransformerCommand();
				tc._jarPath = resultSet.getString("jarPath");
				tc._workDirPath = resultSet.getString("workDirPath");
				tc._fullClassName = resultSet.getString("fullClassName");
				tc._configuration = resultSet.getNString("configuration");
				commands.add(tc);
			}

			return commands;

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
}
