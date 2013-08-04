package cz.cuni.mff.odcleanstore.webfrontend.util;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.data.GraphLoader;
import cz.cuni.mff.odcleanstore.shared.UUIDUniqueURIGenerator;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendApplication;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TemporaryGraphsDao;

/**
 * Utility class for importing data to a temporary graph in dirty database.
 * @author Jan Michelfeit
 */
public class TemporaryGraphLoader
{
	protected static Logger logger = Logger.getLogger(TemporaryGraphLoader.class);
	
	private UUIDUniqueURIGenerator graphNameGenerator;
	
	/**
	 * Handle for a temporary graph in the dirty database.
	 */
	public class TemporaryGraph {
		private String graphURI;

		/**
		 * Constructor
		 * @param graphURI graph URI
		 */
		public TemporaryGraph(String graphURI) {
			this.graphURI = graphURI;
		}
		
		/** Return graph URI. */
		public String getGraphURI() {
			return graphURI;
		}
		
		/** Drop the graph. Call after work with the graph is finished. */ 
		public void deleteGraph() throws Exception {
			try {
				getTempGraphsDao().delete(graphURI);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
	}
	
	public TemporaryGraphLoader() {
		this.graphNameGenerator = new UUIDUniqueURIGenerator(ODCSInternal.DEBUG_TEMP_GRAPH_URI_PREFIX);		
	}
	
	/**
	 * Inserts the given RDF data, serialized as RDF/XML or TTL, to a temporary graph in dirty database.
	 * After work with the temporary graph is finished, call {@link TemporaryGraph#deleteGraph()} to drop the graph.
	 * @param data RDF data serialized as TTL or RDF/XML
	 * @return handle for the temporary graph
	 * @throws Exception
	 */
	public TemporaryGraph importToTemporaryGraph(String data) throws Exception 
	{
		GraphLoader graphLoader = new GraphLoader(EnumDatabaseInstance.DIRTY);
		TemporaryGraph tmpGraph = new TemporaryGraph(graphNameGenerator.nextURI());
		
		graphLoader.importGraph(data, tmpGraph.getGraphURI());
		getTempGraphsDao().save(tmpGraph.getGraphURI());
		
		return tmpGraph;
	}
	
	private TemporaryGraphsDao getTempGraphsDao() {
		ODCSWebFrontendApplication app = (ODCSWebFrontendApplication) ODCSWebFrontendApplication.get();
		return app.getDaoLookupFactory().getDao(TemporaryGraphsDao.class);
	}
}
