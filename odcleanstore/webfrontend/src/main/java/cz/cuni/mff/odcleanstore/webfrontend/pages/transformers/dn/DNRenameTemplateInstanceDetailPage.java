package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.DBOutputHelpPanel;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIRuleHelpPanel;

@AuthorizeInstantiation({ "PIC" })
public class DNRenameTemplateInstanceDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNRenameTemplateInstanceDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<DNRenameTemplateInstance> dnRenameTemplateInstanceDao;

	public DNRenameTemplateInstanceDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Detail", 
			"Show DN rule detail"
		);
		
		// prepare DAO objects
		//
		this.dnRenameTemplateInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRenameTemplateInstanceDao.class);
		
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
