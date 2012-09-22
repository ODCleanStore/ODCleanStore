package cz.cuni.mff.odcleanstore.webfrontend.core;

import org.apache.wicket.protocol.http.WebApplication;

import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.*;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.*;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.*;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.*;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.*;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.*;
import cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes.*;
import cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies.*;

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
		app.mountPage(webUrlPrefix + "/backend/transformers/detail", TransformerDetailPage.class);
	}
	
	private void setupPipelinesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/pipelines/list", PipelinesListPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/new", NewPipelinePage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/edit", EditPipelinePage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/detail", PipelineDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/new", NewTransformerAssignmentPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/edit", EditTransformerAssignmentPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/detail", TransformerInstanceDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/assigned-groups/new", NewGroupAssignmentPage.class);
	}
	
	private void setupOIRulesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/oi/groups/list", OIGroupsListPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/new", NewOIGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/edit", EditOIGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/detail", OIGroupDetailPage.class);
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
		app.mountPage(webUrlPrefix + "/backend/qa/groups/detail", QAGroupDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/new", NewQARulePage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/edit", EditQARulePage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/detail", QARuleDetailPage.class);
	}

	private void setupDNRulesRoutes(WebApplication app) 
	{
		app.mountPage(webUrlPrefix + "/backend/dn/groups/list", DNGroupsListPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/new", NewDNGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/edit", EditDNGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/dn/groups/detail", DNGroupDetailPage.class);
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
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/list", CRPropertiesListPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/global/edit", EditGlobalAggregationSettingsPage.class);
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
}
