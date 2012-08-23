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

public class URLRouter 
{
	private String webUrlPrefix;
	
	public URLRouter(String webUrlPrefix)
	{
		this.webUrlPrefix = webUrlPrefix;
	}
	
	public void setupRouting(WebApplication app)
	{
		app.mountPage(webUrlPrefix + "/login", LogInPage.class);
		
		app.mountPage(webUrlPrefix + "/user-accounts", UserAccountsPage.class);
		app.mountPage(webUrlPrefix + "/user-accounts/list", AccountsListPage.class);
		app.mountPage(webUrlPrefix + "/user-accounts/new", NewAccountPage.class);
		app.mountPage(webUrlPrefix + "/user-accounts/edit", EditAccountPermissionsPage.class);
		
		app.mountPage(webUrlPrefix + "/backend/transformers/list", TransformersListPage.class);
		app.mountPage(webUrlPrefix + "/backend/transformers/new", NewTransformerPage.class);
		app.mountPage(webUrlPrefix + "/backend/transformers/edit", EditTransformerPage.class);
		app.mountPage(webUrlPrefix + "/backend/transformers/detail", TransformerDetailPage.class);
		
		app.mountPage(webUrlPrefix + "/backend/pipelines/list", PipelinesListPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/new", NewPipelinePage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/edit", EditPipelinePage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/detail", PipelineDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/new", NewTransformerAssignmentPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/edit", EditTransformerAssignmentPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/detail", TransformerInstanceDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/pipelines/transformer-instances/assigned-groups/new", NewGroupAssignmentPage.class);
		
		app.mountPage(webUrlPrefix + "/backend/oi/groups/list", OIGroupsListPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/new", NewOIGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/edit", EditOIGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/detail", OIGroupDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/new", NewOIRulePage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/edit", EditOIRulePage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/detail", OIRuleDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/db-outputs/new", NewDBOutputPage.class);
		app.mountPage(webUrlPrefix + "/backend/oi/groups/rules/file-outputs/new", NewFileOutputPage.class);

		app.mountPage(webUrlPrefix + "/backend/qa/groups/list", QAGroupsListPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/new", NewQAGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/edit", EditQAGroupPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/detail", QAGroupDetailPage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/new", NewQARulePage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/edit", EditQARulePage.class);
		app.mountPage(webUrlPrefix + "/backend/qa/groups/rules/detail", QARuleDetailPage.class);

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
		
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/list", CRPropertiesListPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/global/edit", EditGlobalAggregationSettingsPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/new", NewPropertyPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/aggregation-properties/edit", EditPropertyPage.class);
		
		app.mountPage(webUrlPrefix + "/output-ws/label-properties/list", QELabelPropertiesListPage.class);
		app.mountPage(webUrlPrefix + "/output-ws/label-properties/new", NewQELabelPropertyPage.class);

		app.mountPage(webUrlPrefix + "/url-prefixes/list", PrefixesListPage.class);
		app.mountPage(webUrlPrefix + "/url-prefixes/new", NewPrefixPage.class);

		app.mountPage(webUrlPrefix + "/ontologies/list", OntologiesListPage.class);
		app.mountPage(webUrlPrefix + "/ontologies/new", NewOntologyPage.class);
		app.mountPage(webUrlPrefix + "/ontologies/edit", EditOntologyPage.class);
		app.mountPage(webUrlPrefix + "/ontologies/detail", OntologyDetailPage.class);
	}
}
