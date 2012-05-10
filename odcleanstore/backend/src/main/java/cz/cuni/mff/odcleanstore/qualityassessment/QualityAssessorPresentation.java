package cz.cuni.mff.odcleanstore.qualityassessment;

import java.io.File;
import java.util.Collection;

import org.apache.commons.logging.Log;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * This is a shortcut for using QA without the need of engine.
 * It is here just for debugging connection to virtuoso.
 */
public class QualityAssessorPresentation {
	public static void main (String[] args) {
		try {
			Class.forName("virtuoso.jdbc3.Driver");
		} catch (Exception e) {
			System.out.println("DRIVER DID NOT LOAD");
		}
		
		// DO SOMETHING LIKE:
		//INSERT DATA INTO <http://opendata.cz/data/metadata> {<http://opendata.cz/data/namedGraph/1843> <http://purl.org/provenance#publishedBy> <http://opendata.cz>}
		//INSERT INTO DB.FRONTEND.DATA_DOMAINS (id, uri) VALUES (0, 'http://opendata.cz')
		//INSERT INTO DB.FRONTEND.EL_RULES_TO_DOMAINS_RESTRICTIONS (ruleId, domainId) VALUES (4, 0)
		
		final SparqlEndpoint endpoint = new SparqlEndpoint("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba");
		
		QualityAssessor qa = QualityAssessorFactory.createAssessor();
		
		for (int id = 1; id < 1844; ++id)
		{
			TransformedGraph graph = getTransformedGraph(id);
			TransformationContext context = getTransformationContext(endpoint);
			
			try {
				qa.transformNewGraph(graph, context);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				break;
			}
		}
	}
	
	private static TransformedGraph getTransformedGraph (final int id) {
		return new TransformedGraph () {

			@Override
			public String getGraphName() {
				return "http://opendata.cz/data/namedGraph/" + id;
			}
	
			@Override
			public String getGraphId() {
				return null;
			}
	
			@Override
			public String getMetadataGraphName() {
				return "http://opendata.cz/data/metadata";
			}
	
			@Override
			public Collection<String> getAttachedGraphNames() {
				return null;
			}
	
			@Override
			public void addAttachedGraph(String attachedGraphName)
				throws TransformedGraphException {	
			}
	
			@Override
			public void deleteGraph() throws TransformedGraphException {	
			}
	
			@Override
			public boolean isDeleted() {
				return false;
			}
		};
	}
	
	private static TransformationContext getTransformationContext (final SparqlEndpoint endpoint) {
		return new TransformationContext () {
			
			@Override
			public SparqlEndpoint getDirtyDatabaseEndpoint() {
				return endpoint;
			}

			@Override
			public SparqlEndpoint getCleanDatabaseEndpoint() {				
				return endpoint;
			}
	
			@Override
			public String getTransformerConfiguration() {
				return null;
			}
	
			@Override
			public File getTransformerDirectory() {
				return null;
			}
	
			@Override
			public EnumTransformationType getTransformationType() {
				return null;
			}
		};
	}
}
