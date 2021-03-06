package cz.cuni.mff.odcleanstore.webfrontend.systest.dao.users;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.RoleDao;
import cz.cuni.mff.odcleanstore.webfrontend.systest.dao.DaoTest;

public class RoleDaoTest extends DaoTest
{
	private static final String ROOT_RESOURCE_DIR_PATH = "src/test/resources/dao/users/role";
	
	private static final String IMPORT_SCRIPT_LOCATION = ROOT_RESOURCE_DIR_PATH + "/initial_db_import.sql";
	
	private DaoForEntityWithSurrogateKey<Role> roleDao;
	
	public RoleDaoTest() throws Exception 
	{
		super();

		DataSource dataSource = (DataSource) ctx.getBean("dataSource");
		
		roleDao = new RoleDao();
		// TODO: roleDao.setDataSource(dataSource);
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
		Role role = roleDao.load(1);
		
		assertEquals(Role.SCR, role.getLabel());
		assertEquals("Scraper", role.getDescription());
	}
	
	@Test
	public void testLoadingAll() throws URISyntaxException
	{
		List<Role> roles = roleDao.loadAll();
		
		assertEquals(4, roles.size());
		
		assertEquals(Role.SCR, roles.get(0).getLabel());
		assertEquals(Role.ONC, roles.get(1).getLabel());
		assertEquals(Role.PIC, roles.get(2).getLabel());
		assertEquals(Role.ADM, roles.get(3).getLabel());
	}
}
