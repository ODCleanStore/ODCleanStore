package cz.cuni.mff.odcleanstore.data;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.shared.UniqueGraphNameGenerator;
import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Utility used to copy graphs into temporary graphs in dirty database
 * @author Jakub Daniel
 */
public class DebugGraphLoader {
	private static final Logger LOG = LoggerFactory.getLogger(DebugGraphLoader.class);

	private static String markTemporaryGraph = "INSERT INTO DB.ODCLEANSTORE.TEMPORARY_GRAPHS (graphName) VALUES (?)";
	private static String unmarkTemporaryGraph = "DELETE FROM DB.ODCLEANSTORE.TEMPORARY_GRAPHS WHERE graphName = ?";

	private String temporaryGraphURIPrefix;
	private JDBCConnectionCredentials connectionCredentials;
	
	public DebugGraphLoader(URI temporaryGraphURIPrefix, JDBCConnectionCredentials connectionCredentials) {
		this.temporaryGraphURIPrefix = temporaryGraphURIPrefix.toString();
		this.connectionCredentials = connectionCredentials;
	}
	
	private static String getInputBaseURI(String temporaryGraphURIPrefix, String discriminator) {
		return temporaryGraphURIPrefix + discriminator + "/input/";
	}
	
	public HashMap<String, String> load(String input, String discriminator) throws Exception {
		try {
			return loadImpl(new MultipleFormatLoader().load(input, getInputBaseURI(this.temporaryGraphURIPrefix, discriminator)), discriminator);
		} catch (Exception e) {
			LOG.error("Could not finish loading debug graphs from input: {}", e.getMessage());
				
			throw e;
		}
	}

	private void markTemporaryGraph(String temporaryName) throws DatabaseException {
		performUpdate(markTemporaryGraph, temporaryName);
	}

	private void unmarkTemporaryGraph(String temporaryName) throws DatabaseException {
		performUpdate(unmarkTemporaryGraph, temporaryName);
	}

	private void performUpdate(String query, Object... objects) throws DatabaseException {
		VirtuosoConnectionWrapper connection = null;

		try {
			connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);

			connection.execute(query, objects);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (DatabaseException e) {
				}
			}
		}
	}
	
	private HashMap<String, String> loadImpl(NamedGraphSetImpl namedGraphSet, String discriminator) throws Exception {	
		/**
		 * Copy them into unique graphs
		 */
		UniqueGraphNameGenerator graphNameGen = new UniqueGraphNameGenerator(temporaryGraphURIPrefix + discriminator + "/debug/", connectionCredentials);
		
		HashMap<String, String> graphs = new HashMap<String, String>();
		
		try {
			Iterator<NamedGraph> it = namedGraphSet.listGraphs();

			while (it.hasNext()) {
				NamedGraph graph = it.next();
			
				String name = graph.getGraphName().toString();
				String temporaryName;
				
				if (graphs.containsKey(name)) {
					temporaryName = graphs.get(name);
				} else {
					temporaryName = graphNameGen.nextURI();

					/**
					 * Make sure it is noted what graphs are temporarily in the database
					 */
					markTemporaryGraph(temporaryName);

					graphs.put(name, temporaryName);
				}
			
				VirtGraph temporaryGraph = new VirtGraph(temporaryName,
						connectionCredentials.getConnectionString(),
						connectionCredentials.getUsername(),
						connectionCredentials.getPassword());

				/**
				 * Copying contents into unique temporary destination graphs in dirty database
				 */
				LOG.info("Copying input data from <{}> to debug graph <{}>", name, temporaryName);
				
				Model modelTemp = ModelFactory.createModelForGraph(temporaryGraph);
				Model modelInput = ModelFactory.createModelForGraph(graph);
				
				try {
					modelTemp.add(modelInput, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				LOG.info("Input debug graph <{}> copied into <{}>", name, temporaryName);
			}
		} catch (Exception e) {
			LOG.error("Could not load debug graphs due to: " + e.getMessage());
			
			unload(graphs);
			
			throw e;
		}
		
		return graphs;
	}
	
	public void unload(HashMap<String, String> graphs) {
		Set<String> keys = graphs.keySet();

		Iterator<String> it = keys.iterator();

		/**
		 * Drop all graphs
		 */
		while (it.hasNext()) {
			String key = it.next();

			try {
				VirtGraph temporaryGraph = new VirtGraph(graphs.get(key),
						connectionCredentials.getConnectionString(),
						connectionCredentials.getUsername(),
						connectionCredentials.getPassword());
				
				temporaryGraph.clear();

				unmarkTemporaryGraph(graphs.get(key));
				
				LOG.info("Temporary copy <{}> of input debug graph <{}> cleared", graphs.get(key), key);
			} catch (Exception e) {
			}
		}
	}
}
