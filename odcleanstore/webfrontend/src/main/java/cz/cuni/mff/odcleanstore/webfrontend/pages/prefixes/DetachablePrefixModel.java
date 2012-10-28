package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes.PrefixDao;

/**
 * Loadable-detachable model to store a single prefix entity.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DetachablePrefixModel extends LoadableDetachableModel<Prefix>
{
	private static final long serialVersionUID = 1L;
	
	private PrefixDao dao;
	private Prefix item;
	private String id;
	
	/**
	 * 
	 */
	public DetachablePrefixModel(PrefixDao dao, String id)
	{
		this.dao = dao;
		this.id = id;
	}
	
	/**
	 * 
	 * @param dao
	 * @param item
	 */
	public DetachablePrefixModel(PrefixDao dao, Prefix item)
	{
		this.item = item;
		
		this.dao = dao;
		this.id = item.getPrefix();
	}
	
	@Override
	protected Prefix load() 
	{
		if (item == null)
			item = dao.loadBy("NS_PREFIX", id);
		
		return item;
	}
}
