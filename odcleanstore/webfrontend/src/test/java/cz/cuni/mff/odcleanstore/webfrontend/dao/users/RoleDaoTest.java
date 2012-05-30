package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoTest;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.RoleDao;

public class RoleDaoTest extends DaoTest
{
	private static final String ROOT_RESOURCE_DIR_PATH = "src/test/resources/dao/users/role";
	
	private static final String IMPORT_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/initial_db_import.sql";
	
	private Dao<Role> roleDao;
	
	public RoleDaoTest() throws Exception 
	{
		super();

		DataSource dataSource = (DataSource) ctx.getBean("dataSource");
		
		roleDao = new RoleDao();
		roleDao.setDataSource(dataSource);
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
		Role role = roleDao.load(1L);
		
		assertEquals("SCR", role.getLabel());
		assertEquals("Scrapper", role.getDescription());
	}
	
	@Test
	public void testLoadingAll() throws URISyntaxException
	{
		List<Role> roles = roleDao.loadAll();
		
		assertEquals(4, roles.size());
		
		assertEquals("SCR", roles.get(0).getLabel());
		assertEquals("ONC", roles.get(1).getLabel());
		assertEquals("POC", roles.get(2).getLabel());
		assertEquals("ADM", roles.get(3).getLabel());
	}
}
