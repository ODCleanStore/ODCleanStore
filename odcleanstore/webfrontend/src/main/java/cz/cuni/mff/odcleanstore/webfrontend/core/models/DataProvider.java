package cz.cuni.mff.odcleanstore.webfrontend.core.models;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class DataProvider<BO extends BusinessObject> implements IDataProvider<BO>
{
	private static final long serialVersionUID = 1L;
	
	private Dao<BO> dao;
	private List<BO> data;
	
	/**
	 * 
	 * @param dao
	 */
	public DataProvider(Dao<BO> dao)
	{
		this.dao = dao;
	}
	
	private List<BO> getData()
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

	public IModel<BO> model(BO object) 
	{
		return new DetachableModel<BO>(dao, object);
	}
}
