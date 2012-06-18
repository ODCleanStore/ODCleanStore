package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;

public class DetachableTransformerInstanceModel extends LoadableDetachableModel<TransformerInstance>
{
	private static final long serialVersionUID = 1L;
	
	private TransformerInstanceDao dao;
	private TransformerInstance item;
	private Long pipelineId;
	private Long transformerId;
	
	/**
	 * 
	 */
	public DetachableTransformerInstanceModel(TransformerInstanceDao dao, Long pipelineId, Long transformerId)
	{
		this.dao = dao;
		this.pipelineId = pipelineId;
		this.transformerId = transformerId;
	}
	
	/**
	 * 
	 * @param dao
	 * @param item
	 */
	public DetachableTransformerInstanceModel(TransformerInstanceDao dao, TransformerInstance item)
	{
		this.item = item;
		
		this.dao = dao;
		this.pipelineId = item.getPipelineId();
		this.transformerId = item.getTransformerId();
	}
	
	@Override
	protected TransformerInstance load() 
	{
		if (item == null)
			item = dao.load(pipelineId, transformerId);
		
		return item;
	}
}
