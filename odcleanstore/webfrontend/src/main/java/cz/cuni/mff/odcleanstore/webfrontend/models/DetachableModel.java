package cz.cuni.mff.odcleanstore.webfrontend.models;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class DetachableModel<BO extends BusinessObject> extends LoadableDetachableModel<BO>
{
	private static final long serialVersionUID = 1L;
	
	private Dao<BO> dao;
	private Long id;
	
	/**
	 * 
	 */
	public DetachableModel(Dao<BO> dao, Long id)
	{
		this.dao = dao;
		this.id = id;
	}
	
	/**
	 * 
	 * @param dao
	 * @param item
	 */
	public DetachableModel(Dao<BO> dao, BO item)
	{
		this(dao, item.getId());
	}
	
	@Override
	protected BO load() 
	{
		return dao.load(id);
	}
}
