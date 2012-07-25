package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.rdf.model.Model;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;

/**
 *  @author Petr Jerman
 */
final class WorkingInputGraph {

	WorkingInputGraph() {
	}

	void deleteGraphFromDirtyDB(String graphName) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createDirtyDBConnection();
			sva.deleteGraph("<" + graphName + ">");
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	void deleteGraphFromCleanDB(String graphName) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			sva.deleteGraph("<" + graphName + ">");
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	void deleteGraphsFromDirtyDB(Collection<String> graphNames) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createDirtyDBConnection();
			for (String graphName : graphNames) {
				sva.deleteGraph("<" + graphName + ">");
			}
			sva.commit();

		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	void deleteGraphsFromCleanDB(Collection<String> graphNames) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			for (String graphName : graphNames) {
				sva.deleteGraph("<" + graphName + ">");
			}
			sva.commit();

		} finally {
			if (sva != null) {
				sva.close();
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
