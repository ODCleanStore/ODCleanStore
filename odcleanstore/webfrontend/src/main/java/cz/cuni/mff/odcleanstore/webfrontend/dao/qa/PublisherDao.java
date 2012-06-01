package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class PublisherDao extends Dao<Publisher>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "PUBLISHERS";
	
	private ParameterizedRowMapper<Publisher> rowMapper;
	
	public PublisherDao()
	{
		this.rowMapper = new PublisherRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<Publisher> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(Publisher item) 
	{
		String query = "INSERT INTO " + TABLE_NAME + " (label, uri) VALUES (?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getUri()
		};
		
		jdbcTemplate.update(query, params);
	}
	
	@Override
	public void delete(Publisher item) 
	{
		deleteRaw(item.getId());
		
		// TODO: delete all related rule restrictions
	}

	@Override
	public List<Publisher> loadAll() 
	{
		return loadAllRaw();
	}

	@Override
	public Publisher load(Long id) 
	{
		return loadRaw(id);
	}
}
