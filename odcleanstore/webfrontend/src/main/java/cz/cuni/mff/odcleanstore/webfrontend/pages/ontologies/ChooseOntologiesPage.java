package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
		this(null);
	}
	
	public ChooseOntologiesPage(Integer sourceOntologyId) 
	{
		super(
			"Home > Ontologies > Mapping > Choose Ontologies", 
			"Ontologies mapping - choose ontologies"
		);
		
		// prepare DAO objects
		//
		this.ontologyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OntologyDao.class);
		this.sourceOntology = new Ontology();
		// register page components
		//
		addChooseOntologiesForm(sourceOntologyId);
	}
	
	private void addChooseOntologiesForm(Integer sourceOntologyId) 
	{
		Form<ChooseOntologiesPage> form = new Form<ChooseOntologiesPage>(
				"chooseOntologiesForm", new CompoundPropertyModel<ChooseOntologiesPage>(this))
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{	
				AddMappingPage page = new AddMappingPage(sourceOntology.getGraphName(),
						targetOntology != null ? targetOntology.getGraphName() : null);
				setResponsePage(page);
			}
		};
		
		DropDownChoice<Ontology> sourceSelection = createEnumSelectbox(ontologyDao, "sourceOntology");
		if (sourceOntologyId != null)
		{
			for (Ontology o : sourceSelection.getChoices()) {
				if (sourceOntologyId.equals(o.getId())) {
					sourceOntology = o;
					break;
				}
			}
		}

		form.add(sourceSelection);
		form.add(createEnumSelectbox(ontologyDao, "targetOntology", false));
		
		add(form);
	}
}
