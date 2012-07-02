package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class OIOutputDataProvider implements IDataProvider<OIOutput>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<OIOutput> dao;
	private List<OIOutput> data;
	private Long ruleId;
	private Long typeId;
	
	/**
	 * 
	 * @param dao
	 */
	public OIOutputDataProvider(DaoForEntityWithSurrogateKey<OIOutput> dao, Long ruleId, Long typeId)
	{
		this.dao = dao;
		this.ruleId = ruleId;
		this.typeId = typeId;
	}
	
	private List<OIOutput> getData()
	{
		if (data == null)
		{
			QueryCriteria criteria = new QueryCriteria();
			
			criteria.addCriterion("ruleId", ruleId);
			criteria.addCriterion("outputTypeId", typeId);
			
			data = dao.loadAllBy(criteria);
		}
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

	public Iterator<OIOutput> iterator(int first, int count) 
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

	public IModel<OIOutput> model(OIOutput object) 
	{
		return new DetachableModel<OIOutput>(dao, object);
	}
}
