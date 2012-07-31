package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class PrefixDataProvider implements IDataProvider<Prefix>
{
	private static final long serialVersionUID = 1L;
	
	private Dao<Prefix> dao;
	private List<Prefix> data;
	
	/**
	 * 
	 * @param dao
	 */
	public PrefixDataProvider(Dao<Prefix> dao)
	{
		this.dao = dao;
	}
	
	private List<Prefix> getData()
	{
		if (data == null)
			data = dao.loadAll();
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

	public Iterator iterator(int first, int count) 
	{
		// replace this with a special DAO method to only select the sub-list
		// from the database call if necessary (instead of selecting all and 
		// trimming)
		return 
			getData()
				.subList(first, first + count)
					.iterator();
	}

	public int size() 
	{
		return getData().size();
	}

	public IModel<Prefix> model(Prefix object) 
	{
		return new DetachablePrefixModel(dao, object);
	}
}
