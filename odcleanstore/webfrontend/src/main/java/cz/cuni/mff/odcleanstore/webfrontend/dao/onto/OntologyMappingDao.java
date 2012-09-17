package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class OntologyMappingDao extends DaoForEntityWithSurrogateKey<RelationType> 
{
	private static final long serialVersionUID = 1L;
	
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
		
		return getJdbcTemplate().queryForList(query, String.class);
	}

	public void addMapping(String graphName, String sourceUri, String relationType, String targetUri)
	{
		//FIXME: virtuoso.jdbc3.VirtuosoException: executeUpdate can execute only update/insert/delete queries
		String query = "SPARQL INSERT INTO <" + graphName + "> {<" + sourceUri + "> <" + relationType + "> <" + targetUri + ">}";
		
		getJdbcTemplate().update(query);
	}
}
