package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class OntologyMappingDao extends DaoForEntityWithSurrogateKey<RelationType> 
{
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(OntologyMappingDao.class);
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "RELATION_TYPES";
	
	private ParameterizedRowMapper<RelationType> rowMapper;
	
	public OntologyMappingDao()
	{
		this.rowMapper = new OntologyMappingRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<RelationType> getRowMapper() 
	{
		return rowMapper;
	}
	
	public List<String> loadEntityURIs(String ontoGraphName)
	{	
		String query = "SPARQL SELECT ?x FROM <" + ontoGraphName +
				"> WHERE {?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?y}";
		
		return jdbcQueryForList(query, String.class);
	}

	public void addMapping(Integer ontologyId, String sourceUri, String relationType, String targetUri) 
			throws ConnectionException, QueryException
	{	
		String graphName = createGraphName(ontologyId);
		logger.info("Adding ontology mapping: " + sourceUri + " " + relationType + " " + targetUri + 
				" into graph: " + graphName);
		String query = "SPARQL INSERT INTO <" + graphName + "> {`iri(??)` `iri(??)` `iri(??)`}";
		
		VirtuosoConnectionWrapper con = null;
		try
		{
			con = VirtuosoConnectionWrapper.createConnection(
					ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			
			con.execute(query, sourceUri, relationType, targetUri);
		} finally
		{
			if (con != null)
			{
				con.closeQuietly();
			}
		}		
	}
	
	public static String createGraphName(Integer ontologyId)
	{
		return ConfigLoader.getConfig().getWebFrontendGroup().getOntologyMappingsGraphURIPrefix() + ontologyId;
	}
}
