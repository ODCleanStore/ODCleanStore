/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;

/**
 * @author jermanp
 * 
 */
final class WorkingInputGraph {

	WorkingInputGraph() {
	}

	void deleteGraphFromDirtyDB(String graphName) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createDirtyDBConnection();
			sva.deleteGraphs("<" + graphName + ">");
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
			sva.deleteGraphs("<" + graphName + ">");

		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	void deleteGraphsFromDirtyDB(Collection<String> graphNames) throws Exception {
		for (String graphName : graphNames) {
			deleteGraphFromDirtyDB(graphName);
		}
	}

	void deleteGraphsFromCleanDB(Collection<String> graphNames) throws Exception {
		for (String graphName : graphNames) {
			deleteGraphFromCleanDB(graphName);
		}
	}

	void copyGraphsFromDirtyDBToCleanDB(Collection<String> graphNames) throws Exception {
		SimpleVirtuosoAccess sva = null;
		ArrayList<String[]> triples = new ArrayList<String[]>();
		try {
			sva = SimpleVirtuosoAccess.createDirtyDBConnection();
			for(String graphName : graphNames) {
				graphName = "<" + graphName + ">";
				String statement = String.format("SPARQL SELECT ?s ?p ?o %s WHERE { GRAPH %s {?s ?p ?o} }", graphName, graphName);
				triples.addAll(sva.getRowFromSparqlStatement(statement));
			}
			sva.close();
			
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			for (String[] triple : triples) {
				sva.insertQuad(triple[0], triple[1], triple[2], triple[3]);
			}
			sva.commit();

		} finally {
			if (sva != null) {
				sva.close();
			}
		}	}
}
