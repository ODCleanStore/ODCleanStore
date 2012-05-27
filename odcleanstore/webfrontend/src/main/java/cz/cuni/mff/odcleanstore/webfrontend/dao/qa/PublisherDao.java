package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class PublisherDao extends Dao<Publisher>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "PUBLISHERS";
	
	@Override
	public void delete(Publisher item) 
	{
		String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
		
		Object[] params =
		{
			item.getId()
		};
		
		jdbcTemplate.update(query, params);
	}

	@Override
	public void save(Publisher item) 
	{
		String query = "INSERT INTO " + TABLE_NAME + " (uri) VALUES (?)";
		
		Object[] params =
		{
			item.getUri().toString()
		};
		
		jdbcTemplate.update(query, params);
	}

	@Override
	public void update(Publisher item) 
	{
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public List<Publisher> loadAll() 
	{
		return jdbcTemplate.query
		(
			"SELECT * FROM " + TABLE_NAME,
			new PublisherRowMapper()
		);
	}

	@Override
	public Publisher load(Long id) 
	{
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
		Object[] params = { id };
		
		return (Publisher) jdbcTemplate.queryForObject
		(
			query, 
			params, 
			new PublisherRowMapper()
		);
	}
}
