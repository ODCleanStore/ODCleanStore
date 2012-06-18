package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class DetachablePrefixModel extends LoadableDetachableModel<Prefix>
{
	private static final long serialVersionUID = 1L;
	
	private Dao<Prefix> dao;
	private Prefix item;
	private String id;
	
	/**
	 * 
	 */
	public DetachablePrefixModel(Dao<Prefix> dao, String id)
	{
		this.dao = dao;
		this.id = id;
	}
	
	/**
	 * 
	 * @param dao
	 * @param item
	 */
	public DetachablePrefixModel(Dao<Prefix> dao, Prefix item)
	{
		this.item = item;
		
		this.dao = dao;
		this.id = item.getPrefix();
	}
	
	@Override
	protected Prefix load() 
	{
		if (item == null)
			item = dao.loadRawBy("NS_PREFIX", id);
		
		return item;
	}
}
