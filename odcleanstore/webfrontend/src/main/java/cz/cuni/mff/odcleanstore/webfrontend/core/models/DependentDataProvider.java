package cz.cuni.mff.odcleanstore.webfrontend.core.models;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DependentDataProvider<BO extends EntityWithSurrogateKey> implements IDataProvider<BO>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<BO> dao;
	private List<BO> data;
	private String columnName;
	private Object value;
	
	/**
	 * 
	 * @param dao
	 */
	public DependentDataProvider(DaoForEntityWithSurrogateKey<BO> dao, String columnName, Object value)
	{
		this.dao = dao;
		this.columnName = columnName;
		this.value = value;
	}
	
	private List<BO> getData()
	{
		if (data == null)
			data = dao.loadAllBy(columnName, value);
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

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

	public int size() 
	{
		return getData().size();
	}

	public IModel<BO> model(BO object) 
	{
		return new DetachableModel<BO>(dao, object);
	}
}
