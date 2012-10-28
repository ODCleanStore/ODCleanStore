package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;
import cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes.PrefixDao;

/**
 * Sortable-data-provider for prefix entities.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class SortablePrefixDataProvider extends SortableDataProvider<Prefix>
{
	private static final long serialVersionUID = 1L;

	private PrefixDao dao;
	private List<Prefix> data;
	
	/**
	 * 
	 * @param dao
	 */
	public SortablePrefixDataProvider(PrefixDao dao)
	{
		this(dao, "NS_PREFIX");
	}
	
	/**
	 * 
	 * @param dao
	 * @param defaultSortColumnName
	 */
	public SortablePrefixDataProvider(PrefixDao dao, String defaultSortColumnName)
	{
		setSort(defaultSortColumnName, SortOrder.ASCENDING);
		
		this.dao = dao;
	}
	
	/**
	 * Loads the represented collection of URI prefixes into memory (lazily).
	 * 
	 * @return
	 */
	private List<Prefix> getData()
	{
		if (data == null)
		{
			SortParam sortParam = getSort();
			
			QueryCriteria criteria = new QueryCriteria();
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
	
	/**
	 * Returns an iterator over the represented collection of URI prefixes.
	 * 
	 * @return
	 */
	public Iterator<Prefix> iterator(int first, int count) 
	{
		// TODO: consider if it would be viable to fetch only the required data from DB here, instead of creating
		// sublists
		
		return 
			getData()
				.subList(first, first + count)
					.iterator();
	}

	/**
	 * Returns the size of the represented collection of URI prefixes.
	 * 
	 * @return
	 */
	public int size() 
	{
		return getData().size();
	}

	/**
	 * Returns the given URI prefix encapsulated into a loadable-detachable
	 * model.
	 * 
	 * @return
	 */
	public IModel<Prefix> model(Prefix object) 
	{
		return new DetachablePrefixModel(dao, object);
	}
}
