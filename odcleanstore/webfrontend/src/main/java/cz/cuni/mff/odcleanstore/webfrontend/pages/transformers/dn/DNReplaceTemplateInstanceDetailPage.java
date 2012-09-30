package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "PIC" })
public class DNReplaceTemplateInstanceDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(DNReplaceTemplateInstanceDetailPage.class);
	
	private DNReplaceTemplateInstanceDao dnReplaceTemplateInstanceDao;

	public DNReplaceTemplateInstanceDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Detail", 
			"Show DN rule detail"
		);
		
		// prepare DAO objects
		//
		this.dnReplaceTemplateInstanceDao = daoLookupFactory.getDao(DNReplaceTemplateInstanceDao.class);
		
		// register page components
		//
		addHelpWindow("dnReplaceTemplateInstanceHelpWindow", "openDNReplaceTemplateInstanceHelpWindow", new DNReplaceTemplateInstanceHelpPanel("content"));
		addReplaceTemplateInstanceInformationSection(ruleId);
	}

	private void addReplaceTemplateInstanceInformationSection(final Integer ruleId)
	{
		DNReplaceTemplateInstance instance = dnReplaceTemplateInstanceDao.load(ruleId);
		
		setDefaultModel(createModelForOverview(dnReplaceTemplateInstanceDao, ruleId));
		
		add(new Label("propertyName"));
		add(new Label("pattern"));
		add(new Label("replacement"));
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class, 
				instance.getGroupId(),
				"showDNGroupDetailPage"
			)
		);
	}
}
