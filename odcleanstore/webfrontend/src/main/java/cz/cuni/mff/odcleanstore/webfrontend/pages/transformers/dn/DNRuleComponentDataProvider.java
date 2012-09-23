package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

@AuthorizeInstantiation({ Role.PIC })
public class DNRuleComponentDataProvider implements IDataProvider<DNRuleComponent>
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<DNRuleComponent> dao;
	private List<DNRuleComponent> data;
	private Long ruleId;
	
	/**
	 * 
	 * @param dao
	 */
	public DNRuleComponentDataProvider(DaoForEntityWithSurrogateKey<DNRuleComponent> dao, Long ruleId)
	{
		this.dao = dao;
		this.ruleId = ruleId;
	}
	
	private List<DNRuleComponent> getData()
	{
		if (data == null)
			data = dao.loadAllRawBy("ruleId", ruleId);
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

	public Iterator<DNRuleComponent> iterator(int first, int count) 
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

	public IModel<DNRuleComponent> model(DNRuleComponent object) 
	{
		return new DetachableModel<DNRuleComponent>(dao, object);
	}
}
