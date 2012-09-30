package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "PIC" })
public class DNRenameTemplateInstanceDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(DNRenameTemplateInstanceDetailPage.class);
	
	private DNRenameTemplateInstanceDao dnRenameTemplateInstanceDao;

	public DNRenameTemplateInstanceDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Detail", 
			"Show DN rule detail"
		);
		
		// prepare DAO objects
		//
		this.dnRenameTemplateInstanceDao = daoLookupFactory.getDao(DNRenameTemplateInstanceDao.class);
		
		// register page components
		//
		addHelpWindow("dnRenameTemplateInstanceHelpWindow", "openDNRenameTemplateInstanceHelpWindow", new DNRenameTemplateInstanceHelpPanel("content"));
		addReplaceTemplateInstanceInformationSection(ruleId);
	}

	private void addReplaceTemplateInstanceInformationSection(final Integer ruleId)
	{
		DNRenameTemplateInstance instance = dnRenameTemplateInstanceDao.load(ruleId);
		
		setDefaultModel(createModelForOverview(dnRenameTemplateInstanceDao, ruleId));
		
		add(new Label("sourcePropertyName"));
		add(new Label("targetPropertyName"));
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class, 
				instance.getGroupId(),
				"showDNGroupDetailPage"
			)
		);
	}
}
