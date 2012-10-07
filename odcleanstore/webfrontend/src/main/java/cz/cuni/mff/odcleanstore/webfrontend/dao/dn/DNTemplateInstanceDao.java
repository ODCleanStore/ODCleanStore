package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

public abstract class DNTemplateInstanceDao<BO extends DNTemplateInstance> extends DaoForAuthorableEntity<BO>
{
	@Override
	public void delete(BO item) throws Exception
	{
		super.delete(item);
		
		// Mark the group as dirty
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(item.getGroupId());
	}
}
