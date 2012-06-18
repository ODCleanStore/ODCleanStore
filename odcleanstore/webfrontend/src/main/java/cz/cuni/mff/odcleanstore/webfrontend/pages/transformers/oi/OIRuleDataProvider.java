package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.models.DetachableModel;

public class OIRuleDataProvider implements IDataProvider<OIRule>
{
	private static final long serialVersionUID = 1L;
	
	private Dao<OIRule> dao;
	private List<OIRule> data;
	private Long groupId;
	
	/**
	 * 
	 * @param dao
	 */
	public OIRuleDataProvider(Dao<OIRule> dao, Long groupId)
	{
		this.dao = dao;
		this.groupId = groupId;
	}
	
	private List<OIRule> getData()
	{
		if (data == null)
			data = dao.loadAllRawBy("groupId", groupId);
		
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

	public IModel<OIRule> model(OIRule object) 
	{
		return new DetachableModel<OIRule>(dao, object);
	}
}
