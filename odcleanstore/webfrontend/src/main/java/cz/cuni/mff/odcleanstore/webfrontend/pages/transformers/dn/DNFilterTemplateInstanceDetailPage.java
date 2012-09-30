package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNFilterTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "PIC" })
public class DNFilterTemplateInstanceDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(DNFilterTemplateInstanceDetailPage.class);
	
	private DNFilterTemplateInstanceDao dnFilterTemplateInstanceDao;

	public DNFilterTemplateInstanceDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Detail", 
			"Show DN rule detail"
		);
		
		// prepare DAO objects
		//
		this.dnFilterTemplateInstanceDao = daoLookupFactory.getDao(DNFilterTemplateInstanceDao.class);
		
		// register page components
		//
		addHelpWindow("dnFilterTemplateInstanceHelpWindow", "openDNFilterTemplateInstanceHelpWindow", new DNFilterTemplateInstanceHelpPanel("content"));
		addReplaceTemplateInstanceInformationSection(ruleId);
	}

	private void addReplaceTemplateInstanceInformationSection(final Integer ruleId)
	{
		DNFilterTemplateInstance instance = dnFilterTemplateInstanceDao.load(ruleId);
		
		setDefaultModel(createModelForOverview(dnFilterTemplateInstanceDao, ruleId));
		
		add(new Label("propertyName"));
		add(new Label("pattern"));
		add(new Label("keep"));
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class, 
				instance.getGroupId(),
				"showDNGroupDetailPage"
			)
		);
	}
}
