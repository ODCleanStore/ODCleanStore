package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class DetachablePrefixModel extends LoadableDetachableModel<Prefix>
{
	private static final long serialVersionUID = 1L;
	
	private Dao<Prefix> dao;
	private String prefix;
	
	/**
	 * 
	 */
	public DetachablePrefixModel(Dao<Prefix> dao, String prefix)
	{
		this.dao = dao;
		this.prefix = prefix;
	}
	
	/**
	 * 
	 * @param dao
	 * @param item
	 */
	public DetachablePrefixModel(Dao<Prefix> dao, Prefix item)
	{
		this(dao, item.getPrefix());
	}
	
	@Override
	protected Prefix load() 
	{
		return dao.loadRawBy("NS_PREFIX", prefix);
	}
}
