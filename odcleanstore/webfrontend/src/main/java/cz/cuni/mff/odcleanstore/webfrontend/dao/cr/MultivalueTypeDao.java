package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class MultivalueTypeDao extends Dao<MultivalueType> 
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_MULTIVALUE_TYPES";
	
	@Override
	public void delete(MultivalueType item) 
	{
		throw new UnsupportedOperationException(
			"Cannot delete from " + TABLE_NAME + "."
		);
	}

	@Override
	public void save(MultivalueType item) 
	{
		throw new UnsupportedOperationException(
			"Cannot insert into " + TABLE_NAME + "."
		);
	}

	@Override
	public void update(MultivalueType item) 
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + TABLE_NAME + "."
		);	
	}

	@Override
	public List<MultivalueType> loadAll() 
	{
		return jdbcTemplate.query
		(
			"SELECT * FROM " + TABLE_NAME, 
			new MultivalueTypeRowMapper()
		);
	}

	@Override
	public MultivalueType load(Long id) 
	{
		String query = "SELECT * FROM ? WHERE id = ?";
		
		Object[] params = 
		{
			TABLE_NAME,
			id
		};

		List<MultivalueType> matchingRows = jdbcTemplate.query(
			query,
			params,
			new MultivalueTypeRowMapper()
		);
		
		if (matchingRows.size() != 0)
		{
			throw new IllegalArgumentException(
				"The requested CR multivalue type could not be found."
			);
		}
		
		return matchingRows.get(0);
	}
}
