package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

public class RoleDaoTest 
{
	private static Role[] rolePatterns = 
	{
		new Role(1, "SCR", "Scrapper"),
		new Role(2, "ONC", "Ontology creator"),
		new Role(3, "POC", "Policy creator"),
		new Role(4, "ADM", "Administrator")	
	};
	
	private ApplicationContext ctx;
	private RoleDao roleDao;
	
	@Before
	public void setUp()
	{
		ctx = new ClassPathXmlApplicationContext("./config/spring.xml");
		roleDao = (RoleDao) ctx.getBean("roleDao");
	}
	
	@Test
	public void testLoadOperation()
	{
		for (Role rolePattern : rolePatterns)
		{
			Role role = roleDao.load(rolePattern.getId());
			assertEquals(rolePattern, role);
		}
	}
	
	@Test
	public void testLoadAllOperation()
	{
		List<Role> rolePatternsList = Arrays.asList(rolePatterns);
		List<Role> roles = roleDao.loadAll();
		
		assertEquals(rolePatternsList, roles);
	}
}
