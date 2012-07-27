package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DNRuleDataProvider implements IDataProvider<DNRule>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<DNRule> dao;
	private List<DNRule> data;
	private Long groupId;
	
	/**
	 * 
	 * @param dao
	 */
	public DNRuleDataProvider(DaoForEntityWithSurrogateKey<DNRule> dao, Long groupId)
	{
		this.dao = dao;
		this.groupId = groupId;
	}
	
	private List<DNRule> getData()
	{
		if (data == null)
			data = dao.loadAllRawBy("groupId", groupId);
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

	public Iterator<DNRule> iterator(int first, int count) 
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

	public IModel<DNRule> model(DNRule object) 
	{
		return new DetachableModel<DNRule>(dao, object);
	}
}
