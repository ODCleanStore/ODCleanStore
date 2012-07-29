package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RDFGraphEntity;

public abstract class SparqlDao<T extends RDFGraphEntity> extends Dao<T> {

	protected static Logger logger = Logger.getLogger(SparqlDao.class);
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public List<T> loadAllRaw()
	{
		String query = "SELECT * FROM " + getTableName();
		List<T> result = getJdbcTemplate().query(query, getRowMapper());
		for (T entity: result) {
			//TODO load graph into rdfData field
		}
		return result;
	}
}
