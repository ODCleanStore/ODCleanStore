package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * On this page Ontology creator can see a detailed information about chosen ontology.
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.ONC })
public class OntologyDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private OntologyDao ontologyDao;

	public OntologyDetailPage(final Integer ontologyId) 
	{
		super(
			"Home > Ontologies > Detail", 
			"Show ontology detail"
		);
		
		// prepare DAO object
		//
		ontologyDao = daoLookupFactory.getDao(OntologyDao.class);
		
		// register page components
		//
		addHelpWindow(new OntologyHelpPanel("content"));
		addOntologyInformationSection(ontologyId);
	}
	
	private void addOntologyInformationSection(final Integer ontologyId)
	{
		setDefaultModel(createModelForOverview(ontologyDao, ontologyId));
		
		add(new Label("label"));
		add(new Label("description"));
		add(new Label("graphName"));
		add(new Label("definition"));
	}
}