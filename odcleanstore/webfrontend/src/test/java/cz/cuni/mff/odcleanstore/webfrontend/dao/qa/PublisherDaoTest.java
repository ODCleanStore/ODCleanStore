package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoTest;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PublisherDaoTest extends DaoTest
{
	private static final String ROOT_RESOURCE_DIR_PATH = "src/test/resources/dao/qa/publisher";
	
	private static final String IMPORT_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/initial_db_import.sql";
	private static final String AFTER_INSERT_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/after_insert_db_export.xml";
	private static final String AFTER_DELETE_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/after_delete_db_export.xml";
	
	private Dao<Publisher> publisherDao;

	public PublisherDaoTest() throws Exception
	{
		super();
		
		DataSource dataSource = (DataSource) ctx.getBean("dataSource");
		
		publisherDao = new PublisherDao();
		publisherDao.setDataSource(dataSource);
	}
	
	@Override
	protected String getInitialImportScriptLocation()
	{
		return IMPORT_SCRIPT_LOCATION;
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}
	
	@Test
	public void testLoadingById() throws URISyntaxException 
	{
		Publisher publisher = publisherDao.load(1L);
		
		assertEquals("http://www.seznam.cz", publisher.getUri());
	}
	
	@Test
	public void testLoadingAll() throws URISyntaxException
	{
		List<Publisher> publishers = publisherDao.loadAll();
		
		assertEquals(3, publishers.size());
		
		assertEquals("http://www.seznam.cz", publishers.get(0).getUri());
		assertEquals("http://www.google.com", publishers.get(1).getUri());
		assertEquals("http://www.isvzus.cz", publishers.get(2).getUri());
	}
	
	@Test
	public void testInserting() throws Exception
	{
		Publisher publisher = new Publisher();
		publisher.setLabel("NYTimes");
		publisher.setUri("http://www.nytimes.com");
		
		publisherDao.save(publisher);

		assertTableContentEquals(
			"PUBLISHERS",
			new File(AFTER_INSERT_SCRIPT_LOCATION)
		);
	}
	
	@Test
	public void testDeleting() throws Exception
	{
		Publisher publisher = new Publisher(2L, "Google", "http://www.google.com");
		
		publisherDao.delete(publisher);

		assertTableContentEquals(
			"PUBLISHERS",
			new File(AFTER_DELETE_SCRIPT_LOCATION)
		);
	}
}
