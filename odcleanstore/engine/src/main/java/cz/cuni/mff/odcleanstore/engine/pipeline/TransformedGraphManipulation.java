package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.rdf.model.Model;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;

/**
 *  @author Petr Jerman
 */
final class TransformedGraphManipulation {

	TransformedGraphManipulation() {
	}

	void deleteGraphFromDirtyDB(String graphName) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
			con.deleteGraph("<" + graphName + ">");
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	void deleteGraphFromCleanDB(String graphName) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			con.deleteGraph("<" + graphName + ">");
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	void deleteGraphsFromDirtyDB(Collection<String> graphNames) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
			for (String graphName : graphNames) {
				con.deleteGraph("<" + graphName + ">");
			}
			con.commit();

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	void deleteGraphsFromCleanDB(Collection<String> graphNames) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			for (String graphName : graphNames) {
				con.deleteGraph("<" + graphName + ">");
			}
			con.commit();

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	void copyGraphsFromDirtyDBToCleanDB(Collection<String> graphNames) throws Exception {
		JDBCConnectionCredentials creditClean = ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();
		JDBCConnectionCredentials creditDirty = ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials();
		for (String graphName : graphNames) {
			Model dstModel = null;
			Model srcModel = null;
			try {
				srcModel = VirtModel.openDatabaseModel(graphName, creditDirty.getConnectionString(), creditDirty.getUsername(), creditDirty.getPassword());
				dstModel = VirtModel.openDatabaseModel(graphName, creditClean.getConnectionString(), creditClean.getUsername(), creditClean.getPassword());
				dstModel.add(srcModel);
			} finally {
				if (srcModel != null) {
					srcModel.close();
				}
				if (dstModel != null) {
					dstModel.close();
				}
			}
		}
	}
}
