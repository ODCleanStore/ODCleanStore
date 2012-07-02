package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class TransformerInstanceDataProvider implements IDataProvider<TransformerInstance>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<TransformerInstance> dao;
	private List<TransformerInstance> data;
	private Long pipelineId;
	
	/**
	 * 
	 * @param dao
	 */
	public TransformerInstanceDataProvider(DaoForEntityWithSurrogateKey<TransformerInstance> dao, Long pipelineId)
	{
		this.dao = dao;
		this.pipelineId = pipelineId;
	}
	
	private List<TransformerInstance> getData()
	{
		if (data == null)
			data = dao.loadAllRawBy("pipelineId", pipelineId);
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

	public Iterator<TransformerInstance> iterator(int first, int count) 
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

	public IModel<TransformerInstance> model(TransformerInstance object) 
	{
		return new DetachableModel<TransformerInstance>(dao, object);
	}
}
