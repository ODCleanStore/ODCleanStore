package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

public abstract class DNTemplateInstanceDao<BO extends DNTemplateInstance> extends DaoForAuthorableEntity<BO>
{
	private static final long serialVersionUID = 1L;

	@Override
	public void delete(BO item) throws Exception
	{
		super.delete(item);
		
		// Mark the group as dirty
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(item.getGroupId());
	}
}
