package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;

public class AssignedInstancesModel extends LoadableDetachableModel<List<TransformerInstance>> 
{
	private static final long serialVersionUID = 1L;

	private Integer pipelineId;
	private TransformerInstanceDao dao;
	private Integer skippedInstanceId;
	
	public AssignedInstancesModel(Integer pipelineId, TransformerInstanceDao dao, Integer skippedInstanceId) 
	{
		this.pipelineId = pipelineId;
		this.dao = dao;
		this.skippedInstanceId = skippedInstanceId;
	}
	
	@Override
	protected List<TransformerInstance> load()
	{
		List<TransformerInstance> list = dao.loadAllBy("pipelineId", pipelineId);
		if (skippedInstanceId == null)
		{
			return list;
		}
		else
		{
			Iterator<TransformerInstance> it = list.iterator();
			while (it.hasNext())
			{
				TransformerInstance inst = it.next();
				if (inst.getId().equals(skippedInstanceId))
				{
					it.remove();
					break;
				}
			}
			return list;
		}
	}
};