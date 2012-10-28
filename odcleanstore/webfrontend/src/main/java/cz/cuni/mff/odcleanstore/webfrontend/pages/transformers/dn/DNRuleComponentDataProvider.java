package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;

/**
 * Data-provider to obtain registered dn-rule-components which belong to
 * the dn-rule given by it's id.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class DNRuleComponentDataProvider implements IDataProvider<DNRuleComponent>
{
	private static final long serialVersionUID = 1L;
	
	private DNRuleComponentDao dao;
	private List<DNRuleComponent> data;
	private Integer ruleId;
	
	/**
	 * 
	 * @param dao
	 */
	public DNRuleComponentDataProvider(DNRuleComponentDao dao, Integer ruleId)
	{
		this.dao = dao;
		this.ruleId = ruleId;
	}
	
	/**
	 * Lazily load the represented collection.
	 * 
	 * @return
	 */
	private List<DNRuleComponent> getData()
	{
		if (data == null)
			data = dao.loadAllBy("ruleId", ruleId);
		
		return data;
	}
	
	/**
	 * Free allocated memory.
	 */
	public void detach() 
	{
		data = null;
	}

	/**
	 * Returns an iterator over the represented collection.
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
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

	/**
	 * Returns the size of the represented collection.
	 * 
	 * @return
	 */
	public int size() 
	{
		return getData().size();
	}

	/**
	 * Returns the given component instance encapsulated into 
	 * a loadable-detachable model.
	 * 
	 * @param object
	 * @return
	 */
	public IModel<DNRuleComponent> model(DNRuleComponent object) 
	{
		return new DetachableModel<DNRuleComponent>(dao, object);
	}
}
