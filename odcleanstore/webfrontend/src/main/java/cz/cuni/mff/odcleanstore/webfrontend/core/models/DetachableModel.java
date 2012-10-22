package cz.cuni.mff.odcleanstore.webfrontend.core.models;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoSortableDataProvidable;

/**
 * A BO encapsulated into a loadable detachable model.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <BO>
 */
public class DetachableModel<BO extends EntityWithSurrogateKey> extends LoadableDetachableModel<BO>
{
	private static final long serialVersionUID = 1L;
	
	private DaoSortableDataProvidable<BO> dao;
	private BO item;
	private Integer id;
	
	/**
	 * 
	 * @param dao
	 * @param id
	 */
	public DetachableModel(DaoSortableDataProvidable<BO> dao, Integer id)
	{
		this.dao = dao;
		this.id = id;
	}
	
	/**
	 * 
	 * @param dao
	 * @param item
	 */
	public DetachableModel(DaoSortableDataProvidable<BO> dao, BO item)
	{
		this.item = item;
		
		this.dao = dao;
		this.id = item.getId();
	}
	
	@Override
	protected BO load() 
	{
		if (item == null)
			item = dao.load(id);
		
		return item;
	}
}
