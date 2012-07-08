package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class AssignedGroupsDataProvider implements IDataProvider<RuleAssignment>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<RuleAssignment> dao;
	private List<RuleAssignment> data;
	private Long transformerInstanceId;
	
	/**
	 * 
	 * @param dao
	 */
	public AssignedGroupsDataProvider(DaoForEntityWithSurrogateKey<RuleAssignment> dao, Long transformerInstanceId)
	{
		this.dao = dao;
		this.transformerInstanceId = transformerInstanceId;
	}
	
	private List<RuleAssignment> getData()
	{
		if (data == null)
			data = dao.loadAllRawBy("transformerInstanceId", transformerInstanceId);
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

	public Iterator<RuleAssignment> iterator(int first, int count) 
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

	public IModel<RuleAssignment> model(RuleAssignment object) 
	{
		return new DetachableModel<RuleAssignment>(dao, object);
	}
}
