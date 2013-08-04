package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Mapping;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

/**
 * The Ontology mapping DAO.
 * 
 * @author Tomas Soukup
 *
 */
public class OntologyMappingDao extends Dao
{
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(OntologyMappingDao.class);
	
	private final ParameterizedRowMapper<Mapping> rowMapper;
	
	/**
	 * 
	 */
	public OntologyMappingDao()
	{
		this.rowMapper = new MappingRowMapper();
	}
	
	/**
	 * 
	 * @param ontoGraphName
	 * @return
	 */
	public List<String> loadEntityURIs(String ontoGraphName)
	{	
		String query = "SPARQL SELECT ?x FROM <" + ontoGraphName +
				"> WHERE {?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?y}";
		
		return jdbcQueryForList(query, String.class);
	}

	/**
	 * @param ontologyId
	 * @param mapping
	 * @throws ConnectionException
	 * @throws QueryException
	 */
	public void addMapping(Integer ontologyId, Mapping mapping) 
			throws ConnectionException, QueryException
	{	
		String graphName = createGraphName(ontologyId);
		logger.info("Adding ontology mapping: " + mapping.getSourceUri() + " " + mapping.getRelationType() + " "
		+ mapping.getTargetUri() + " into graph: " + graphName);
		
		String mappingQuery = "SPARQL INSERT INTO <" + graphName + "> {`iri(??)` `iri(??)` `iri(??)`}";
		executeMappingQuery(mappingQuery, mapping);
		
		String metadataQuery = "SPARQL INSERT INTO <" + graphName + "> {<" + graphName + "> <" + ODCS.GENERATED_GRAPH + "> 1}";
		executeMappingQuery(metadataQuery, null);
	}
	
	/**
	 * @param ontologyId
	 * @return
	 */
	public static String createGraphName(Integer ontologyId)
	{
		return ODCSInternal.ONTOLOGY_MAPPINGS_GRAPH_URI_PREFIX + ontologyId;
	}
	
	/**
	 * @param ontologyId
	 * @return
	 */
	public List<Mapping> loadAll(Integer ontologyId)
	{		
		String query = "SPARQL SELECT ?sourceUri ?relationType ?targetUri FROM <" + createGraphName(ontologyId) + "> "
				+ "WHERE {?sourceUri ?relationType ?targetUri FILTER (?relationType != <" + ODCS.GENERATED_GRAPH + ">)}";
		
		return jdbcQuery(query, rowMapper);
	}
	
	/**
	 * @param ontologyId
	 * @param mapping
	 * @throws Exception
	 */
	public void delete(Integer ontologyId, Mapping mapping) throws Exception
	{	
		String graphName = createGraphName(ontologyId);
		logger.info("Deleting ontology mapping: " + mapping.getSourceUri() + " " + mapping.getRelationType() + " "
		+ mapping.getTargetUri() + " from graph: " + graphName);
		
		String query = "SPARQL DELETE DATA FROM <" + graphName + "> {<" + mapping.getSourceUri() + "> <" 
				+ mapping.getRelationType() + "> <" + mapping.getTargetUri() + ">}";
		
		executeMappingQuery(query, null);
	}
	
	private void executeMappingQuery(String query, Mapping mapping) throws ConnectionException, QueryException
	{
		VirtuosoConnectionWrapper con = null;
		try
		{
			con = createVirtuosoConnectionWrapper(EnumDatabaseInstance.CLEAN);
			if (mapping != null)
			{
				con.execute(query, mapping.getSourceUri(), mapping.getRelationType(), mapping.getTargetUri());
			} else
			{
				con.execute(query);
			}
		} 
		finally
		{
			if (con != null)
			{
				con.closeQuietly();
			}
		}
	}
}
