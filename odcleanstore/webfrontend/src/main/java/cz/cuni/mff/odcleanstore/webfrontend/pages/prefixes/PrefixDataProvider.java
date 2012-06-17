package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
		// return new DetachablePrefixModel(dao, object);
		return new Model<Prefix>(object);
	}
}
