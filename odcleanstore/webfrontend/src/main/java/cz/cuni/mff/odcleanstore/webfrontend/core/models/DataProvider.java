package cz.cuni.mff.odcleanstore.webfrontend.core.models;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

/**
 * A simple data provider to provide a collection of all registered
 * instances of the given BO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <BO>
 */
public class DataProvider<BO extends EntityWithSurrogateKey> implements IDataProvider<BO>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<BO> dao;
	private List<BO> data;
	
	/**
	 * 
	 * @param dao
	 */
	public DataProvider(DaoForEntityWithSurrogateKey<BO> dao)
	{
		this.dao = dao;
	}
	
	/**
	 * Loads all registered instances of the given BO in a lazy way.
	 * 
	 * @return
	 */
	private List<BO> getData()
	{
		if (data == null)
			data = dao.loadAll();
		
		return data;
	}

	/**
	 * Drops references to the allocated data structures.
	 * 
	 */
	public void detach() 
	{
		data = null;
	}

	/**
	 * Returns an iterator over the sub-collection of the represented
	 * data starting at the first-th entity and ending at the 
	 * (first+count)-th entity.
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
	public Iterator<BO> iterator(int first, int count) 
	{
		// replace this with a special DAO method to only select the sub-list
		// from the database call if necessary (instead of selecting all and 
		// trimming)
		return 
			getData()
				.subList(first, first + count)
					.iterator();
	}

	/**
	 * Returns the size of the represented collection.
	 * 
	 * @return
	 */
	public int size() 
	{
		return getData().size();
	}

	/**
	 * Returns the given BO encapsulated into a lodable-detachable
	 * model.
	 * 
	 * @param object
	 */
	public IModel<BO> model(BO object) 
	{
		return new DetachableModel<BO>(dao, object);
	}
}
