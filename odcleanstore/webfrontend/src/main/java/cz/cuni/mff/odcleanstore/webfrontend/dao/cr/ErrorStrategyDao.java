package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import java.util.List;

public class ErrorStrategyDao extends Dao<ErrorStrategy>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_ERROR_STRATEGIES";
	
	@Override
	public void delete(ErrorStrategy item) 
	{
		throw new UnsupportedOperationException(
			"Cannot delete from " + TABLE_NAME + "."
		);
	}

	@Override
	public void save(ErrorStrategy item) 
	{
		throw new UnsupportedOperationException(
			"Cannot insert into " + TABLE_NAME + "."
		);
	}

	@Override
	public void update(ErrorStrategy item) 
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + TABLE_NAME + "."
		);	
	}

	@Override
	public List<ErrorStrategy> loadAll() 
	{
		return jdbcTemplate.query
		(
			"SELECT * FROM " + TABLE_NAME, 
			new ErrorStrategyRowMapper()
		);
	}

	@Override
	public ErrorStrategy load(Long id) 
	{
		String query = 
			"SELECT * FROM " + TABLE_NAME + " " +
			"WHERE id = ?";
		
		Object[] params = 
		{
			id
		};

		List<ErrorStrategy> matchingRows = jdbcTemplate.query(
			query,
			params,
			new ErrorStrategyRowMapper()
		);
		
		if (matchingRows.size() != 1)
		{
			throw new IllegalArgumentException(
				"The requested CR error strategy could not be found."
			);
		}
		
		return matchingRows.get(0);
	}
}
