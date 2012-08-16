package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.util.Pair;

/**
 * 
 * @author Dusan
 *
 */
public class QueryCriteria 
{
	public enum SortOrder { ASC, DESC };
	
	private List<Pair<String, Object>> whereClauses;
	private List<Pair<String, SortOrder>> orderByClauses;
	
	/**
	 * 
	 */
	public QueryCriteria()
	{
		this.whereClauses = new LinkedList<Pair<String, Object>>();
		this.orderByClauses = new LinkedList<Pair<String, SortOrder>>();
	}
	
	/**
	 * 
	 * @param column
	 * @param value
	 */
	public void addWhereClause(String column, Object value)
	{
		this.whereClauses.add(new Pair<String, Object>(column, value));
	}
	
	/**
	 * 
	 * @param column
	 */
	public void addOrderByClause(String column, SortOrder order)
	{
		this.orderByClauses.add(new Pair<String, SortOrder>(column, order));
	}
	
	/**
	 * 
	 * @return
	 */
	public String buildWhereClause()
	{
		StringBuilder builder = new StringBuilder();

		String columnName = whereClauses.get(0).getFirst();
		builder.append(columnName + " = ?");
		
		for (int i = 1; i < whereClauses.size(); i++)
		{
			columnName = whereClauses.get(i).getFirst();
			builder.append(" AND " + columnName + " = ?");
		}
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public Object[] buildWhereClauseParams()
	{
		List<Object> result = new LinkedList<Object>();
		
		for (Pair<String, Object> criterion : whereClauses)
			result.add(criterion.getSecond());
		
		return result.toArray();
	}
	
	/**
	 * 
	 * @return
	 */
	public String buildOrderByClause()
	{
		StringBuilder builder = new StringBuilder();

		String columnName = orderByClauses.get(0).getFirst();
		String sortOrder = orderByClauses.get(0).getSecond().toString();
		builder.append(columnName + " " + sortOrder);
		
		for (int i = 1; i < orderByClauses.size(); i++)
		{
			columnName = orderByClauses.get(i).getFirst();
			sortOrder = orderByClauses.get(i).getSecond().toString();
			
			builder.append(", " + columnName + " " + sortOrder);
		}
		
		return builder.toString();
	}
}
