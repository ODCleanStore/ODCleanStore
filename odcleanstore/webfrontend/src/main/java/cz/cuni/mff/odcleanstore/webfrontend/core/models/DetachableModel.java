package cz.cuni.mff.odcleanstore.webfrontend.core.models;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DetachableModel<BO extends EntityWithSurrogateKey> extends LoadableDetachableModel<BO>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<BO> dao;
	private BO item;
	private Long id;
	
	/**
	 * 
	 */
	public DetachableModel(DaoForEntityWithSurrogateKey<BO> dao, Long id)
	{
		this.dao = dao;
		this.id = id;
	}
	
	/**
	 * 
	 * @param dao
	 * @param item
	 */
	public DetachableModel(DaoForEntityWithSurrogateKey<BO> dao, BO item)
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
