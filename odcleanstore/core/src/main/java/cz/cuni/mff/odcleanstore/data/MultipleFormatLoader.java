package cz.cuni.mff.odcleanstore.data;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.shared.UUIDUniqueURIGenerator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.jena.driver.VirtGraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility used to transform input streams in RDF/XML, ...
 * @author Jakub Daniel
 */
public class MultipleFormatLoader {
	private static final Logger LOG = LoggerFactory.getLogger(MultipleFormatLoader.class);
	
	private String uriBase;
	private UniqueURIGenerator graphNameGenerator;
	private JDBCConnectionCredentials connectionCredentials;
	
	public MultipleFormatLoader(String uriBase, JDBCConnectionCredentials connectionCredentials) {
		this.uriBase = uriBase;
		this.graphNameGenerator = new UUIDUniqueURIGenerator(uriBase);
		this.connectionCredentials = connectionCredentials;
	}

    public HashMap<String, String> load(String source) throws IOException {     
        String[] formats = {"RDF/XML", "TTL", "N3", "TRIG"};
        
        for (String format : formats) {
        	HashMap<String, String> graphs = attemptToReadInto(source, format);
        	
        	if (graphs != null) {
        		return graphs;
        	}
        }

        throw new IOException("Could not interpret input source.");
    }
    
	private static String markTemporaryGraph = "INSERT REPLACING DB.ODCLEANSTORE.TEMPORARY_GRAPHS (graphName) VALUES (?)";
	private static String unmarkTemporaryGraph = "DELETE FROM DB.ODCLEANSTORE.TEMPORARY_GRAPHS WHERE graphName = ?";
    
	private void markTemporaryGraph(String temporaryName) throws DatabaseException {
		performUpdate(markTemporaryGraph, "Could not mark temporary graph", temporaryName);
	}

	private void unmarkTemporaryGraph(String temporaryName) throws DatabaseException {
		performUpdate(unmarkTemporaryGraph, "Could not unmark temporary graph", temporaryName);
	}

	private void performUpdate(String query, String errorMessage, Object... objects) throws DatabaseException {
		VirtuosoConnectionWrapper connection = null;

		try {
			connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);

			connection.execute(query, objects);
		} catch (DatabaseException e) {
			LOG.error(errorMessage);
			LOG.error(e.getMessage());
			
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (DatabaseException e) {
				}
			}
		}
	}
    
    private HashMap<String, String> attemptToReadInto(String source, String format) {
    	HashMap<String, String> graphs = new HashMap<String, String>();

        try {
        	NamedGraphSetImpl namedGraphSet = new NamedGraphSetImpl();
        	GraphReaderService reader = new GraphReaderService();
        	reader.setSourceString(source, uriBase);
            reader.setLanguage(format);
            reader.readInto(namedGraphSet);

            Iterator<NamedGraph> inputGraphs = namedGraphSet.iterator();
            
            while (inputGraphs.hasNext()) {
            	NamedGraph graph = inputGraphs.next();
            	
            	String graphName = graph.getGraphName().toString();
            	String temporaryGraphName;
            	
				if (graphs.containsKey(graphName)) {
					temporaryGraphName = graphs.get(graphName);
				} else {
					temporaryGraphName = graphNameGenerator.nextURI();

					/**
					 * Make sure it is noted what graphs are temporarily in the database
					 */
					markTemporaryGraph(temporaryGraphName);

					graphs.put(graphName, temporaryGraphName);
				}
            	
            	VirtGraph temporaryGraph = new VirtGraph(temporaryGraphName,
						connectionCredentials.getConnectionString(),
						connectionCredentials.getUsername(),
						connectionCredentials.getPassword());

				/**
				 * Copying contents into unique temporary destination graphs in dirty database
				 */
				LOG.info("Copying input data from <{}> to debug graph <{}>", graphName, temporaryGraphName);
				
				Model modelTemp = ModelFactory.createModelForGraph(temporaryGraph);
				Model modelInput = ModelFactory.createModelForGraph(graph);
				
				try {
					modelTemp.add(modelInput, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				LOG.info("Input debug graph <{}> copied into <{}>", graphName, temporaryGraphName);
            }
            
            return graphs;
        } catch (Exception e) {
        	LOG.warn("Attempt to interpret " + format + " failed: " + e.getMessage());

        	unload(graphs);
        	
        	return null;
        }
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
				LOG.error("Could not clear temporary graph <{}>", graphs.get(key));
			}
		}
	}
}
