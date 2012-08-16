package cz.cuni.mff.odcleanstore.webfrontend.dao;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria.SortOrder;

public class QueryCriteriaTest 
{
	@Test
	public void testBuildingSimpleWhereClause() 
	{
		QueryCriteria criteria = new QueryCriteria();
		criteria.addWhereClause("count", new Integer(1));
		
		assertEquals("count = ?", criteria.buildWhereClause());
		
		Object[] range = { new Integer(1) };
		assertArrayEquals(range, criteria.buildWhereClauseParams());
	}

	@Test
	public void testBuildingComplexWhereClause() 
	{
		QueryCriteria criteria = new QueryCriteria();
		criteria.addWhereClause("size", new Integer(5));
		criteria.addWhereClause("width", new Integer(10));
		criteria.addWhereClause("height", new Integer(15));
		
		assertEquals("size = ? AND width = ? AND height = ?", criteria.buildWhereClause());

		Object[] range = { new Integer(5), new Integer(10), new Integer(15) };
		assertArrayEquals(range, criteria.buildWhereClauseParams());
	}
	
	@Test
	public void testBuildingSimpleOrderByClause()
	{
		QueryCriteria criteria = new QueryCriteria();
		criteria.addOrderByClause("size", SortOrder.ASC);
		
		assertEquals("size ASC", criteria.buildOrderByClause());
	}
	
	@Test
	public void testBuildingComplexOrderByClause()
	{
		QueryCriteria criteria = new QueryCriteria();
		criteria.addOrderByClause("size", SortOrder.ASC);
		criteria.addOrderByClause("width", SortOrder.ASC);
		criteria.addOrderByClause("height", SortOrder.DESC);
		
		assertEquals("size ASC, width ASC, height DESC", criteria.buildOrderByClause());
	}
}
