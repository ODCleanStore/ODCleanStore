package cz.cuni.mff.odcleanstore.webfrontend.systest.dao.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.systest.dao.DaoTest;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransformerDaoTest extends DaoTest
{
	private static final String ROOT_RESOURCE_DIR_PATH = "src/test/resources/dao/en/transformer";
	
	private static final String IMPORT_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/initial_db_import.sql";
	private static final String AFTER_INSERT_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/after_insert_db_export.xml";
	private static final String AFTER_DELETE_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/after_delete_db_export.xml";
	
	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;

	public TransformerDaoTest() throws Exception
	{
		super();
		
		DataSource dataSource = (DataSource) ctx.getBean("dataSource");
		
		transformerDao = new TransformerDao();
		// TODO: transformerDao.setDataSource(dataSource);
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
		Transformer transformer = transformerDao.load(1);
		
		assertTrue(1 == transformer.getId());
		assertEquals("QA", transformer.getLabel());
		assertEquals("Standard quality assessment transformer", transformer.getDescription());
		assertEquals(".", transformer.getJarPath());
		
		assertEquals(
			"cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl", 
			transformer.getFullClassName()
		);
	}
	
	@Test
	public void testLoadingAll() throws URISyntaxException
	{
		List<Transformer> transformers = transformerDao.loadAll();
		
		assertEquals(2, transformers.size());
		
		assertEquals("QA", transformers.get(0).getLabel());
		assertEquals("Linker", transformers.get(1).getLabel());
	}
	
	@Test
	public void testInserting() throws Exception
	{
		/*
		Publisher publisher = new Publisher();
		publisher.setLabel("NYTimes");
		publisher.setUri("http://www.nytimes.com");
		
		publisherDao.save(publisher);

		assertTableContentEquals(
			"PUBLISHERS",
			new File(AFTER_INSERT_SCRIPT_LOCATION)
		);
		*/
	}
	
	@Test
	public void testDeleting() throws Exception
	{
		/*
		Publisher publisher = new Publisher(2, "Google", "http://www.google.com");
		
		publisherDao.delete(publisher);

		assertTableContentEquals(
			"PUBLISHERS",
			new File(AFTER_DELETE_SCRIPT_LOCATION)
		);
		*/
	}
}
