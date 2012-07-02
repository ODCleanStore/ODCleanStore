package cz.cuni.mff.odcleanstore.webfrontend.dao;

import static org.junit.Assert.*;

import org.junit.Test;

public class QueryCriteriaTest 
{
	@Test
	public void testJoiningSimpleCriteria() 
	{
		QueryCriteria criteria = createSimpleCriteria();
		assertEquals("count = ?", criteria.joinToString());
	}

	@Test 
	public void testSimpleCriteriaRange()
	{
		QueryCriteria criteria = createSimpleCriteria();
		Object[] range = { new Integer(1) };
		
		assertArrayEquals(range, criteria.getRange());
	}
	
	@Test
	public void testJoiningComplexCriteria() 
	{
		QueryCriteria criteria = createComplexCriteria();
		assertEquals("size = ? AND width = ? AND height = ?", criteria.joinToString());
	}
	
	@Test
	public void testComplexCriteriaRange()
	{
		QueryCriteria criteria = createComplexCriteria();
		Object[] range = { new Integer(5), new Integer(10), new Integer(15) };
		
		assertArrayEquals(range, criteria.getRange());
	}
	
	private QueryCriteria createSimpleCriteria()
	{
		QueryCriteria criteria = new QueryCriteria();
		criteria.addCriterion("count", new Integer(1));
		return criteria;
	}
	
	private QueryCriteria createComplexCriteria()
	{
		QueryCriteria criteria = new QueryCriteria();
		
		criteria.addCriterion("size", new Integer(5));
		criteria.addCriterion("width", new Integer(10));
		criteria.addCriterion("height", new Integer(15));
		
		return criteria;
	}
}
