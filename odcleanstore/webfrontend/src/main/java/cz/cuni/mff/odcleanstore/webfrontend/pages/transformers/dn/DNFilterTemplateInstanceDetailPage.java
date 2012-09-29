package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;
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
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNFilterTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.DBOutputHelpPanel;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIRuleHelpPanel;

@AuthorizeInstantiation({ "PIC" })
public class DNFilterTemplateInstanceDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNFilterTemplateInstanceDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<DNFilterTemplateInstance> dnFilterTemplateInstanceDao;

	public DNFilterTemplateInstanceDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Detail", 
			"Show DN rule detail"
		);
		
		// prepare DAO objects
		//
		this.dnFilterTemplateInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNFilterTemplateInstanceDao.class);
		
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
