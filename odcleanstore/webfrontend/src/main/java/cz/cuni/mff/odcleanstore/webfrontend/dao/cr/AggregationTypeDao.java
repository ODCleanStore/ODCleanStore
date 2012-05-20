package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import java.util.List;

public class AggregationTypeDao extends Dao<AggregationType>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_AGGREGATION_TYPES";
	
	@Override
	public void delete(AggregationType item) 
	{
		throw new UnsupportedOperationException(
			"Cannot delete from " + TABLE_NAME + "."
		);
	}

	@Override
	public void save(AggregationType item) 
	{
		throw new UnsupportedOperationException(
			"Cannot insert into " + TABLE_NAME + "."
		);
	}

	@Override
	public void update(AggregationType item) 
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + TABLE_NAME + "."
		);	
	}

	@Override
	public List<AggregationType> loadAll() 
	{
		return jdbcTemplate.query
		(
			"SELECT * FROM " + TABLE_NAME, 
			new AggregationTypeRowMapper()
		);
	}

	@Override
	public AggregationType load(Long id) 
	{
		String query = "SELECT * FROM ? WHERE id = ?";
		
		Object[] params = 
		{
			TABLE_NAME,
			id
		};

		List<AggregationType> matchingRows = jdbcTemplate.query(
			query,
			params,
			new AggregationTypeRowMapper()
		);
		
		if (matchingRows.size() != 0)
		{
			throw new IllegalArgumentException(
				"The requested CR aggregation type could not be found."
			);
		}
		
		return matchingRows.get(0);
	}
}
