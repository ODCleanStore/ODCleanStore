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
	private List<Pair<String, Object>> criteria;
	
	/**
	 * 
	 */
	public QueryCriteria()
	{
		this.criteria = new LinkedList<Pair<String, Object>>();
	}
	
	/**
	 * 
	 * @param column
	 * @param value
	 */
	public void addCriterion(String column, Object value)
	{
		this.criteria.add(new Pair<String, Object>(column, value));
	}
	
	/**
	 * 
	 * @param criteria
	 * @return
	 */
	public String joinToString()
	{
		assert criteria != null && criteria.size() >= 1;
		
		StringBuilder builder = new StringBuilder();

		String columnName = criteria.get(0).getFirst();
		builder.append(columnName + " = ?");
		
		for (int i = 1; i < criteria.size(); i++)
		{
			columnName = criteria.get(i).getFirst();
			builder.append(" AND " + columnName + " = ?");
		}
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @param criteria
	 * @return
	 */
	public Object[] getRange()
	{
		List<Object> result = new LinkedList<Object>();
		
		for (Pair<String, Object> criterion : criteria)
			result.add(criterion.getSecond());
		
		return result.toArray();
	}
}
