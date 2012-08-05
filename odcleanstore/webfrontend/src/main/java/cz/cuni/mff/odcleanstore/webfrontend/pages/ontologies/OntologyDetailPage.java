package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class OntologyDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<Ontology> ontologyDao;

	public OntologyDetailPage(final Long ontologyId) 
	{
		super("Home > Ontologies > Detail", "Ontology Detail");
		
		// prepare DAO object
		//
		ontologyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OntologyDao.class);
		// register page components
		//		
		addOntologyInformationSection(ontologyId);
	}
	
	private void addOntologyInformationSection(final Long ontologyId)
	{
		setDefaultModel(createModelForOverview(ontologyDao, ontologyId));
		
		add(new Label("label"));
		add(new Label("description"));
		add(new Label("graphName"));
		add(new Label("rdfData"));
	}
}