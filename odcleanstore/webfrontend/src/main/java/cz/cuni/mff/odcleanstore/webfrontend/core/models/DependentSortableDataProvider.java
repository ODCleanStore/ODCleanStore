package cz.cuni.mff.odcleanstore.webfrontend.core.models;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class DependentSortableDataProvider<BO extends EntityWithSurrogateKey> extends SortableDataProvider<BO>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<BO> dao;
	private List<BO> data;
	private String columnName;
	private Object value;
	
	/**
	 * 
	 * @param dao
	 */
	public DependentSortableDataProvider(DaoForEntityWithSurrogateKey<BO> dao, String columnName, Object value)
	{
		this(dao, "id", columnName, value);
	}
	
	/**
	 * 
	 * @param dao
	 * @param defaultSortColumnName
	 * @param columnName
	 * @param value
	 */
	public DependentSortableDataProvider(DaoForEntityWithSurrogateKey<BO> dao, String defaultSortColumnName,
		String columnName, Object value)
	{
		setSort(defaultSortColumnName, SortOrder.ASCENDING);
		
		this.dao = dao;
		this.columnName = columnName;
		this.value = value;
	}
	
	private List<BO> getData()
	{
		if (data == null)
		{
			SortParam sortParam = getSort();
			
			QueryCriteria criteria = new QueryCriteria();
			criteria.addWhereClause(columnName, value);
			criteria.addOrderByClause(sortParam.getProperty(), sortParam.isAscending());
			
			data = dao.loadAllBy(criteria);
		}
		
		return data;
	}
	
	@Override
	public void detach()
	{
		data = null;
	}
	
	public Iterator<? extends BO> iterator(int first, int count) 
	{
		// TODO: consider if it would be viable to fetch only the required data from DB here, instead of creating
		// sublists
		
		return 
			getData()
				.subList(first, first + count)
					.iterator();
	}

	public int size() 
	{
		return getData().size();
	}

	public IModel<BO> model(BO object) 
	{
		return new DetachableModel<BO>(dao, object);
	}
}
