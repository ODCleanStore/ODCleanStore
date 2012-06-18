package cz.cuni.mff.odcleanstore.webfrontend.systest.dao.qa;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.systest.dao.DaoTest;

public class QARuleDaoTest extends DaoTest
{
	private static final String ROOT_RESOURCE_DIR_PATH = "src/test/resources/dao/qa/rule";
	
	private static final String IMPORT_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/initial_db_import.sql";
	
	private Dao<QARule> ruleDao;

	public QARuleDaoTest() throws Exception
	{
		super();
		
		DataSource dataSource = (DataSource) ctx.getBean("dataSource");
		
		ruleDao = new QARuleDao();
		// TODO: ruleDao.setDataSource(dataSource);
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
		QARule rule = ruleDao.load(1L);
		
		assertEquals("Rule no. 1", rule.getDescription());
		assertEquals("Filter for rule no. 1", rule.getFilter());
		assertEquals(0.1, rule.getCoefficient(), 0.01);
	}
	
	@Test
	public void testLoadingAll() throws URISyntaxException
	{
		List<QARule> publishers = ruleDao.loadAll();
		
		assertEquals(2, publishers.size());
		
		assertEquals("Rule no. 1", publishers.get(0).getDescription());
		assertEquals("Rule no. 2", publishers.get(1).getDescription());
	}
}
