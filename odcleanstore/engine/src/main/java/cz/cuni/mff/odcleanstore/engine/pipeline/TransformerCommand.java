package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.common.RowListener;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;

public final class TransformerCommand {

	private int _id;
	private String _jarPath;
	private String _fullClassName;
	private String _workDirPath;
	private String _configuration;

	int getId() {
		return _id;
	}

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

	static Collection<TransformerCommand> getActualPlan(String dbSchemaPrefix) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			final ArrayList<TransformerCommand> commands = new ArrayList<TransformerCommand>();

			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Select id, jarPath, fullClassName, workDirPath, configuration from %s.REGISTERED_TRANSFORMERS WHERE active<>0 ORDER BY priority", dbSchemaPrefix);
			sva.processSqlStatementRows(sqlStatement, new RowListener() {

				@Override
				public void processRow(ResultSet rs, ResultSetMetaData metaData) throws SQLException {
					TransformerCommand tc = new TransformerCommand();
					tc._id = rs.getInt("id");
					tc._jarPath = rs.getString("jarPath");
					tc._workDirPath = rs.getString("workDirPath");
					tc._fullClassName = rs.getString("fullClassName");

					Blob configuration = rs.getBlob("configuration");
					tc._configuration = new String(configuration.getBytes(1, (int) configuration.length()));

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
