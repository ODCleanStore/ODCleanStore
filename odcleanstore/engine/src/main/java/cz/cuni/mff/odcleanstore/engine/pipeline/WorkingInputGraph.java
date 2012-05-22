package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;

import virtuoso.jena.driver.VirtModel;

import cz.cuni.mff.odcleanstore.engine.Engine;
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
		for (String graphName : graphNames) {
			Model dstModel = null;
			Model srcModel = null;
			try {
				srcModel = VirtModel.openDatabaseModel(graphName, Engine.DIRTY_DATABASE_ENDPOINT.getUri(), Engine.DIRTY_DATABASE_ENDPOINT.getUsername(), Engine.DIRTY_DATABASE_ENDPOINT.getPassword());
				dstModel = VirtModel.openDatabaseModel(graphName, Engine.CLEAN_DATABASE_ENDPOINT.getUri(), Engine.CLEAN_DATABASE_ENDPOINT.getUsername(), Engine.CLEAN_DATABASE_ENDPOINT.getPassword());
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
