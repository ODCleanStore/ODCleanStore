package cz.cuni.mff.odcleanstore.webfrontend.core;

import org.apache.wicket.protocol.http.WebApplication;

import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.myaccount.EditPasswordPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.myaccount.MyAccountPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies.EditOntologyPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies.NewOntologyPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies.OntologiesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies.OntologyDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.AggregationSettingsPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.EditPropertyPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.NewPropertyPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.NewQELabelPropertyPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.QELabelPropertiesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.EditPipelinePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.EditTransformerAssignmentPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.NewGroupAssignmentPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.NewPipelinePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.NewTransformerAssignmentPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.PipelinesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes.NewPrefixPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes.PrefixesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.EditTransformerPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.NewTransformerPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.TransformersListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.DNGroupsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.DNRuleComponentDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.DNRuleDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.EditDNGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.EditDNRuleComponentPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.EditDNRulePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.NewDNGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.NewDNRuleComponentPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.NewDNRulePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.EditOIGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.EditOIRulePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.NewDBOutputPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.NewFileOutputPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.NewOIGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.NewOIRulePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIGroupsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIRuleDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.EditQAGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.EditQARulePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.NewQAGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.NewQARulePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QAGroupsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.AccountsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.EditAccountPermissionsPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.NewAccountPage;

/**
 * 
 * @author Dusan
 *
 */
public class URLRouter 
{
	private String webUrlPrefix;
	
	/**
	 * 
	 * @param webUrlPrefix
	 */
	public URLRouter(String webUrlPrefix)
	{
		this.webUrlPrefix = webUrlPrefix;
	}
	
	/**
	 * 
	 * @param app
	 */
	public void setupRouting(WebApplication app)
	{
		setupLoginRoutes(app);
		setupUserAccountsRoutes(app);
		setupBackendRoutes(app);
		setupOutputWSRoutes(app);
		setupURLPrefixesRoutes(app);
		setupOntologiesRoutes(app);
		setupMyAccountRoutes(app);
	}

	// 
	// ------------------------------------------------------------------------
	// LOGIN ROUTES
	// ------------------------------------------------------------------------
	//
	
	private void setupLoginRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/login", LogInPage.class);
	}
	
	// 
	// ------------------------------------------------------------------------
	// USER ACCOUNTS ROUTES
	// ------------------------------------------------------------------------
	//
	
	private void setupUserAccountsRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/user-accounts/list", AccountsListPage.class);
		app.mountPage(webUrlPrefix + "/user-accounts/new", NewAccountPage.class);
		app.mountPage(webUrlPrefix + "/user-accounts/edit", EditAccountPermissionsPage.class);	
	}
	
	// 
	// ------------------------------------------------------------------------
	// BACKEND ROUTES
	// ------------------------------------------------------------------------
	//

	private void setupBackendRoutes(WebApplication app) 
	{
		setupTransformersRoutes(app);
		setupPipelinesRoutes(app);
		setupOIRulesRoutes(app);
		setupQARulesRoutes(app);
		setupDNRulesRoutes(app);
	}
	
	private void setupTransformersRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/transformers/list", TransformersListPage.class);
		app.mountPage(webUrlPrefix + "/backend/transformers/new", NewTransformerPage.class);
		app.mountPage(webUrlPrefix + "/backend/transformers/edit", EditTransformerPage.class);
	}
	
	private void setupPipelinesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/pipelines/list", PipelinesListPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/new", NewPipelinePage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/edit", EditPipelinePage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/new", NewTransformerAssignmentPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/edit", EditTransformerAssignmentPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/assigned-groups/new", NewGroupAssignmentPage.class);
	}
	
	private void setupOIRulesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/oi/groups/list", OIGroupsListPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/new", NewOIGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/detail", EditOIGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/new", NewOIRulePage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/edit", EditOIRulePage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/detail", OIRuleDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/db-outputs/new", NewDBOutputPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/file-outputs/new", NewFileOutputPage.class);
	}

	private void setupQARulesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/qa/groups/list", QAGroupsListPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/new", NewQAGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/edit", EditQAGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/new", NewQARulePage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/edit", EditQARulePage.class);
	}

	private void setupDNRulesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/dn/groups/list", DNGroupsListPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/new", NewDNGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/edit", EditDNGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/rules/new", NewDNRulePage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/rules/edit", EditDNRulePage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/rules/detail", DNRuleDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/rules/components/new", NewDNRuleComponentPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/rules/components/edit", EditDNRuleComponentPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/rules/components/detail", DNRuleComponentDetailPage.class);
	}
	
	// 
	// ------------------------------------------------------------------------
	// OUTPUT-WS ROUTES
	// ------------------------------------------------------------------------
	//
	
	private void setupOutputWSRoutes(WebApplication app) 
	{
		setupAggregationPropertiesRoutes(app);
		setupLabelPropertiesRoutes(app);
	}

	private void setupLabelPropertiesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/output-ws/label-properties/list", QELabelPropertiesListPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/label-properties/new", NewQELabelPropertyPage.class);	
	}

	private void setupAggregationPropertiesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/list", AggregationSettingsPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/new", NewPropertyPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/edit", EditPropertyPage.class);	
	}

	// 
	// ------------------------------------------------------------------------
	// URL-PREFIXES ROUTES
	// ------------------------------------------------------------------------
	//
	
	private void setupURLPrefixesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/url-prefixes/list", PrefixesListPage.class);
		app.mountPage(webUrlPrefix + "/url-prefixes/new", NewPrefixPage.class);
	}
	
	// 
	// ------------------------------------------------------------------------
	// ONTOLOGIES ROUTES
	// ------------------------------------------------------------------------
	//
	
	private void setupOntologiesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/ontologies/list", OntologiesListPage.class);
		app.mountPage(webUrlPrefix + "/ontologies/new", NewOntologyPage.class);
		app.mountPage(webUrlPrefix + "/ontologies/edit", EditOntologyPage.class);
		app.mountPage(webUrlPrefix + "/ontologies/detail", OntologyDetailPage.class);
	}
	
	// 
	// ------------------------------------------------------------------------
	// MY ACCOUNT ROUTES
	// ------------------------------------------------------------------------
	//
		
	private void setupMyAccountRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/user-account/my-account", MyAccountPage.class);
		app.mountPage(webUrlPrefix + "/user-account/password", EditPasswordPage.class);
	}
}
