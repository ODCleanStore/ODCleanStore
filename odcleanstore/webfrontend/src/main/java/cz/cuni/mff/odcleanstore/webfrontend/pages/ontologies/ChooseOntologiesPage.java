package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.ONC })
public class ChooseOntologiesPage extends FrontendPage {
	
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<Ontology> ontologyDao;
	
	private Ontology sourceOntology;
	private Ontology targetOntology;

	public ChooseOntologiesPage() 
	{
		super(
			"Home > Ontologies > Mapping > Choose Ontologies", 
			"Ontologies mapping - choose ontologies"
		);
		
		// prepare DAO objects
		//
		this.ontologyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OntologyDao.class);
		
		// register page components
		//
		addChooseOntologiesForm();
	}
	
	private void addChooseOntologiesForm() 
	{
		Form<ChooseOntologiesPage> form = new Form<ChooseOntologiesPage>(
				"chooseOntologiesForm", new CompoundPropertyModel<ChooseOntologiesPage>(this))
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{	
				AddMappingPage page = new AddMappingPage(sourceOntology,
						targetOntology != null ? targetOntology.getGraphName() : null);
				setResponsePage(page);
			}
		};
		form.add(createEnumSelectbox(ontologyDao, "sourceOntology"));
		form.add(createEnumSelectbox(ontologyDao, "targetOntology", false));
		
		add(form);
	}
}
