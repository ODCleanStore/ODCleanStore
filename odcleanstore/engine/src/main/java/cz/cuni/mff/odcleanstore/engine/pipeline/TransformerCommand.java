package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.common.RowListener;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;

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

	static Collection<TransformerCommand> getActualPlan(String dbSchemaPrefix, String pipelineName)
			throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			final ArrayList<TransformerCommand> commands = new ArrayList<TransformerCommand>();

			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String
					.format("Select t.jarPath, t.fullClassName, tp.workDirPath, tp.configuration \n" +
							"FROM %s.TRANSFORMERS t, %s.PIPELINES p, %s.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT tp \n" +
							"WHERE t.id = tp.transformerId AND tp.pipelineId = p.id \n" +
							"AND p.label='%s' AND p.runOnCleanDB = 0 \n" +
							"ORDER BY tp.priority",
							dbSchemaPrefix, dbSchemaPrefix, dbSchemaPrefix, pipelineName);
			sva.processSqlStatementRows(sqlStatement, new RowListener() {

				@Override
				public void processRow(ResultSet rs, ResultSetMetaData metaData)
						throws SQLException {
					TransformerCommand tc = new TransformerCommand();
					tc._jarPath = rs.getString("jarPath");
					tc._workDirPath = rs.getString("workDirPath");
					tc._fullClassName = rs.getString("fullClassName");

					Blob configuration = rs.getBlob("configuration");
					if (configuration != null) {
						byte[] cbytes = configuration.getBytes(1, (int) configuration.length());
						if (cbytes != null) {
							tc._configuration = new String(cbytes);
						}
					}
					commands.add(tc);
				}
			});

			return commands;

		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}
}
